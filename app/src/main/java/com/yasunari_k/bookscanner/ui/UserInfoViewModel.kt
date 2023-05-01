package com.yasunari_k.bookscanner.ui

import android.accounts.Account
import android.content.Context
import android.text.format.DateFormat
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.SheetsScopes
import com.google.api.services.sheets.v4.model.BatchUpdateValuesRequest
import com.google.api.services.sheets.v4.model.ValueRange
import com.google.gson.Gson
import com.yasunari_k.bookscanner.Repository
import com.yasunari_k.bookscanner.SheetsQuickstart
import com.yasunari_k.bookscanner.model.Book
import com.yasunari_k.bookscanner.model.BookBorrower
import com.yasunari_k.bookscanner.model.Info
import com.yasunari_k.bookscanner.network.SheetApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.*

private const val TAG = "UserInfoViewModel"
class UserInfoViewModel(private val repository: Repository): ViewModel() {
    private val _bookBorrower = MutableStateFlow(
        BookBorrower(
            name = "",
            emailAddress = "",
            registerDate = ""
        )
    )
    val bookBorrower: StateFlow<BookBorrower> = _bookBorrower.asStateFlow()

    private val _bookInfoInMemory = MutableStateFlow(Info(title = "", authors = emptyList(), publishedDate = "", description = ""))
    val bookInfoInMemory: StateFlow<Info> = _bookInfoInMemory.asStateFlow()

    private val _credentialState = MutableStateFlow<GoogleAccountCredential?>(null)
    val credentialState: StateFlow<GoogleAccountCredential?> = _credentialState.asStateFlow()

    fun updateBorrowerInfo(borrowerInfo: String) {
        if (!isQrCodeConformed(borrowerInfo)) {
            return
        }

        viewModelScope.launch {
            val borrowerInfoMap = convertFetchedInfoToUserData(borrowerInfo)
            Log.d("ViewModel","Convert fetched info to user data")
            updateBorrower(borrowerInfoMap)
            Log.d("ViewModel","Update is done!")
        }
    }

    private fun updateBorrower(borrowerInfoMap: Map<String, String>) {
        _bookBorrower.update {
            it.copy(
                name = borrowerInfoMap["name"] ?: "",
                emailAddress = borrowerInfoMap["email"] ?: "",
                registerDate = borrowerInfoMap["registeredDate"] ?: ""
            )
        }
        Log.d("updateBorrower", "BorrowerName=${borrowerInfoMap["name"]}")
    }

    fun isQrCodeConformed(dataFromQrCode: String): Boolean {
        return dataFromQrCode.contains("name") &&
                dataFromQrCode.contains("date") &&
                dataFromQrCode.contains("email")
    }

    fun isIsbnCodeFormatConformed(codeToCheck: String): Boolean {
        return codeToCheck.startsWith("978")
    }

    suspend fun updateBookInfo(isbnCode: String) {
        val response: Book = repository.fetchInfo(isbnCode)
        val noDataAvailable = "No Data Available"

        if(response.kind == noDataAvailable) {
            _bookInfoInMemory.update {
                it.copy(
                    title = "Not Found",
                    authors = emptyList(),
                    publishedDate = "",
                    description = "API couldn't fetch info with the scanned data."
                )
            }
            Log.d("ViewModel", "Response is empty")
        } else {
            val bookInfo = extractInfoFromBook(response)
            _bookInfoInMemory.update {
                it.copy(
                    title = bookInfo.title,
                    authors = bookInfo.authors,
                    publishedDate = bookInfo.publishedDate,
                    description = bookInfo.description
                )
            }
            Log.d("ViewModel", "Response: $response")

            //todo: Try to access a Google Sheet
            withContext(Dispatchers.IO) {
                launch {
                    try {
                        writeLoginInfo()
                    } catch (e: IOException) {
                        Log.d(TAG, e.message.toString())
                        throw e
                    } catch (e: Exception) {
                        Log.d(TAG, e.message.toString())
                        throw e
                    }
                }
            }
        }
    }

    fun confirmBorrowBook() {
        //TODO: Send book info to a given Google Sheet
        val title = bookInfoInMemory.value.title
        Log.d("confirmBorrowBook", "The function has been called! title=$title")
    }

    fun sendGetRequest() {
        viewModelScope.launch {
            //val result = SheetApi.retrofitService.getSomething("test")
            val result = SheetApi.retrofitService.getSomething()

            Log.d("SendGetRequest", "result=$result")
        }
    }

    fun extractInfoFromBook(book: Book):Info {
        Log.d("extractInfoFromBook", "book=$book")
        val bookInfo = book.items.first().volumeInfo
        val title = bookInfo.title
        val authors = bookInfo.authors
        val publishedDate = bookInfo.publishedDate
        val description = bookInfo.description

        return Info(title, authors, publishedDate, description)
    }

    fun convertFetchedInfoToUserData(scannedJsonData: String): Map<String, String> {
//        Log.d(TAG, "scannedJsonData:$scannedJsonData")
        val fetchedBookBorrower = Gson().fromJson(scannedJsonData, BookBorrower::class.java)

        val borrowerInfoMap= mutableMapOf(
            "name" to fetchedBookBorrower.name,
            "email" to fetchedBookBorrower.emailAddress,
            "registerdDate" to fetchedBookBorrower.registerDate,
        )

        Log.d("BookBorrower",
            "name=${fetchedBookBorrower.name}, " +
                    "email=${fetchedBookBorrower.emailAddress}, " +
                    "registeredDate=${fetchedBookBorrower.registerDate}"
        )

        return borrowerInfoMap
    }

    fun resetInfo() {
        viewModelScope.launch {
            _bookBorrower.update {
                it.copy(name = "", emailAddress = "", registerDate = "")
            }
            _bookInfoInMemory.update {
                it.copy(title = "", authors = emptyList(), publishedDate = "", description = "")
            }
        }
    }

    fun resetBookInfo() {
        viewModelScope.launch {
            _bookInfoInMemory.update {
                it.copy(title = "", authors = emptyList(), publishedDate = "", description = "")
            }
        }
    }

    fun saveCredentials(context: Context, account: Account) {
        val credential: GoogleAccountCredential = GoogleAccountCredential.usingOAuth2(
            context,
            Collections.singleton("https://www.googleapis.com/auth/spreadsheets")
        ).setSelectedAccount(account)

        _credentialState.value = credential
        Log.d(TAG, "mCredential = $credential")
    }

    fun writeLoginInfo() {
        // サービスのスコープとしてSpreadSheetsのRead/Writeを設定
        val scopes = listOf(SheetsScopes.SPREADSHEETS)
        // GoogleAccountCredentialを生成
        // Google Sign-in API実装にて取得したGoogleAccount情報を利用。（社員以外は利用できないように）
//        credential.selectedAccount = account
        // サービス呼び出し
        val service = Sheets.Builder(
            NetHttpTransport(),
            GsonFactory.getDefaultInstance(), _credentialState.value)
            .setApplicationName("Book Scanner")
            .build()
        Log.d(TAG, _credentialState.value.toString())
        // 書き込む内容を設定
        val rows = listOf<ValueRange>(
            ValueRange()
                .setRange("'${SheetsQuickstart.UNLOCK_LOG_SPREADSHET_SHEETNAME}'!A1") // 書き込み先の「シート名!セル」を指定
                .setValues(listOf(
//                    listOf<Any>(mACCOUNT!!.email.toString()) // サインインしたGoogleAccountのメールアドレスを書き込む
                    //listOf<Any>(account.email.toString()) // サインインしたGoogleAccountのメールアドレスを書き込む
                    listOf<Any>("Test 123")
                )),
            ValueRange()
                .setRange("'${SheetsQuickstart.UNLOCK_LOG_SPREADSHET_SHEETNAME}'!B1") // 書き込み先の「シート名!セル」を指定
                .setValues(listOf(
                    listOf<Any>(DateFormat.format("yyyy/MM/dd kk:mm:ss", Date()).toString()) // 日時を書き込む
                ))
        )
        // 複数範囲への書き込みリクエストの作成
        val body = BatchUpdateValuesRequest()
            .setValueInputOption("RAW")
            .setData(rows)

        // 複数範囲への書き込み処理を実行
        try{
            val result = service.spreadsheets().values().batchUpdate(SheetsQuickstart.UNLOCK_LOG_SPREADSHEET_ID, body).execute()
            Log.d("updatedRows", "${result.totalUpdatedRows}")
        }catch (e: java.io.IOException){
            // 例外処理
            Log.d("updatedRows", "error = $e")

            if (e is UserRecoverableAuthIOException) {
                Log.d("updatedRows", "e is UserRecoverableAuthIOException")
                throw e
                //myActivity.startActivityForResult(e.getIntent(), MyActivity.REQUEST_AUTHORIZATION);
            } else {
                // other cases
                Log.d("updatedRows", "e is not UserRecoverableAuthIOException")
            }
        }catch (e: UserRecoverableAuthIOException) {
            Log.d("updatedRows", "error = $e")
//            if (e is UserRecoverableAuthIOException) {
//                myActivity.startActivityForResult(e.getIntent(), MyActivity.REQUEST_AUTHORIZATION);
//            } else {
//                // other cases
//            }
        }
    }

    // Define ViewModel factory in a companion object
    companion object {
        fun provideViewModelFactory(
            repository: Repository
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(UserInfoViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return UserInfoViewModel(
                        repository = repository
                    ) as T
                }
                throw IllegalArgumentException("Unknown ViewModel Class")
            }
        }
    }
}