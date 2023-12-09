package com.ashray.letschat

import android.content.Context

class Utils {
    private var progressDialog:CustomProgressDialog?=null

    fun showCustomDialog(context: Context, message: String) {
        if (progressDialog == null || progressDialog?.isShowing == false) {
            progressDialog = CustomProgressDialog(context, message)
            progressDialog?.show()
        }
    }

    fun hideCustomDialog() {
        progressDialog?.dismiss()
        progressDialog = null
    }
}