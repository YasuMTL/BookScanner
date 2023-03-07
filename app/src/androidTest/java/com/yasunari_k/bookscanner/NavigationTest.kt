package com.yasunari_k.bookscanner

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class NavigationTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    lateinit var navController: TestNavHostController

    @Before
    fun setupBookScannerNavHost() {
        composeTestRule.setContent {
            navController =
                TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(
                ComposeNavigator()
            )
            BookScannerNavHost(navController = navController)
        }
    }

    @Test
    fun bookScannerNavHost_verifyMainDisplayedAsStartDestination() {
        composeTestRule
            .onNodeWithContentDescription("Main Screen")
            .assertIsDisplayed()
    }

    @Test
    fun bookScannerNavHost_clickAuthButton_navigatesToCameraView() {
        composeTestRule
            .onNodeWithContentDescription("Show Camera View")
            .performClick()

        composeTestRule
            .onNodeWithContentDescription("Camera View")
            .assertIsDisplayed()
    }
}