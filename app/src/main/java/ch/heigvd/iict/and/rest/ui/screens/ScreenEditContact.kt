package ch.heigvd.iict.and.rest.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import ch.heigvd.iict.and.rest.models.Contact
import ch.heigvd.iict.and.rest.navigation.AppScreens
import ch.heigvd.iict.and.rest.ui.TopBar

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ScreenEditContact(navController : NavHostController, contact : Long?) {
    Scaffold(
        topBar = { TopBar(currentScreen = AppScreens.EditContact,
            canNavigateBack = true,
            onNavigateBack = { navController.navigateUp() }) }
    ) {
        Column() {
            Text(text = "new contact")
            nameTextFieldItem(name = "Cux", placeHolder = "Name", null)
        }
    }
}

@Composable
fun nameTextFieldItem(name : String, placeHolder : String, value : String?) {
    Row(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = name)
        if (value != null) {
            TextField(value = "", onValueChange = {}, placeholder = { Text(text = placeHolder!!) })
        } else {
            TextField(value = "", placeholder = { Text(text = placeHolder!!) }, onValueChange = {})
        }
    }
}