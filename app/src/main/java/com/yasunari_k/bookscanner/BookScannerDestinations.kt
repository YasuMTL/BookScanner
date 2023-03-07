package com.yasunari_k.bookscanner

interface BookScannerDestination {
    val route: String
}

object Main : BookScannerDestination {
    override val route = "main"
}

object AuthenticationCamera : BookScannerDestination {
    override val route = "authentication"
}

object Account : BookScannerDestination {
    override val route = "loggedIn"
}

object Borrow : BookScannerDestination {
    override val route = "borrow"
}

object Return : BookScannerDestination {
    override val route = "return"
}

// Screens to be displayed in the top RallyTabRow
val bookScannerScreens
    = listOf(Main, AuthenticationCamera, Account, Borrow, Return)