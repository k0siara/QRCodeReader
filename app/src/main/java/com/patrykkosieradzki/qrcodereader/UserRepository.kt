package com.patrykkosieradzki.qrcodereader

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.patrykkosieradzki.qrcodereader.extensions.getClassName
import com.patrykkosieradzki.qrcodereader.logger.Logger
import com.patrykkosieradzki.qrcodereader.model.User

class UserRepository(private val databaseReference: DatabaseReference)
    : Repository<User> {

    override fun add(user: User) {
        databaseReference.child(user.uid).addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot?) {
                val databaseUser = dataSnapshot?.getValue(User::class.java)

                if (databaseUser == null) {
                    databaseReference.child(user.uid).setValue(user)
                    Log.d(getClassName(), "onDataChange: New user " + user.uid + " added to database")
                } else {
                    Log.d(getClassName(), "onDataChange: User already in the database, skipping adding new user to the database")
                }

            }

            override fun onCancelled(databaseError: DatabaseError?) {
                Log.w(getClassName(), "onCanceled: Failed to read user from the database")
            }

        })
    }

    override fun remove(user: User) {

    }
}