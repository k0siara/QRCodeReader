package com.patrykkosieradzki.qrcodereader.repository

import com.google.firebase.database.*
import com.patrykkosieradzki.qrcodereader.model.User
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.debug
import org.jetbrains.anko.warn

class UserRepository(private var database: DatabaseReference) : Repository<User>, AnkoLogger {

    init {
        database = database.child("users")
    }

    override fun add(user: User, listener: OnCompleteListener?) {
        database.child(user.uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot?) {
                val databaseUser = dataSnapshot?.getValue(User::class.java)

                if (databaseUser == null) {
                    database.child(user.uid).setValue(user)
                    debug("onDataChange: New user ${user.uid} added to database")
                } else {
                    debug("onDataChange: User already in the database, skipping adding new user to the database")
                }

                listener?.onComplete()
            }

            override fun onCancelled(databaseError: DatabaseError?) {
                warn("onCanceled: Failed to read user from the database")
                warn("Cause: ${databaseError.toString()}}")
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