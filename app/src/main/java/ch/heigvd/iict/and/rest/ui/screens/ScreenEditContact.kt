package ch.heigvd.iict.and.rest.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material.RadioButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import ch.heigvd.iict.and.rest.navigation.AppScreens
import ch.heigvd.iict.and.rest.ui.TopBar

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ScreenEditContact(navController : NavHostController, contact : Long?) {
    val (selected, setSelected) = remember { mutableStateOf("") }

    Scaffold(
        topBar = { TopBar(currentScreen = AppScreens.EditContact,
            canNavigateBack = true,
            onNavigateBack = { navController.navigateUp() }) }
    ) {
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(10.dp, 0.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(text = "New contact")
            textFieldItem(name = "Name", placeHolder = "Name", null)
            textFieldItem(name = "Firstname", placeHolder = "Firstname", null)
            textFieldItem(name = "Email", placeHolder = "Email", null)
            textFieldItem(name = "Birthday", placeHolder = "Birthday", null)
            textFieldItem(name = "Address", placeHolder = "Address", null)
            textFieldItem(name = "Zip", placeHolder = "Zip", null)
            textFieldItem(name = "City", placeHolder = "City", null)
            RadioGroup(items = listOf("Home", "Mobile", "Office", "Fax"), selected = selected, setSelected = setSelected, title = "Phone type")
            textFieldItem(name = "Phone number", placeHolder = "Phone number", null)
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
        if (value != null) {
            TextField(value = "", onValueChange = {}, placeholder = { Text(text = placeHolder!!) })
        } else {
            TextField(value = "", placeholder = { Text(text = placeHolder!!) }, onValueChange = {})
        }
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