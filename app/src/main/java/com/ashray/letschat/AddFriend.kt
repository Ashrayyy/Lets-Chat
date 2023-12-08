package com.ashray.letschat

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.ashray.letschat.databinding.FragmentAddFriendBinding

class AddFriend : DialogFragment() {
    private lateinit var binding: FragmentAddFriendBinding
    private var clickListener: confirmation?=null

    interface confirmation{
        fun friendAdded(username : String)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=DataBindingUtil.inflate(inflater,R.layout.fragment_add_friend,container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()

        val dialog = dialog
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.WRAP_CONTENT
            dialog.window?.setLayout(width, height)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            clickListener = context as confirmation
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement DialogListener")
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
    }

    private fun setListeners() {
        binding.apply {
            addFriend.setOnClickListener {
                clickListener?.friendAdded(friendUsername.text.toString())
            }
        }
    }

}