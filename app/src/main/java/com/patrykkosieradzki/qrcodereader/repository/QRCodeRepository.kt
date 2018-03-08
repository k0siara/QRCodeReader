package com.patrykkosieradzki.qrcodereader.repository

import android.util.Log
import com.google.firebase.database.*
import com.patrykkosieradzki.qrcodereader.model.QRCode
import com.patrykkosieradzki.qrcodereader.model.User
import com.patrykkosieradzki.qrcodereader.ui.home.HomeActivity

class QRCodeRepository(private val database: DatabaseReference) : Repository<QRCode> {

    companion object {
        val TAG: String = "QRCodeRepository"
    }

    override fun add(qrCode: QRCode, listener: OnCompleteListener?) {
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue(User::class.java)

                if (user != null) {
                    val key = database.push().key
                    database.child(key).setValue(qrCode)
                    Log.d(TAG, "onDataChange: Added new QRCode ${qrCode.text}")

                    listener?.onComplete()
                } else {
                    Log.d(TAG, "onDataChange: User not found in the database. Trying to insert data with a non-existent account")

                    listener?.onError()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(TAG, "onCanceled: Failed to read user from the database")

                listener?.onError()
            }
        })
    }

    override fun remove(qrCode: QRCode, listener: OnCompleteListener?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


}