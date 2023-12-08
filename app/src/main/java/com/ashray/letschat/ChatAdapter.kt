package com.ashray.letschat

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ashray.letschat.databinding.ItemReceivedBinding
import com.ashray.letschat.databinding.ItemSentBinding

class ChatAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val SENT = 1
    private val RECEIVED = 2
    var chats = listOf<Message>()

    @JvmName("differ")
    fun setChats(messages : List<Message>){
        chats=messages
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return if (chats[position].sender % 2 == 1) {
            SENT
        } else {
            RECEIVED
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            SENT -> {
                val inflater = LayoutInflater.from(parent.context)
                val binding= ItemSentBinding.inflate(inflater,parent,false)
                ViewHolderSent(binding)
            }
            RECEIVED -> {
                val inflater = LayoutInflater.from(parent.context)
                val binding= ItemReceivedBinding.inflate(inflater,parent,false)
                ViewHolderReceived(binding)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun getItemCount(): Int {
        Log.i("Ashdgdhray",chats.size.toString())
        return chats.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model=chats[position]
        val viewType = getItemViewType(position)
        when (viewType) {
            SENT -> {
                val viewHolderTypeOne = holder as ViewHolderSent
                viewHolderTypeOne.bind(model)
            }
            RECEIVED -> {
                val viewHolderTypeTwo = holder as ViewHolderReceived
                viewHolderTypeTwo.bind(model)
            }
        }
    }

    inner class ViewHolderSent(private val binding: ItemSentBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(data: Message) {
            binding.chatsent.text=data.message
        }
    }

    inner class ViewHolderReceived(private val binding: ItemReceivedBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(data: Message) {
            binding.chatreceived.text=data.message
        }
    }

}