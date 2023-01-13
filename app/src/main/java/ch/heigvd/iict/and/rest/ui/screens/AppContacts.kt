package ch.heigvd.iict.and.rest.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ch.heigvd.iict.and.rest.ContactsApplication
import ch.heigvd.iict.and.rest.models.Contact
import ch.heigvd.iict.and.rest.navigation.AppScreens
import ch.heigvd.iict.and.rest.viewmodels.ContactsViewModel
import ch.heigvd.iict.and.rest.viewmodels.ContactsViewModelFactory

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun AppContact(application: ContactsApplication, contactsViewModel : ContactsViewModel = viewModel(factory= ContactsViewModelFactory(application))) {
    val context = LocalContext.current
    val contacts : List<Contact> by contactsViewModel.allContacts.observeAsState(initial = emptyList())
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = AppScreens.Contacts.name) {
        composable(AppScreens.Contacts.name) {
            ScreenContactList(
                contacts = contacts,
                navController = navController,
                onPopulate = {contactsViewModel.enroll()},
                onSynchronize = {contactsViewModel.refresh()}
            )
        }

        composable(AppScreens.EditContact.name) {
            ScreenEditContact(navController = navController, contact = Contact())
        }

    }

}