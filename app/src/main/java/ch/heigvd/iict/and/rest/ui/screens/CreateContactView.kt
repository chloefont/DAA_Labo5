package ch.heigvd.iict.and.rest.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import ch.heigvd.iict.and.rest.R
import ch.heigvd.iict.and.rest.navigation.AppScreens
import ch.heigvd.iict.and.rest.ui.TopBar

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun CreateContactView() {
    Scaffold(
        topBar = {
            TopBar(currentScreen = AppScreens.NewContact, canNavigateBack = true, onNavigateBack = { /*TODO*/ })
        }
    ) {
        Text(text = "coucou")
    }
}