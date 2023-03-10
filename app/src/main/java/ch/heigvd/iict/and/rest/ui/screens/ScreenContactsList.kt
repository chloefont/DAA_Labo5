package ch.heigvd.iict.and.rest.ui.screens

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import ch.heigvd.iict.and.rest.R
import ch.heigvd.iict.and.rest.models.Contact
import ch.heigvd.iict.and.rest.models.PhoneType
import ch.heigvd.iict.and.rest.models.StatusType
import ch.heigvd.iict.and.rest.navigation.AppScreens
import ch.heigvd.iict.and.rest.ui.TopBar
import ch.heigvd.iict.and.rest.ui.theme.MyComposeApplicationTheme

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ScreenContactList(
    contacts : List<Contact>,
    navController: NavHostController,
    onPopulate: () -> Unit,
    onSynchronize : () -> Unit
) {

    val onAddContact = {navController.navigate(AppScreens.EditContact.name)}
    val onContactSelected = { selectedClient : Contact ->
        navController.navigate(AppScreens.EditContact.name + "?contactId=${selectedClient.id}")
        //Toast.makeText(context, "TODO - Edition de ${selectedContact.firstname} ${selectedContact.name}", Toast.LENGTH_SHORT).show()
    }

    val actionList : @Composable RowScope.() -> Unit = {
        IconButton(onClick = {
            onPopulate()
        }) { Icon(painter = painterResource(R.drawable.populate), contentDescription = null) }
        IconButton(onClick = {
            onSynchronize()
        }) { Icon(painter = painterResource(R.drawable.synchronize), contentDescription = null) }

    }

    Scaffold(
        topBar = { TopBar(currentScreen = AppScreens.Contacts, canNavigateBack = false, actionList = actionList) },
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
            FloatingActionButton(onClick = onAddContact) {
                Icon(Icons.Default.Add, contentDescription = null)
            }
        },
    )
    {
        Column {
            Text(text = stringResource(R.string.screen_list_title), fontSize = 24.sp)
            if (contacts.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.screen_list_empty),
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(contacts) { item ->
                        ContactItemView(item) { clickedContact ->
                            onContactSelected(clickedContact)
                        }
                    }
                }

            }
        }


//        Column(modifier = Modifier.padding(padding)) {  }
//        ScreenContactList(contacts) { selectedContact ->
//            Toast.makeText(context, "TODO - Edition de ${selectedContact.firstname} ${selectedContact.name}", Toast.LENGTH_SHORT).show()
//        }
    }

}

@Composable
fun ContactItemView(contact: Contact, onClick : (Contact) -> Unit) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .height(48.dp)
        .padding(2.dp)
        .clickable {
            onClick(contact)
        },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically) {
        Image(painter = painterResource(id = R.drawable.contact), contentDescription = stringResource(id = R.string.screen_list_contacticon_ctndesc))
        Column(modifier = Modifier.weight(1f).padding(horizontal = 10.dp, vertical = 2.dp),
            horizontalAlignment = Alignment.Start) {
            Text(text = "${contact.firstname} ${contact.name}")
            Text(text = "${contact.phoneNumber}")
        }
        Image(painter = painterResource(id = when(contact.type){
            PhoneType.MOBILE -> R.drawable.cellphone
            PhoneType.FAX -> R.drawable.fax
            PhoneType.HOME -> R.drawable.phone
            PhoneType.OFFICE -> R.drawable.office
            else -> R.drawable.office
        }),
            contentDescription = stringResource(id = R.string.screen_list_contacttype_ctndesc))
    }
}

val contactsDemo = listOf(
    Contact(null, "Dupont", "Roger", null, null, "", "1400", "Yverdon", PhoneType.HOME, "+41 21 944 23 55", remoteId = null, status = StatusType.NEW),
    Contact(null, "Dupond", "Tatiana", null, null, "", "1000", "Lausanne", PhoneType.OFFICE, "+41 24 763 34 12", remoteId = null, status = StatusType.NEW),
    Contact(null, "Toto", "Tata", null, null, "", "1400", "Yverdon", PhoneType.MOBILE, "+41 21 456 25 36", remoteId = null, status = StatusType.NEW)
)

@Preview(showBackground = true)
@Composable
fun ContactListPreview() {
    MyComposeApplicationTheme {
        ScreenContactList(contactsDemo, rememberNavController(), {}, {})
    }
}

@Preview(showBackground = true)
@Composable
fun ContactItemPreview() {
    MyComposeApplicationTheme {
        ContactItemView(contactsDemo[0], {})
    }
}