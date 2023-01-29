package ch.heigvd.iict.and.rest.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import ch.heigvd.iict.and.rest.ContactsApplication
import ch.heigvd.iict.and.rest.models.CalendarTypeAdapter
import ch.heigvd.iict.and.rest.models.Contact
import ch.heigvd.iict.and.rest.models.StatusType
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.*


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

            withContext(Dispatchers.IO) {
                val contacts = get_contacts()
                for (contact in contacts) {
                    contact.remoteId = contact.id
                    contact.id = null
                    contact.status = StatusType.OK
                    repository.addContact(contact)
                }
            }
        }
    }

    fun refresh() {
        println(apiUuid)
        var contacts = repository.allContacts;

        for (contact in contacts.value!!) {
            if (contact.status == StatusType.OK) {
                continue;
            }

            fun update(contact: Contact) {
                val remoteId: Long? = put_contact(contact)

                contact.remoteId = remoteId
                contact.status = StatusType.OK
                repository.changeContact(contact)
            }

            fun new(contact: Contact) {
                val remoteId = post_contact(contact)

                contact.remoteId = remoteId
                contact.status = StatusType.OK
                repository.changeContact(contact)
            }

            fun delete(contact: Contact) {
                val result = delete_contact(contact)
                if (result) {
                    repository.deleteContact(contact)
                }
            }

            // Lancer thread IO
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    when (contact.status) {
                        StatusType.DELETED -> delete(contact)
                        StatusType.UPDATED -> update(contact)
                        StatusType.NEW -> new(contact)
                        StatusType.OK -> {}
                    }
                }
            }

        }
    }

    fun post_contact(contact: Contact): Long? {
        val url = URL("https://daa.iict.ch/contacts/")


        val conn = url.openConnection() as HttpURLConnection
        conn.requestMethod = "POST"
        conn.setRequestProperty("x-uuid", apiUuid!!)
        conn.setRequestProperty("Content-Type", "application/json")

        contact.id = null
        //contact.birthday = null
        val gson = GsonBuilder()
            .registerTypeHierarchyAdapter(Calendar::class.java, CalendarTypeAdapter())
            .create()
        val json: String = gson.toJson(contact)
        conn.outputStream.use { output ->
            output.write(json.toByteArray(Charsets.UTF_8))
        }

        if (conn.responseCode != 201) {
            return null
        }

        var data = "";
        BufferedReader(InputStreamReader(conn.inputStream)).use { br ->
            data = br.readText()
        }

        val valtype = object : TypeToken<Contact?>() {}.type
        val result = gson.fromJson<Contact?>(data, valtype)

        return result.id
    }

    fun get_contacts(): List<Contact> {

        val url = URL("https://daa.iict.ch/contacts")


        val conn = url.openConnection() as HttpURLConnection
        conn.requestMethod = "GET"
        conn.setRequestProperty("x-uuid", apiUuid!!)

        var data = "";
        BufferedReader(InputStreamReader(conn.inputStream)).use { br ->
            data = br.readText()
        }

        val gson = GsonBuilder()
            .registerTypeHierarchyAdapter(Calendar::class.java, CalendarTypeAdapter())
            .create()
        val valtype = object : TypeToken<List<Contact>?>() {}.type
        val result: List<Contact> = gson.fromJson<List<Contact>>(data, valtype) as List<Contact>

        return result;
    }

    fun get_contact(id: Long): Contact? {
        // TODO ne marche pas
        val url = URL("https://daa.iict.ch/contacts/$id")


        val conn = url.openConnection() as HttpURLConnection
        conn.requestMethod = "GET"
        conn.setRequestProperty("x-uuid", apiUuid!!)

        var data = "";
        BufferedReader(InputStreamReader(conn.inputStream)).use { br ->
            data = br.readText()
        }
        println(data)
        val gson = Gson()
        val valtype = Contact::class.java

        return gson.fromJson(data, valtype);
    }

    fun delete_contact(contact: Contact): Boolean {
        var id = contact.remoteId;
        val url = URL("https://daa.iict.ch/contacts/$id")


        val conn = url.openConnection() as HttpURLConnection
        conn.requestMethod = "DELETE"
        conn.setRequestProperty("x-uuid", apiUuid!!)

        return conn.responseCode == 204
    }

    fun put_contact(contact: Contact): Long? {
        var id = contact.remoteId;
        val url = URL("https://daa.iict.ch/contacts/$id")


        val conn = url.openConnection() as HttpURLConnection
        conn.requestMethod = "PUT"
        conn.setRequestProperty("x-uuid", apiUuid!!)
        conn.setRequestProperty("Content-Type", "application/json")

        val gson = GsonBuilder()
            .registerTypeHierarchyAdapter(Calendar::class.java, CalendarTypeAdapter())
            .create()
        val json: String = gson.toJson(contact)
        conn.outputStream.use { output ->
            output.write(json.toByteArray(Charsets.UTF_8))
        }

        if (conn.responseCode != 200) {
            return null
        }

        var data = "";
        BufferedReader(InputStreamReader(conn.inputStream)).use { br ->
            data = br.readText()
        }

        val valtype = object : TypeToken<Contact?>() {}.type
        val result = gson.fromJson<Contact?>(data, valtype)

        return result.id
    }

    fun addContact(contact: Contact) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                contact.status = StatusType.NEW

                if (apiUuid != null) {
                    var remoteId: Long? = post_contact(contact)
                    if (remoteId != null) {
                        contact.remoteId = remoteId
                        contact.status = StatusType.OK
                    }

                }

                repository.addContact(contact)
            }
        }
    }

    fun changeContact(contact: Contact) {
        viewModelScope.launch {
            var id = contact.id
            withContext(Dispatchers.IO) {
                contact.status = StatusType.UPDATED
                if (apiUuid != null) {
                    contact.id = contact.remoteId
                    val remoteId: Long? = put_contact(contact)
                    if (remoteId != null){
                        contact.remoteId = remoteId
                        contact.status = StatusType.OK
                    }
                }
                contact.id = id
                repository.changeContact(contact)
            }
        }
    }

    fun deleteContact(contact: Contact) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                if (apiUuid != null) {
                    val ok = delete_contact(contact)
                    if (ok) {
                        repository.deleteContact(contact)
                    } else {
                        contact.status = StatusType.DELETED
                        repository.changeContact(contact)
                    }
                }
            }


        }
    }

    suspend fun getAPIUuid(): String = withContext(Dispatchers.IO) {

        val url = URL("$apiBaseURL/enroll")
        return@withContext url.readText(Charsets.UTF_8)
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