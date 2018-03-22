package com.patrykkosieradzki.qrcodereader.repository

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.patrykkosieradzki.qrcodereader.model.QRCode
import com.patrykkosieradzki.qrcodereader.model.User
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.debug
import org.jetbrains.anko.warn

class QRCodeRepository(private var database: DatabaseReference, userId: String) : Repository<QRCode>, AnkoLogger {

    init {
        database = database.child("users").child(userId).child("qrCodes")
    }

    override fun add(qrCode: QRCode, listener: OnCompleteListener?) {
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val databaseUser = dataSnapshot.getValue(User::class.java)

                if (databaseUser != null) {
                    val key = database.push().key
                    database.child(key).setValue(qrCode)

                    debug("onDataChange: Added new QRCode ${qrCode.text}")
                    listener?.onComplete()
                } else {
                    debug("onDataChange: User not found in the database. Trying to insert data with a non-existent account")
                    listener?.onError()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                warn("onCanceled: Failed to read user from the database")
                warn("Cause: ${databaseError.toString()}}")
                listener?.onError()
            }
        })
    }

    override fun remove(qrCode: QRCode, listener: OnCompleteListener?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun query() = database


}