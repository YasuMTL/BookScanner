package com.yasunari_k.bookscanner

import android.accounts.Account
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.SheetsScopes
import com.google.api.services.sheets.v4.model.*
import com.google.mlkit.vision.barcode.common.Barcode
import com.yasunari_k.bookscanner.ui.UserInfoViewModel
import com.yasunari_k.bookscanner.ui.account.LoggedInScreen
import com.yasunari_k.bookscanner.ui.main.MainScreen
import com.yasunari_k.bookscanner.ui.returns.ReturnScreen
import com.yasunari_k.bookscanner.ui.theme.BookScannerTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okio.IOException
import java.text.SimpleDateFormat
import java.util.*

private const val TAG = "MainActivity"
class MainActivity : ComponentActivity() {
    private lateinit var mAccount: Account
    private var shouldShowCamera: MutableState<Boolean> = mutableStateOf(false)

    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest
    val REQ_ONE_TAP = 111//For now
    val REQ_USER_AUTHRIZATION = 222
    private var showOneTapUI = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            BookScannerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    color = MaterialTheme.colors.background,
                ) {
                    BookScannerApp()
                }
            }
        }

        requestCameraPermission()

        //todo: Check for a signed-in user -> https://developers.google.com/identity/one-tap/android/get-saved-credentials#2_check_for_a_signed-in_user
        val account = GoogleSignIn.getLastSignedInAccount(this)
        account?.let {
            Log.i(TAG, "displayName = ${account.displayName.toString()}")
        }
        val isUserSignedOut = true
        val hasUserNotDeclinedOneTapSignIn = true

        configureOneTapClient()

        if (hasUserNotDeclinedOneTapSignIn) {
            if (isUserSignedOut) {
                displayOneTapSignInUi()
            }
        }
    }

    fun writeLoginInfo(
        credentialState: StateFlow<GoogleAccountCredential?>,
        borrowerName: String,
        bookName: String,
        borrowerEmail: String
    ) {
        // サービスのスコープとしてSpreadSheetsのRead/Writeを設定
        val scopes = listOf(SheetsScopes.SPREADSHEETS)
        // サービス呼び出し
        val service = Sheets.Builder(
            NetHttpTransport(),
            GsonFactory.getDefaultInstance(), credentialState.value)
            .setApplicationName("Book Scanner")
            .build()

        Log.d(TAG, credentialState.value.toString())

        // 複数範囲への書き込み処理を実行
        try{
            val spreadsheetId = "1wj15p6XhNNphsMXYP8xQq4ftrxCCjG8mCA-ufPM_ukE"

            // 二次元配列で書き込む値を保持
            val values: List<List<Any>> = listOf(
                listOf(borrowerName, bookName, borrowerEmail, getCurrentDate())
            )

            val body = ValueRange().setValues(values)

            // 書き込み。
            val result: AppendValuesResponse =
                service.spreadsheets()
                    .values()
                    // appendは一番下の行に追加していってくれる
                    .append(spreadsheetId, "TestSheet!A:B", body)
                    // RAWを指定すると値がそのまま表示される。USER_ENTEREDだと数字や日付の書式が手入力時と同じになるらしい
                    .setValueInputOption("RAW")
                    .execute()
        }catch (e: java.io.IOException){
            // 例外処理
            Log.d("updatedRows", "error = $e")

            if (e is UserRecoverableAuthIOException) {
                Log.d("updatedRows", "e is UserRecoverableAuthIOException")
                startActivityForResult(e.getIntent(), REQ_USER_AUTHRIZATION)
            } else {
                // other cases
                Log.d("updatedRows", "e is not UserRecoverableAuthIOException")
            }
        }catch (e: UserRecoverableAuthIOException) {
            Log.d("updatedRows", "error = $e")
        }
    }

    private fun getCurrentDate(): String {
        val time = Calendar.getInstance().time
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm")
        val current = formatter.format(time)

        return current
    }

    private fun deleteRow(credentialState: StateFlow<GoogleAccountCredential?>) {
        val tag = "deleteRow"
        val service = Sheets.Builder(
            NetHttpTransport(),
            GsonFactory.getDefaultInstance(), credentialState.value)
            .setApplicationName("Book Scanner")
            .build()

        // 複数範囲への書き込み処理を実行
        try{
            val spreadsheetId = "1wj15p6XhNNphsMXYP8xQq4ftrxCCjG8mCA-ufPM_ukE"

            val deleteDimensionsRequest = DeleteDimensionRequest()
            val dimensionRange = DimensionRange().apply {
                dimension = "ROWS"
                startIndex = 0
                endIndex = 2
            }
            deleteDimensionsRequest.range = dimensionRange
            val deleteRequest = Request().setDeleteDimension(deleteDimensionsRequest)
            val batchUpdateSpreadsheetRequest = BatchUpdateSpreadsheetRequest()
            batchUpdateSpreadsheetRequest.requests = listOf(deleteRequest)

            val deleteResult = service.spreadsheets()
                .batchUpdate(spreadsheetId, batchUpdateSpreadsheetRequest)

            deleteResult.execute()
            Log.d(tag, "delete request was executed.")
        } catch (e: java.io.IOException) {
            // 例外処理
            Log.d(tag, "error = $e")

            if (e is UserRecoverableAuthIOException) {
                Log.d(tag, "e is UserRecoverableAuthIOException")
                startActivityForResult(e.getIntent(), REQ_USER_AUTHRIZATION)
            } else {
                // other cases
                Log.d(tag, "e is not UserRecoverableAuthIOException")
            }
        }catch (e: UserRecoverableAuthIOException) {
            Log.d(tag, "error = $e")
        }
    }

    private fun displayOneTapSignInUi() {
        val TAG = "displayOneTapSignInUI"

        oneTapClient.beginSignIn(signInRequest)
            .addOnSuccessListener(this) { result ->
                try {
                    startIntentSenderForResult(
                        result.pendingIntent.intentSender, REQ_ONE_TAP,
                        null, 0, 0, 0, null)
                } catch (e: IntentSender.SendIntentException) {
                    Log.e(TAG, "Couldn't start One Tap UI: ${e.localizedMessage}")
                }
            }
            .addOnFailureListener(this) { e ->
                // No saved credentials found. Launch the One Tap sign-up flow, or
                // do nothing and continue presenting the signed-out UI.
                e.localizedMessage?.let { Log.d(TAG, it) }
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val TAG = "onActivityResult"

        when (requestCode) {
            REQ_ONE_TAP -> {
                try {
                    //val credential = oneTapClient.getSignInCredentialFromIntent(data)
                    val credential = oneTapClient.getSignInCredentialFromIntent(data)
                    val idToken = credential.googleIdToken
                    val username = credential.id
                    val password = credential.password

                    when {
                        idToken != null -> {
                            // Got an ID token from Google. Use it to authenticate
                            // with your backend.
                            verifyIdToken(idToken)
                            //createAccountFromCredentialId(username)
                            Log.d(TAG, "Got ID token = $idToken")

                            mAccount = Account(username, packageName)
                            Log.d(TAG, "mAccount saved")
                        }
                        password != null -> {
                            // Got a saved username and password. Use them to authenticate
                            // with your backend.
                            Log.d(TAG, "Got password.")
                        }
                        else -> {
                            // Shouldn't happen.
                            Log.d(TAG, "No ID token or password!")
                        }
                    }
                } catch (e: ApiException) {
                    when (e.statusCode) {
                        CommonStatusCodes.CANCELED -> {
                            Log.d(TAG, "One-tap dialog was closed.")
                            // Don't re-prompt the user.
                            showOneTapUI = false
                        }
                        CommonStatusCodes.NETWORK_ERROR -> {
                            Log.d(TAG, "One-tap encountered a network error.")
                            // Try again or just ignore.
                        }
                        else -> {
                            Log.d(TAG, "Couldn't get credential from result." +
                                    " (${e.localizedMessage})")
                        }
                    }
                }
            }

            REQ_USER_AUTHRIZATION -> {
                Log.d("$TAG: REQ_USER_AUTHRIZATION", data.toString())
            }
        }
    }

    fun verifyIdToken(idTokenString: String) {
        val transport = NetHttpTransport()
        val jsonFactory = GsonFactory()
        val CLIENT_ID = "976682064008-fvfvi239e45m2pp5nr8npbv7anb9n0u9.apps.googleusercontent.com"

        val verifier = GoogleIdTokenVerifier.Builder(
            transport,
            jsonFactory
        ) // Specify the CLIENT_ID of the app that accesses the backend:
            .setAudience(Collections.singletonList(CLIENT_ID)) // Or, if multiple clients access the backend:
            //.setAudience(Arrays.asList(CLIENT_ID_1, CLIENT_ID_2, CLIENT_ID_3))
            .build()

        // (Receive idTokenString by HTTPS POST)

        val idToken = verifier.verify(idTokenString)
        if (idToken != null) {
            val payload: GoogleIdToken.Payload = idToken.payload

            // Print user identifier
            val userId: String = payload.getSubject()
            println("User ID: $userId")

            // Get profile information from payload
//            val email: String = payload.getEmail()
//            val emailVerified: Boolean = java.lang.Boolean.valueOf(payload.getEmailVerified())
//            val name = payload.get("name")
//            val pictureUrl = payload.get("picture")
//            val locale = payload.get("locale")
//            val familyName = payload.get("family_name")
//            val givenName = payload.get("given_name")

            // Use or store profile information
            // ...
        } else {
            println("Invalid ID token.")
        }
    }

    private fun configureOneTapClient() {
        val yourWebServerClientId = "976682064008-guhrb4bp4r9h1e9ke5dcu4fj2tpuoqlr.apps.googleusercontent.com"

        oneTapClient = Identity.getSignInClient(this)
        signInRequest = BeginSignInRequest.builder()
            .setPasswordRequestOptions(BeginSignInRequest.PasswordRequestOptions.builder()
                .setSupported(true)
                .build())
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    // Your server's client ID, not your Android client ID.
                    .setServerClientId(yourWebServerClientId)
                    // Only show accounts previously used to sign in.
//                    .setFilterByAuthorizedAccounts(true)
                    .setFilterByAuthorizedAccounts(false)
                    .build())
            // Automatically sign in when exactly one credential is retrieved.
            .setAutoSelectEnabled(true)
            .build()
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS =
            mutableListOf (
                android.Manifest.permission.CAMERA
            ).toTypedArray()
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                //startCamera()
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun requestCameraPermission() {
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.CAMERA
            ) -> {
                Log.i("MainActivity", "Permission previously granted")
                shouldShowCamera.value = true // 👈🏽
            }
            else -> {
                Log.i("MainActivity", "Permission not granted yet")
                ActivityCompat.requestPermissions(
                    this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
                )
            }
        }
    }

    @Composable
    fun BookScannerApp() {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val navController = rememberNavController()

            val currentBackStack by navController.currentBackStackEntryAsState()
            // Fetch your currentDestination:
            val currentDestination = currentBackStack?.destination

            // Change the variable to this and use Overview as a backup screen if this returns null
            val currentScreen = bookScannerScreens.find { it.route == currentDestination?.route } ?: Main

            Scaffold { innerPadding ->
                BookScannerNavHost(
                    navController = navController,
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }

    @Composable
    fun BookScannerNavHost(
        navController: NavHostController,
        modifier: Modifier = Modifier,
        userInfoViewModel: UserInfoViewModel
        = viewModel(factory = UserInfoViewModel.provideViewModelFactory(Repository()))//Want to use hilt or something to provide Repository instance here
    ) {
        val context = LocalContext.current
        val coroutineScope = rememberCoroutineScope()

        NavHost(
            navController = navController,
            startDestination = Main.route,
            modifier = modifier
        ) {
            composable(route = Main.route) {
                MainScreen(
                    onClickAuth = {
                        navController
                            .navigateSingleTopTo(AuthenticationCamera.route)
                    }
                )
            }
            composable(route = AuthenticationCamera.route) {
                ScanView(
                    Barcode.FORMAT_QR_CODE,
                    onImageCaptured = { borrowerInfo ->
                        val isDataConformed = userInfoViewModel.isQrCodeConformed(borrowerInfo)
                        if (isDataConformed) {
                            Log.d("AuthenticationCamera","Main -----------> LoggedIn")
                            userInfoViewModel.updateBorrowerInfo(borrowerInfo)
                            userInfoViewModel.resetBookInfo()

                            //TODO: Google Sign-in for using Sheets API
                            userInfoViewModel.saveCredentials(context, mAccount)

                            coroutineScope.launch {
                                withContext(Dispatchers.IO) {
                                    deleteRow(userInfoViewModel.credentialState)
                                }
                            }

                            navController.navigateSingleTopTo(LoggedIn.route)
                        } else {
                            showToast(context, "Scanned QR code is not conformed. Try with another one.")
                            navController.navigateSingleTopTo(Main.route)
                        }
                    }
                )
            }
            composable(route = LoggedIn.route) {
                LoggedInScreen(
                    onClickBorrow = {
                        Log.d("LoggedInScreen","Need to scan a barcode for an ISBN number")
                        navController
                            .navigateSingleTopTo(Borrow.route)
                    },
                    onClickReturn = {
                        Log.d("LoggedInScreen","Need to show Borrowed Book List")
                        navController
                            .navigateSingleTopTo(Return.route)
                    },
                    onClickLogout = {
                        Log.d("LoggedInScreen","Logout and get back to MainScreen")
                        userInfoViewModel.resetInfo()
                        navController
                            .navigateSingleTopTo(Main.route)
                    },
                    borrowerState = userInfoViewModel.bookBorrower.collectAsState(),
                    borrowedBookState = userInfoViewModel.bookInfoInMemory.collectAsState()
                )
            }
            composable(route = Borrow.route) {
                ScanView(
                    Barcode.FORMAT_EAN_13,
                    onImageCaptured = { isbnCode ->
                        if(userInfoViewModel.isIsbnCodeFormatConformed(isbnCode)) {
                            try {
                                coroutineScope.launch {
                                    withContext(Dispatchers.IO) {
                                        userInfoViewModel.updateBookInfo(isbnCode)
                                        Log.d(TAG, "Scanned Barcode is conformed. Now we will fetch the book info with the ISBN code.")

                                        writeLoginInfo(
                                            userInfoViewModel.credentialState,
                                            bookName = userInfoViewModel.bookInfoInMemory.value.title,
                                            borrowerName = userInfoViewModel.bookBorrower.value.name,
                                            borrowerEmail = userInfoViewModel.bookBorrower.value.emailAddress
                                        )//todo: title is still empty
                                        Log.d(TAG, "title = ${userInfoViewModel.bookInfoInMemory.value.title}")
                                    }
                                }
                            } catch (e: IOException) {
                                if(e is UserRecoverableAuthIOException) {
                                    Log.d(TAG, e.stackTrace.toString())
                                    startActivityForResult(e.intent, REQ_USER_AUTHRIZATION)
                                }
                            }
                        } else {
                            showToast(context, "Scanned Barcode doesn't contain ISBN code. Please try again.")
                        }

                        navController
                            .navigateSingleTopTo(LoggedIn.route)
                    }
                )
            }
            composable(route = Return.route) {
                ReturnScreen(
                    onClickBackButton = {
                        /*todo: How to get back to LoggedIn screen with user information? */
                        showToast(context, "Return screen --> LoggedIn Screen")
                        navController
                            .navigateSingleTopTo(LoggedIn.route)
                    }
                )
            }
        }
    }

    private fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun NavHostController.navigateSingleTopTo(route: String) =
        this.navigate(route) {
            popUpTo(
                this@navigateSingleTopTo.graph.findStartDestination().id
            ) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }

}//End of MainActivity()