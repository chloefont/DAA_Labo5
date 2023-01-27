package ch.heigvd.iict.and.rest.viewmodels

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.*
import ch.heigvd.iict.and.rest.ContactsApplication
import ch.heigvd.iict.and.rest.models.Contact
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL


class ContactsViewModel(application: ContactsApplication) : AndroidViewModel(application) {

    private val repository = application.repository

    val allContacts : LiveData<List<Contact>>get() = repository.allContacts
    val apiBaseURL = "https://daa.iict.ch"

    fun enroll() {
        viewModelScope.launch {
            repository.clearAllContacts()

            val uid = getAPIUuid()
            getApplication<ContactsApplication>().getSharedPreferences("ContactsApp", Context.MODE_PRIVATE).edit().putString("UUID", uid).apply()
            Log.i("DEV", "UUID: $uid")
        }
    }

    fun refresh() {
        Log.d("DEV", "refresh")
        viewModelScope.launch {
            if (getApplication<ContactsApplication>().getSharedPreferences("ContactsApp", Context.MODE_PRIVATE).contains("UUID")) {
                Log.d("DEV", "UUID found")
                val uuid = getApplication<ContactsApplication>().getSharedPreferences("ContactsApp", Context.MODE_PRIVATE).getString("UUID", "")
                val url = URL("$apiBaseURL/contacts")
                val connection = url.openConnection() as HttpURLConnection
                connection.doOutput = true
                connection.requestMethod = "GET"
                connection.setRequestProperty("X-UUID", uuid)

                connection.inputStream.use { input ->
                    val result = input.bufferedReader().use { it.readText() }
                    Log.d("DEV1", "Result: $result")
                }
            }
        }
    }

    fun addContact(contact: Contact) {
        viewModelScope.launch {
            //TODO
        }
    }

    fun changeContact(contact: Contact) {
        viewModelScope.launch {
            //TODO
        }
    }

    suspend fun getAPIUuid() : String = withContext(Dispatchers.IO) {

        val url = URL("$apiBaseURL/enroll")
        return@withContext url.readText(Charsets.UTF_8)
    }

}

class ContactsViewModelFactory(private val application: ContactsApplication) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ContactsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ContactsViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}