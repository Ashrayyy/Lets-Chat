package com.ashray.letschat

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.FirebaseDatabase

class MainViewModel :ViewModel() {
    private var _toast = MutableLiveData<String>()
    val toast : LiveData<String> get() = _toast

    fun createAccount(username : String , password : String){
        val db= FirebaseDatabase.getInstance().reference
        val data= hashMapOf(
            username to password
        )
        db.child("users").child(username.lowercase()).get()
            .addOnSuccessListener {
                Log.i("ashray",it.toString())
                if(it.value==null){
                    db.child("users").child(username.lowercase()).setValue(data)
                    _toast.postValue("Account Created, Please Log In")
                }
                else{
                    _toast.postValue("Username Unavailable")
                }
            }
            .addOnFailureListener{
                _toast.postValue("Some Error Occured")
            }
    }

    fun logIn(username : String , password : String){
        val db=FirebaseDatabase.getInstance().reference
        db.child("users").child(username.lowercase()).get()

            .addOnSuccessListener {
                if(it.value==null){
                    _toast.postValue("User Not Found")
                }
                else{
                    val kvp=it.value.toString()
                    val ar=kvp.split('=','{','}')?.get(2)
                    if(ar.toString()==password){
                        _toast.postValue("Login Successful")
                    }
                    else{
                        _toast.postValue("Incorrect Password")
                    }
                }
            }
            .addOnFailureListener {
                _toast.postValue("Some Error Occured")
            }
    }
}