package com.ashray.letschat

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Window
import androidx.databinding.DataBindingUtil
import com.ashray.letschat.databinding.CustomProgressDialogBinding

class CustomProgressDialog(private val context: Context, private var message: String) : Dialog(context) {
    private lateinit var binding: CustomProgressDialogBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.custom_progress_dialog, null, false)
        setContentView(binding.root)
        binding.messageTv.text = message
        setCancelable(false)
    }

}