package com.ashray.letschat

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.HORIZONTAL
import androidx.recyclerview.widget.RecyclerView.Orientation
import androidx.recyclerview.widget.RecyclerView.Recycler
import com.ashray.letschat.databinding.ActivityChatBinding
import com.ashray.letschat.databinding.ActivityMainBinding
import com.bumptech.glide.Glide
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.io.FileNotFoundException

class ChatActivity : AppCompatActivity(), RecyclerViewClickListener ,AddFriend.confirmation{
    private lateinit var viewModel: ChatViewModel
    private lateinit var binding: ActivityChatBinding
    private lateinit var imageUri: Uri
    private var from : String? = null
    private var fragment : AddFriend? = null
    private var to : String? = null
    private lateinit var chatAdapter : ChatAdapter
    private lateinit var adapter : FriendsAdapter
    private lateinit var utils : Utils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_chat)
        viewModel=ViewModelProvider(this)[ChatViewModel::class.java]
        from=intent.getStringExtra("username")
        utils=Utils()
        initViews()
        setListeners()
        observe()
    }

    private fun observe() {
        viewModel.update.observe(this,{
            chatAdapter.setChats(viewModel.thisChat)
            utils.hideCustomDialog()
            if(viewModel.thisChat.size>0) {
                binding.chatBox.smoothScrollToPosition(viewModel.thisChat.size - 1);
            }
        })
        viewModel.updateFriends.observe(this,{
            utils.hideCustomDialog()
            adapter.setFriends(viewModel.friends)
        })
        viewModel.toast.observe(this,{
            utils.hideCustomDialog()
            toast(it)
        })
    }

    private fun initViews() {
        val storagename = FirebaseStorage.getInstance().reference.child("$from")
        Log.i("ashray", storagename.name.toString())
        storagename.downloadUrl.addOnSuccessListener {uri->
            Glide.with(this)
                .load(uri)
                .into(binding.dp)
        }
        adapter=FriendsAdapter(this)
//        adapter.setFriends(listOf("aditya", "priyash"))

        utils.showCustomDialog(this@ChatActivity,"Please Wait..")
        viewModel.getFriends(from.toString())
        binding.apply {
            recyclerView.also {
                it.layoutManager=LinearLayoutManager(this@ChatActivity,RecyclerView.HORIZONTAL,false)
                it.adapter=adapter
            }
            myUserName.text=from.toString()
        }
        chatAdapter=ChatAdapter()
        binding.apply {
            chatBox.also {
                it.layoutManager= LinearLayoutManager(this@ChatActivity)
                it.adapter=chatAdapter
            }
        }
    }

    private fun setListeners() {
        binding.apply {
            logOut.setOnClickListener {
                onBackPressed()
            }
            send.setOnClickListener{
                if(messageBox.text.isNotEmpty()) {
                    viewModel.sendChat(from.toString(),to.toString(),messageBox.text.toString())
                }
                messageBox.text.clear()
            }
            addChat.setOnClickListener {
                showFragment()
            }
            dp.setOnClickListener {
                selectImage()
            }
        }
    }

    private fun selectImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, 100)
    }

    private fun showFragment() {
        if(fragment==null || fragment?.isAdded==false){
            fragment= AddFriend()
            fragment?.show(supportFragmentManager, fragment?.tag)
        }
    }

    override fun onChatSelected(username: String) {
        to=username
        binding.apply{
            receiver.text=to
            notSelected.visibility= View.GONE
            selected.visibility=View.VISIBLE
        }
        utils.showCustomDialog(this@ChatActivity,"Please Wait..")
        viewModel.updateChat(from.toString(),to.toString())
    }

    private fun toast(message: String) {
        Toast.makeText(this@ChatActivity,message, Toast.LENGTH_SHORT).show()
    }

    override fun friendAdded(username: String) {
        utils.showCustomDialog(this@ChatActivity,"Please Wait..")
        viewModel.addFriend(from.toString(), username)
        fragment?.dismiss()
        fragment=null
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100 && resultCode == RESULT_OK) {
            imageUri = data?.data!!
            imageUri?.let {
                viewModel.updateImage(from.toString(),it)
                Glide.with(this)
                    .load(it)
                    .into(binding.dp)

            }
        }
    }
}