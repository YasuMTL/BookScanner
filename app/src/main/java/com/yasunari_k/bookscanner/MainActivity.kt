package com.yasunari_k.bookscanner

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.mlkit.vision.barcode.common.Barcode
import com.yasunari_k.bookscanner.ui.account.LoggedInScreen
import com.yasunari_k.bookscanner.ui.main.MainScreen
import com.yasunari_k.bookscanner.ui.returns.ReturnScreen
import com.yasunari_k.bookscanner.ui.theme.BookScannerTheme

class MainActivity : ComponentActivity() {
    private var shouldShowCamera: MutableState<Boolean> = mutableStateOf(false)
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
    modifier: Modifier = Modifier
) {
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
                onImageCapturedAndCorrectCode = {
                    Log.d("AuthenticationCamera","Scanned QR Code is conformed!")
                    navController
                        .navigateSingleTopTo(LoggedIn.route)
                },
                onImageCapturedButNotCorrectCode = {
                    Log.d("AuthenticationCamera","Scanned QR Code isn't conformed!")
                    navController.popBackStack()
                }
            )
        }
        composable(route = LoggedIn.route) {
            LoggedInScreen()
        }
        composable(route = Borrow.route) {
            ScanView(
                Barcode.TYPE_ISBN,
                onImageCapturedAndCorrectCode = {
//                    navController
//                        .navigateSingleTopTo(LoggedIn.route)
                    Log.d("BorrowScreen", "Fetch an ISBN number successfully!")
                },
                onImageCapturedButNotCorrectCode = {
                    Log.d("BorrowScreen", "Scanned Barcode doesn't contain ISBN number")
                    //Toast.makeText(LocalContext.current, "Scanned Barcode doesn't contain ISBN number", Toast.LENGTH_SHORT).show()
                }
            )
        }
        composable(route = Return.route) {
            ReturnScreen()
        }
    }
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