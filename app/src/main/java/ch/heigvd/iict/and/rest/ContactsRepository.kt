package ch.heigvd.iict.and.rest

import android.util.Log
import ch.heigvd.iict.and.rest.database.ContactsDao
import ch.heigvd.iict.and.rest.models.Contact
import java.net.URL
import kotlin.concurrent.thread

class ContactsRepository(private val contactsDao: ContactsDao) {

    val allContacts = contactsDao.getAllContactsLiveData()

    fun clearAllContacts() {
        thread {
            contactsDao.clearAllContacts()
        }
    }

    fun addContact(contact: Contact) {
        thread {
            contactsDao.insert(contact)
        }
    }

    fun changeContact(contact: Contact) {
        thread {
            contactsDao.update(contact)
        }
    }

    fun getContactById(id: Long) : Contact? {
        var contact : Contact? = null
        thread {
            contact = contactsDao.getContactById(id!!)
        }
        return contact
    }

    fun deleteContact(contact: Contact) {
        thread {
            contactsDao.delete(contact)
        }
    }
}