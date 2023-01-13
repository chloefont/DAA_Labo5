package ch.heigvd.iict.and.rest.ui.screens

import android.annotation.SuppressLint
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import ch.heigvd.iict.and.rest.models.Contact
import ch.heigvd.iict.and.rest.navigation.AppScreens
import ch.heigvd.iict.and.rest.ui.TopBar

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ScreenEditContact(navController : NavHostController, contact : Contact?) {
    Scaffold(
        topBar = { TopBar(currentScreen = AppScreens.EditContact,
            canNavigateBack = true,
            onNavigateBack = { navController.navigateUp() }) }
    ) {
        Text(text = "new contact")
    }
}