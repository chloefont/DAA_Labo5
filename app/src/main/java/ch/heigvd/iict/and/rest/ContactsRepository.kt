package ch.heigvd.iict.and.rest

import android.util.Log
import ch.heigvd.iict.and.rest.database.ContactsDao
import java.net.URL
import kotlin.concurrent.thread

class ContactsRepository(private val contactsDao: ContactsDao) {

    val allContacts = contactsDao.getAllContactsLiveData()

    fun clearAllContacts() {
        thread {
            contactsDao.clearAllContacts()
        }
    }
}