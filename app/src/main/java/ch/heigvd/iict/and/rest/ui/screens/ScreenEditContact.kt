package ch.heigvd.iict.and.rest.ui.screens

import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Create
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.runtime.*
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
import java.util.*

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ScreenEditContact(navController: NavHostController, contact: Long?, contactsViewModel: ContactsViewModel) {
    val contacts: List<Contact> by contactsViewModel.allContacts.observeAsState(initial = emptyList())

    val (currentContact, setCurrentContact) = remember { mutableStateOf(Contact(
        firstname = "",
        name = "",
        email = "",
        phoneNumber = "",
        type = PhoneType.MOBILE,
        birthday = Calendar.getInstance(),
        address = "",
        city = "",
        zip = "",
    )) }

    if (contact != null) {
        val found = contacts.find { it.id == contact!! }
        if (found != null) {
            setCurrentContact(found!!)
        }
    }

    val (selected, setSelected) = remember { mutableStateOf("")}
    val simpleDateFormat = SimpleDateFormat("dd-MM-yyyy")

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
            textFieldItem(name = "Name", placeHolder = "Name", value = currentContact.name, onValueChanged = {
                setCurrentContact(currentContact.copy(name = it!!))
            })
            textFieldItem(name = "Firstname", placeHolder = "Firstname", value = currentContact.firstname, onValueChanged = { setCurrentContact(currentContact.copy(firstname = it!!)) })
            textFieldItem(name = "Email", placeHolder = "Email", currentContact.email, onValueChanged = { setCurrentContact(currentContact.copy(email = it!!)) })
            textFieldItem(name = "Birthday", placeHolder = "Birthday", value = simpleDateFormat.format(currentContact.birthday?.time)) //TODO : add date picker? not obligatory
            textFieldItem(name = "Address", placeHolder = "Address", currentContact.address, onValueChanged = { setCurrentContact(currentContact.copy(address = it!!)) })
            textFieldItem(name = "Zip", placeHolder = "Zip", currentContact.zip, onValueChanged = { setCurrentContact(currentContact.copy(zip = it!!)) })
            textFieldItem(name = "City", placeHolder = "City", currentContact.city, onValueChanged = { setCurrentContact(currentContact.copy(city = it!!)) })
            RadioGroup(
                items = listOf("Home", "Mobile", "Office", "Fax"),
                selected = selected,
                setSelected = {
                    setSelected(it)
                    Log.d("RadioGroup", "Selected: $it")
                    when (it) {
                        "Home" -> setCurrentContact(currentContact.copy(type = PhoneType.HOME))
                        "Mobile" -> setCurrentContact(currentContact.copy(type = PhoneType.MOBILE))
                        "Office" -> setCurrentContact(currentContact.copy(type = PhoneType.OFFICE))
                        "Fax" -> setCurrentContact(currentContact.copy(type = PhoneType.FAX))
                    }
                              },
                title = "Phone type"
            )
            textFieldItem(name = "Phone number", placeHolder = "Phone number", currentContact.phoneNumber, onValueChanged = { setCurrentContact(currentContact.copy(phoneNumber = it!!)) })

            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Button(onClick = { /*TODO*/ }) {
                    Text(text = "Cancel")
                }

                Button(onClick = { /*TODO*/ }) {
                    Icon(Icons.Outlined.Delete, contentDescription = "Delete")
                    Text(text = "Delete")
                }

                Button(onClick = {
                    Log.d("Contact", currentContact.toString())
                    if (contact == null) {
                        contactsViewModel.addContact(currentContact)
                    } else {
                        contactsViewModel.changeContact(currentContact)
                    }
                }) {
                    Icon(Icons.Outlined.Create, contentDescription = "Save")
                    Text(text = "Save")
                }
            }
        }

    }
}

@Composable
fun textFieldItem(name : String, placeHolder : String, value : String?, onValueChanged: (String?) -> Unit = {}) {
    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier= Modifier
        .wrapContentSize(Alignment.Center)
        .fillMaxWidth()) {
        Text(text = name, modifier= Modifier
            .wrapContentHeight()
            .align(Alignment.CenterVertically))
        val finalValue = value ?: ""
        TextField(value = finalValue, onValueChange = {onValueChanged(it)}, placeholder = { Text(text = placeHolder!!) })
    }
}

@Composable
fun RadioGroup(title: String,
               items: List<String>,
               selected: String,
               setSelected: (selected: String) -> Unit ) {
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