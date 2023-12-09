package com.ashray.letschat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.get
import com.ashray.letschat.databinding.ActivityMainBinding
import com.google.firebase.Firebase
import com.google.firebase.app
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private lateinit var viewModel : MainViewModel
    private lateinit var utils: Utils
    private lateinit var from: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_main)
        viewModel=ViewModelProvider(this)[MainViewModel::class.java]
        utils= Utils()
        setListeners()
        observe()
    }

    private fun observe() {
        viewModel.toast.observe(this@MainActivity,{
            toast(it)
            utils.hideCustomDialog()
            from=binding.loginName.text.toString()
            binding.apply {
                loginName.text?.clear()
                loginPass.text?.clear()
                userName.text?.clear()
                password.text?.clear()
            }
            if(it=="Login Successful"){
                val intent=Intent(this@MainActivity,ChatActivity::class.java)
                intent.putExtra("username",from)
                startActivity(intent)
            }
        })
    }

    private fun resetViews(){
        binding.apply {
            loSignup.visibility=View.GONE
            loLogin.visibility=View.GONE
            linearLayout.visibility=View.GONE
        }
    }

    private fun setListeners() {
        binding.apply {
            back1.setOnClickListener {
                resetViews()
                linearLayout.visibility=View.VISIBLE
            }
            back2.setOnClickListener {
                resetViews()
                linearLayout.visibility=View.VISIBLE
            }
            login.setOnClickListener{
                resetViews()
                loLogin.visibility=View.VISIBLE
            }
            signup.setOnClickListener {
                resetViews()
                loSignup.visibility=View.VISIBLE
            }
            createAccount.setOnClickListener {
                utils.showCustomDialog(this@MainActivity,"Please Wait..")
                viewModel.createAccount(userName.text.toString() ,password.text.toString())
            }
            logIn.setOnClickListener {
                utils.showCustomDialog(this@MainActivity,"Please Wait..")
                viewModel.logIn(loginName.text.toString() ,loginPass.text.toString())
            }
        }
    }

    private fun toast(message: String) {
        Toast.makeText(this@MainActivity,message,Toast.LENGTH_SHORT).show()
    }
}