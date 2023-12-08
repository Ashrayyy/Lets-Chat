package com.ashray.letschat

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

class ChatViewModel:ViewModel() {
    private val _toast=MutableLiveData<String>()
    private val _update=MutableLiveData<Int>()
    private val _updateFriends=MutableLiveData<Int>()
    val toast : LiveData<String> get() = _toast
    val update : LiveData<Int> get() = _update
    val updateFriends : LiveData<Int> get() = _updateFriends

    var thisChat= mutableListOf<Message>()
    var friends= mutableListOf<String>()

    fun sendChat(from:String, to:String, message:String){
        val db= Firebase.firestore
        val chat1=hashMapOf(
            "message" to message,
            "sender" to 1
        )
        val chat2=hashMapOf(
            "message" to message,
            "sender" to 2
        )
        db.collection(from+"-"+to).document(System.currentTimeMillis().toString()).set(chat1)
            .addOnFailureListener {
                _toast.postValue("Some Error Occured")
            }
        db.collection(to+"-"+from).document(System.currentTimeMillis().toString()).set(chat2)
            .addOnFailureListener {
                _toast.postValue("Some Error Occured")
            }
        updateChat(from, to)
    }

    fun updateChat(from : String, to:String) {
        val db= Firebase.firestore
        db.collection(from+'-'+to)
            .get()
            .addOnSuccessListener { documents ->
                thisChat.clear()
                _update.postValue(1)
                for (document in documents) {
                    document.data?.let {
                        val sender=it.get("sender").toString().toInt()
                        val message= it.get("message").toString()
                        thisChat.add(Message(message,sender))
                    }
                    Log.d("ashray", "${document.id} => ${document.data}")
                    _update.postValue(1)
                }
            }
            .addOnFailureListener { exception ->
               _toast.postValue(exception.message.toString())
            }
        Log.i("Ashray size ", thisChat.size.toString())
    }

    fun getFriends(from : String) {
        val db= Firebase.firestore
        db.collection(from)
            .get()
            .addOnSuccessListener { documents ->
                friends.clear()
                _updateFriends.postValue(1)
                for (document in documents) {
                    document.data?.let {
                        val friend=it.get("username").toString()
                        friends.add(friend)
                    }
                    Log.d("ashray", "${document.id} => ${document.data}")
                }
                _updateFriends.postValue(1)
            }
            .addOnFailureListener { exception ->
                _toast.postValue(exception.message.toString())
            }
    }

    fun addFriend(from:String, username: String) {
        if(from == username){
            _toast.postValue("You cannot add yourself as friend!")
            return
        }
        val db= FirebaseDatabase.getInstance().reference
        db.child("users").child(username.lowercase()).get()
            .addOnSuccessListener {
                if(it.value==null){
                    _toast.postValue("User Not Found")
                }
                else{
                    if(username.lowercase() in friends){
                        _toast.postValue("User already exists as friend")
                    }
                    else{
                        val fdb=Firebase.firestore
                        var data= hashMapOf(
                            "username" to username
                        )
                        fdb.collection(from).document(System.currentTimeMillis().toString()).set(data)
                            .addOnSuccessListener {
                                _toast.postValue("User added as friend")
                                getFriends(from)
                            }
                            .addOnFailureListener {
                                _toast.postValue("Something went wrong")
                                getFriends(from)
                            }
                        data= hashMapOf(
                            "username" to from
                        )
                        fdb.collection(username).document(System.currentTimeMillis().toString()).set(data)

                    }
                }
            }
            .addOnFailureListener {
                _toast.postValue("Some Error Occured")
            }
    }

    fun updateImage(from: String,imageUri : Uri) {
        val bucket=FirebaseStorage.getInstance().getReference(from)
        bucket.putFile(imageUri)
            .addOnSuccessListener {
                _toast.postValue("Picture Changed Successfully")
            }
    }
}