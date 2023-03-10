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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import ch.heigvd.iict.and.rest.models.Contact
import ch.heigvd.iict.and.rest.models.PhoneType
import ch.heigvd.iict.and.rest.models.StatusType
import ch.heigvd.iict.and.rest.navigation.AppScreens
import ch.heigvd.iict.and.rest.ui.TopBar
import ch.heigvd.iict.and.rest.viewmodels.ContactsViewModel
import java.util.*

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ScreenEditContact(
    navController: NavHostController,
    contact: Long?,
    contactsViewModel: ContactsViewModel
) {

    val found : Contact? = contactsViewModel.getContactById(contact)


    val (selected, setSelected) = remember { mutableStateOf(found?.type.toString() ?: PhoneType.MOBILE.toString()) }
    val simpleDateFormat = SimpleDateFormat("dd-MM-yyyy")

    val (name, setName) = remember { mutableStateOf(found?.name ?: "") }
    val (firstname, setFirstname) = remember { mutableStateOf(found?.firstname ?: "") }
    val (email, setEmail) = remember { mutableStateOf(found?.email ?: "") }
    val (phoneNumber, setPhoneNumber) = remember { mutableStateOf(found?.phoneNumber ?: "") }
    val (address, setAddress) = remember { mutableStateOf(found?.address ?: "") }
    val (zip, setZip) = remember { mutableStateOf(found?.zip ?: "") }
    val (city, setCity) = remember { mutableStateOf(found?.city ?: "") }
    val (birthday, setBirthday) = remember { mutableStateOf(found?.birthday ?: Calendar.getInstance()) }

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
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(text = "New contact")
            textFieldItem(name = "Name", placeHolder = "Name", value = name, onValueChanged = {
                setName(it!!)
            })
            textFieldItem(
                name = "Firstname",
                placeHolder = "Firstname",
                value = firstname,
                onValueChanged = {
                    setFirstname(it!!)
                })
            textFieldItem(name = "Email", placeHolder = "Email", email, onValueChanged = {
                setEmail(it!!)
            })
            textFieldItem(
                name = "Birthday",
                placeHolder = "Birthday",
                value = SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'").format(birthday?.time)
            ) //TODO : add date picker? not obligatory
            textFieldItem(name = "Address", placeHolder = "Address", address, onValueChanged = {
                setAddress(it!!)
            })
            textFieldItem(name = "Zip", placeHolder = "Zip", zip, onValueChanged = {
                setZip(it!!)
            })
            textFieldItem(name = "City", placeHolder = "City", city, onValueChanged = {
                setCity(it!!)
            })
            RadioGroup(
                items = listOf("Home", "Mobile", "Office", "Fax"),
                selected = selected,
                setSelected = setSelected,
                title = "Phone type"
            )
            textFieldItem(
                name = "Phone number",
                placeHolder = "Phone number",
                phoneNumber,
                onValueChanged = {
                    setPhoneNumber(it!!)
                })

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(onClick = { navController.navigateUp() }) {
                    Text(text = "Cancel")
                }

                if (found != null) {
                    Button(onClick = {
                        contactsViewModel.deleteContact(found)

                        navController.navigateUp()
                    }) {
                        Icon(Icons.Outlined.Delete, contentDescription = "Delete")
                        Text(text = "Delete")
                    }
                }

                Button(onClick = {

                    val phoneType = when (selected) {
                        "Home" -> PhoneType.HOME
                        "Mobile" -> PhoneType.MOBILE
                        "Office" -> PhoneType.OFFICE
                        "Fax" -> PhoneType.FAX
                        else -> PhoneType.MOBILE
                    }

                    val newContact = Contact(
                        firstname = firstname,
                        name = name,
                        email = email,
                        phoneNumber = phoneNumber,
                        type = phoneType,
                        birthday = birthday,
                        address = address,
                        city = city,
                        zip = zip,
                        remoteId = found?.remoteId ?: null,
                        status = StatusType.NEW
                    )
                    if (contact == null) {
                        contactsViewModel.addContact(newContact)
                    } else if (found != null) {
                        newContact.id = found.id
                        newContact.status = StatusType.UPDATED
                        contactsViewModel.changeContact(newContact)
                    }

                    navController.navigateUp()
                }) {
                    Icon(Icons.Outlined.Create, contentDescription = "Save")
                    Text(text = "Save")
                }
            }
        }

    }
}

@Composable
fun textFieldItem(
    name: String,
    placeHolder: String,
    value: String?,
    onValueChanged: (String?) -> Unit = {}
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier
            .wrapContentSize(Alignment.Center)
            .fillMaxWidth()
    ) {
        Text(
            text = name, modifier = Modifier
                .wrapContentHeight()
                .align(Alignment.CenterVertically)
        )
        val finalValue = value ?: ""
        TextField(value = finalValue, onValueChange = {

            onValueChanged(it)
        }, placeholder = { Text(text = placeHolder!!) })
    }
}

@Composable
fun RadioGroup(
    title: String,
    items: List<String>,
    selected: String,
    setSelected: (selected: String) -> Unit
) {
    Text(text = title)
    Row(horizontalArrangement = Arrangement.SpaceBetween) {
        items.forEach { item ->
            RadioButton(
                selected = item == selected,
                onClick = { setSelected(item) },
                enabled = true,
            )
            Text(
                text = item, textAlign = TextAlign.Center, modifier = Modifier
                    .wrapContentHeight()
                    .align(Alignment.CenterVertically)
            )
        }
    }

}