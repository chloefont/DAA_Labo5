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
import java.util.LinkedList


class ContactsViewModel(application: ContactsApplication) : AndroidViewModel(application) {

    private val repository = application.repository
    private var apiUuid: String? = null

    init {
        if (getApplication<ContactsApplication>().getSharedPreferences(
                "ContactsApp",
                Context.MODE_PRIVATE
            ).contains("UUID")
        ) {
            apiUuid = getApplication<ContactsApplication>().getSharedPreferences(
                "ContactsApp",
                Context.MODE_PRIVATE
            ).getString("UUID", "")
        }
    }

    val allContacts: LiveData<List<Contact>> get() = repository.allContacts
    val apiBaseURL = "https://daa.iict.ch"

    fun getContactById(id: Long?): Contact? {
        return allContacts.value?.find { it.id == id }
    }

    fun enroll() {
        viewModelScope.launch {
            repository.clearAllContacts()

            apiUuid = getAPIUuid()
            getApplication<ContactsApplication>().getSharedPreferences(
                "ContactsApp",
                Context.MODE_PRIVATE
            ).edit().putString("UUID", apiUuid).apply()
            Log.i("DEV", "UUID: $apiUuid")
        }
    }

    fun refresh() {
        var contacts = repository.getAllContacts();
        for (contact in contacts) {
            if (contact.status == StatusType.OK) {
                continue;
            }

            fun update(contact: Contact) {
                val remoteId: Long? = put_contact(contact)

                contact.remoteId = remoteId;
                contact.status = StatusType.OK
                changeContact(contact)
            }

            fun new(contact: Contact) {
                val remoteId = post_contact(contact)

                contact.remoteId = remoteId;
                contact.status = StatusType.OK
                changeContact(contact)
            }

            fun delete(contact: Contact) {
                val result = delete_contact(contact)
                deleteContact(contact)
            }

            // Lancer thread IO
            run {
                when (contact.status) {
                    StatusType.DELETED -> delete(contact)
                    StatusType.UPDATED -> update(contact)
                    StatusType.NEW -> new(contact)
                    StatusType.OK -> {}
                }
            }

        }
    }

    fun post_contact(contact: Contact): Long? {
        return 0;
    }

    fun get_contacts(): List<Contact> {
        return LinkedList<Contact>();
    }

    fun get_contact(id: Long): Contact? {
        return null;
    }

    fun delete_contact(contact: Contact): Boolean {
        return true;
    }

    fun put_contact(contact: Contact): Long? {
        return 0;
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

    suspend fun getAPIUuid(): String = withContext(Dispatchers.IO) {

        val url = URL("$apiBaseURL/enroll")
        return@withContext url.readText(Charsets.UTF_8)
    }

    suspend fun apiAddContact(contact: Contact): Long? = withContext(Dispatchers.IO) {

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

class ContactsViewModelFactory(private val application: ContactsApplication) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ContactsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ContactsViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}