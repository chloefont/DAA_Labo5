package ch.heigvd.iict.and.rest.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import ch.heigvd.iict.and.rest.ContactsApplication
import ch.heigvd.iict.and.rest.models.Contact
import ch.heigvd.iict.and.rest.models.StatusType
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL


class ContactsViewModel(application: ContactsApplication) : AndroidViewModel(application) {

    private val repository = application.repository
    private var apiUuid : String? = null

    init {
        if (getApplication<ContactsApplication>().getSharedPreferences("ContactsApp", Context.MODE_PRIVATE).contains("UUID")) {
            apiUuid = getApplication<ContactsApplication>().getSharedPreferences("ContactsApp", Context.MODE_PRIVATE).getString("UUID", "")
        }
    }

    val allContacts : LiveData<List<Contact>>get() = repository.allContacts
    val apiBaseURL = "https://daa.iict.ch"

    fun getContactById(id: Long?) : Contact? {
        return allContacts.value?.find { it.id == id }
    }

    fun enroll() {
        viewModelScope.launch {
            repository.clearAllContacts()

            apiUuid = getAPIUuid()
            getApplication<ContactsApplication>().getSharedPreferences("ContactsApp", Context.MODE_PRIVATE).edit().putString("UUID", apiUuid).apply()
            Log.i("DEV", "UUID: $apiUuid")
        }
    }

    fun refresh() {
        viewModelScope.launch {
            if (apiUuid != null) {
                val url = URL("$apiBaseURL/contacts")
                val connection = url.openConnection() as HttpURLConnection
                connection.doOutput = true
                connection.requestMethod = "GET"
                connection.setRequestProperty("X-UUID", apiUuid)

                connection.inputStream.use { input ->
                    val result = input.bufferedReader().use { it.readText() }
                    Log.d("DEV", "Result: $result")
                }
            }
        }
    }

    fun addContact(contact: Contact) {
        viewModelScope.launch {
            contact.status = StatusType.NEW
            repository.addContact(contact)

            if (apiUuid != null) {
                apiAddContact(contact)
            }
        }
    }

    fun changeContact(contact: Contact) {
        viewModelScope.launch {
            repository.changeContact(contact)
        }
    }

    fun deleteContact(contact: Contact) {
        viewModelScope.launch {
            repository.deleteContact(contact)
        }
    }

    suspend fun getAPIUuid() : String = withContext(Dispatchers.IO) {

        val url = URL("$apiBaseURL/enroll")
        return@withContext url.readText(Charsets.UTF_8)
    }

    suspend fun apiAddContact(contact: Contact) : Long? = withContext(Dispatchers.IO) {

        val url = URL("$apiBaseURL/contacts")
        val connection = url.openConnection() as HttpURLConnection
        connection.doOutput = true
        connection.requestMethod = "POST"
        connection.setRequestProperty("X-UUID", apiUuid)
        connection.setRequestProperty("Content-Type", "application/json")

        contact.id = null
        val gson = Gson()
        val json: String = gson.toJson(contact)
        connection.outputStream.use { output ->
            output.write(json.toByteArray(Charsets.UTF_8))
        }

        connection.inputStream.use { input ->
            val result = input.bufferedReader().use { it.readText() }
            Log.d("DEV1", "Result: $result")
        }

        return@withContext null
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