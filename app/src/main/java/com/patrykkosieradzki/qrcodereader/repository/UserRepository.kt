package com.patrykkosieradzki.qrcodereader.repository

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.patrykkosieradzki.qrcodereader.model.User

class UserRepository(private val databaseReference: DatabaseReference)
    : Repository<User> {

    companion object {
        val TAG: String = "UserRepository"
    }

    override fun add(user: User, listener: OnCompleteListener?) {
        databaseReference.child(user.uid).addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot?) {
                val databaseUser = dataSnapshot?.getValue(User::class.java)

                if (databaseUser == null) {
                    databaseReference.child(user.uid).setValue(user)
                    Log.d(TAG, """onDataChange: New user ${user.uid} added to database""")
                } else {
                    Log.d(TAG, "onDataChange: User already in the database, skipping adding new user to the database")
                }

                listener?.onComplete()
            }

            override fun onCancelled(databaseError: DatabaseError?) {
                Log.w(TAG, "onCanceled: Failed to read user from the database")
                listener?.onError()
            }

        })
    }

    override fun remove(item: User, listener: OnCompleteListener?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }



}