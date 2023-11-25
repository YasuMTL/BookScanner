package com.yasunari_k.bookscanner

object SheetsQuickstart {
    const val UNLOCK_LOG_SPREADSHET_SHEETNAME = "TestSheet"
    const val UNLOCK_LOG_SPREADSHEET_ID = "0"
}

//object SheetsQuickstart {
//    private const val APPLICATION_NAME = "Google Sheets API Java Quickstart"
//    private val JSON_FACTORY: JsonFactory = GsonFactory.getDefaultInstance()
//    private const val TOKENS_DIRECTORY_PATH = "tokens"
//
//    /**
//     * Global instance of the scopes required by this quickstart.
//     * If modifying these scopes, delete your previously saved tokens/ folder.
//     */
//    private val SCOPES = listOf(SheetsScopes.SPREADSHEETS_READONLY)
//    private const val CREDENTIALS_FILE_PATH = "assets/credentials.json"
//
//    /**
//     * Creates an authorized Credential object.
//     *
//     * @param HTTP_TRANSPORT The network HTTP Transport.
//     * @return An authorized Credential object.
//     * @throws IOException If the credentials.json file cannot be found.
//     */
//    @Throws(IOException::class)
//    private fun getCredentials(HTTP_TRANSPORT: NetHttpTransport, context: Context): Credential {
//        // Load client secrets.
//        //val `in` = SheetsQuickstart::class.java.getResourceAsStream(CREDENTIALS_FILE_PATH)
//        val `in` = context.assets.open("credentials.json")
//            ?: throw FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH)
//        val clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, InputStreamReader(`in`))
//
//        val tokenFolder = File("${context.externalCacheDir}" +
//                File.separator + TOKENS_DIRECTORY_PATH)
//        // Build flow and trigger user authorization request.
//        val flow = GoogleAuthorizationCodeFlow.Builder(
//            HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES
//        )
//            //.setDataStoreFactory(FileDataStoreFactory(File(TOKENS_DIRECTORY_PATH)))
//            .setDataStoreFactory(FileDataStoreFactory(tokenFolder))
//            .setAccessType("offline")
//            .build()
//        val receiver = LocalServerReceiver.Builder().setPort(8888).build()
//        return AuthorizationCodeInstalledApp(flow, receiver).authorize("user")
//    }
//
////    private fun getCredentials(context: Context): GoogleAccountCredential {
////        val signedInAccount: GoogleSignInAccount? = GoogleSignIn.getLastSignedInAccount(context)
////        val account: Account? = signedInAccount?.account
////        val credential: GoogleAccountCredential = GoogleAccountCredential.usingOAuth2(
////            context,
////            Collections.singleton("https://www.googleapis.com/auth/spreadsheets")
////        )
////        credential.selectedAccount = account
////
////        return credential
////    }
//    /**
//     * Prints the names and majors of students in a sample spreadsheet:
//     * https://docs.google.com/spreadsheets/d/1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms/edit
//     */
//    //public static void main(String... args) throws IOException, GeneralSecurityException {
//    @Throws(GeneralSecurityException::class, IOException::class)
//    fun test(context: Context) {
//        // Build a new authorized API client service.
//        val HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport()
//        //        final String spreadsheetId = "1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms";
//        val spreadsheetId = "1wj15p6XhNNphsMXYP8xQq4ftrxCCjG8mCA-ufPM_ukE"
//        val range = "Class Data!A1:B1"
//        val service = Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, /*getCredentials(context)*/getCredentials(HTTP_TRANSPORT, context))
//            .setApplicationName(APPLICATION_NAME)
//            .build()
//        val response = service.spreadsheets().values()[spreadsheetId, range]
//            .execute()
//        val values = response.getValues()
//        if (values == null || values.isEmpty()) {
//            println("No data found.")
//        } else {
//            println("Name, Major")
//            for (row in values) {
//                // Print columns A and E, which correspond to indices 0 and 4.
//                System.out.printf("%s, %s\n", row[0], row[4])
//            }
//        }
//    }
//}