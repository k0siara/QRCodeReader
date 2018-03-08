package com.patrykkosieradzki.qrcodereader.repository

import android.util.Log
import com.google.firebase.database.*
import com.patrykkosieradzki.qrcodereader.model.User

class UserRepository(private val database: DatabaseReference) : Repository<User> {

    companion object {
        val TAG: String = "UserRepository"
    }

    override fun add(user: User, listener: OnCompleteListener?) {
        database.child(user.uid).addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot?) {
                val databaseUser = dataSnapshot?.getValue(User::class.java)

                if (databaseUser == null) {
                    database.child(user.uid).setValue(user)
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

    fun query(child: String): Query {
        return database.child(child)
    }



}