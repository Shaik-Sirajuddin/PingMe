package com.sirajapps.pingme.storage

import android.util.Log

import androidx.lifecycle.LiveData
import com.google.firebase.database.*


class FirebaseQueryLiveData(ref: DatabaseReference, private val type: Int) : LiveData<Data?>() {
    private val query: Query = ref
    private val childListener: ChildEventListener = ChildListener()
    private val valueListener:ValueEventListener = MyValueEventListener()
    private var isDone = false
    override fun onActive() {
        when (type) {
            valueEventType -> {
                query.addValueEventListener(valueListener)
            }
            singleEventType -> {
                if(isDone)return
                query.addListenerForSingleValueEvent(valueListener)
            }
            else -> {
                query.addChildEventListener(childListener)
            }
        }
    }

    override fun onInactive() {
        if(type == valueEventType || type == singleEventType){
            query.removeEventListener(valueListener)
        }else{
            query.removeEventListener(childListener)
        }
    }

    private inner class MyValueEventListener : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            value = Data(dataSnapshot,Data.dataSnapshot)
            if(type == singleEventType){
                isDone = true
            }
        }

        override fun onCancelled(databaseError: DatabaseError) {
            Log.e(
                LOG_TAG,
                "Can't listen to query $query", databaseError.toException()
            )
        }
    }
    private inner class ChildListener:ChildEventListener{
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
            value = Data(snapshot,Data.childAdded)
        }

        override fun onChildRemoved(snapshot: DataSnapshot) {
            if(type == childAddAndRemoveType) {
                value = Data(snapshot, Data.childRemoved)
            }
        }

        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            if(type == childChangeAlsoType){
                value = Data(snapshot,Data.childChanged)
            }
        }override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
        }override fun onCancelled(error: DatabaseError) {
        }
    }
    companion object {
        private const val LOG_TAG = "FirebaseQueryLiveData"
        const val  childAddOnlyType = 1
        const val  childAddAndRemoveType = 2
        const val  valueEventType = 3
        const val  singleEventType = 4
        const val  childChangeAlsoType = 5
    }
}
data class Data(val data:DataSnapshot,val type:Int){
    companion object{
        const val childAdded = 1
        const val childRemoved = 2
        const val childChanged = 4
        const val dataSnapshot = 3
    }
}