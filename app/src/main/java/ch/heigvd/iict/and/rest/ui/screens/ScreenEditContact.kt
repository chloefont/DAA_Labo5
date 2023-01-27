package ch.heigvd.iict.and.rest.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material.RadioButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Create
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import ch.heigvd.iict.and.rest.models.Contact
import ch.heigvd.iict.and.rest.models.PhoneType
import ch.heigvd.iict.and.rest.navigation.AppScreens
import ch.heigvd.iict.and.rest.ui.TopBar
import ch.heigvd.iict.and.rest.viewmodels.ContactsViewModel
import java.util.Calendar

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ScreenEditContact(navController: NavHostController, contact: Long?, contactsViewModel: ContactsViewModel) {
    val contacts: List<Contact> by contactsViewModel.allContacts.observeAsState(initial = emptyList())
    var currentContact : Contact? = null
    if (contact != null) {
        currentContact = contacts.find { it.id == contact!! }
    }

    val (selected, setSelected) = remember { mutableStateOf("")}

        val nameValue = currentContact?.name
        val firstnameValue = currentContact?.firstname
        val emailValue = currentContact?.email
        val birthdayValue = currentContact?.birthday ?: Calendar.getInstance()
        val addressValue = currentContact?.address
        val zipValue = currentContact?.zip
        val cityValue = currentContact?.city
        val phonetypeValue = currentContact?.type ?: PhoneType.MOBILE
        val phonenumberValue = currentContact?.phoneNumber

        Scaffold(
            topBar = {
                TopBar(currentScreen = AppScreens.EditContact,
                    canNavigateBack = true,
                    onNavigateBack = { navController.navigateUp() })
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp, 0.dp)
                    .verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(text = "New contact")
                textFieldItem(name = "Name", placeHolder = "Name", value = nameValue)
                textFieldItem(name = "Firstname", placeHolder = "Firstname", firstnameValue)
                textFieldItem(name = "Email", placeHolder = "Email", emailValue)
                textFieldItem(name = "Birthday", placeHolder = "Birthday", "birthdayValue")
                textFieldItem(name = "Address", placeHolder = "Address", addressValue)
                textFieldItem(name = "Zip", placeHolder = "Zip", zipValue)
                textFieldItem(name = "City", placeHolder = "City", cityValue)
                RadioGroup(
                    items = listOf("Home", "Mobile", "Office", "Fax"),
                    selected = selected,
                    setSelected = setSelected,
                    title = "Phone type"
                )
                textFieldItem(name = "Phone number", placeHolder = "Phone number", null)

                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Button(onClick = { /*TODO*/ }) {
                        Text(text = "Cancel")
                    }

                    Button(onClick = { /*TODO*/ }) {
                        Icon(Icons.Outlined.Delete, contentDescription = "Delete")
                        Text(text = "Delete")
                    }

                    Button(onClick = { /*TODO*/ }) {
                        Icon(Icons.Outlined.Create, contentDescription = "Save")
                        Text(text = "Save")
                    }
                }
            }

        }
}

@Composable
fun textFieldItem(name : String, placeHolder : String, value : String?) {
    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier= Modifier
        .wrapContentSize(Alignment.Center)
        .fillMaxWidth()) {
        Text(text = name, modifier= Modifier
            .wrapContentHeight()
            .align(Alignment.CenterVertically))
        val finalValue = value ?: ""
        TextField(value = finalValue, onValueChange = {}, placeholder = { Text(text = placeHolder!!) })
    }
}

@Composable
fun RadioGroup(title: String,
               items: List<String>,
               selected: String,
               setSelected: (selected: String) -> Unit) {
    Text(text = title)
    Row(horizontalArrangement = Arrangement.SpaceBetween) {
        items.forEach { item ->
            RadioButton(
                selected = item == selected,
                onClick = { setSelected(item) },
                enabled = true,
            )
            Text(text = item, textAlign = TextAlign.Center, modifier= Modifier
                .wrapContentHeight()
                .align(Alignment.CenterVertically))
        }
    }

}