package com.ashray.letschat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ashray.letschat.databinding.ItemFriendBinding
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage

interface RecyclerViewClickListener {
    fun onChatSelected(username: String)
}

class FriendsAdapter(private val listener: RecyclerViewClickListener) : RecyclerView.Adapter<FriendsAdapter.ViewHolder>() {

    var friends = listOf<String>()

    @JvmName("function")
    fun setFriends(friendList: List<String>){
        friends=friendList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendsAdapter.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding=ItemFriendBinding.inflate(inflater,parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FriendsAdapter.ViewHolder, position: Int) {
        val model=friends[position]
        holder.bind(model)
    }

    override fun getItemCount(): Int {
        return friends.size
    }

    inner class ViewHolder(private val binding: ItemFriendBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind(username:String){
            binding.apply {
                val storagename = FirebaseStorage.getInstance().reference.child("$username")
                storagename.downloadUrl.addOnSuccessListener {uri->
                    Glide.with(itemView)
                        .load(uri)
                        .into(dp)
                }
                userName.text=username
                itemView.setOnClickListener {
                    listener.onChatSelected(username)
                }
            }
        }
    }
}