package com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.activity.data.db.contact

import android.annotation.SuppressLint
import android.content.Context
import android.provider.ContactsContract
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

object DataContact {

    @SuppressLint("Range")
    fun getAllContact(context: Context): Flow<MutableList<ContactModel>> {
        val lstContact = mutableListOf<ContactModel>()
        var tmpContact = ContactModel()

        val cur = context.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
            null, null, null
        )
        cur?.let {
            if (it.count > 0) {
                while (it.moveToNext()) {
                    val id =
                        it.getString(it.getColumnIndex(ContactsContract.Data.NAME_RAW_CONTACT_ID))
                    val name =
                        it.getString(it.getColumnIndex(ContactsContract.Data.DISPLAY_NAME))
                    val number =
                        it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                    val ringtone =
                        it.getString(it.getColumnIndex(ContactsContract.Data.CUSTOM_RINGTONE))
                    val lookupKey = it.getString(it.getColumnIndex(ContactsContract.Data.LOOKUP_KEY))

                    if (id != tmpContact.id) {
                        val contact = ContactModel(id, name, number, ringtone ?: "", lookupKey)
                        lstContact.add(contact)
                        tmpContact = contact
                    }
                }
            }
        }

        cur?.close()
        return flow {
            emit(lstContact)
        }
    }
}