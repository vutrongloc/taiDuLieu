package com.example.taidulieu

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

open class BaseActivity : AppCompatActivity() {
    lateinit var myProgressDialog: Dialog
    fun showProgressDialog(context: Context) {
        myProgressDialog = Dialog(context)
        myProgressDialog.setContentView(R.layout.progress_dialog)
        myProgressDialog.setCancelable(false)
        myProgressDialog.setCanceledOnTouchOutside(false)
        myProgressDialog.show()

    }

    fun hideProgressDialog() {
        myProgressDialog.dismiss()
    }

    fun showToast(context: Activity, message: String, error: Boolean) {
        val inflater = LayoutInflater.from(context) // lấy LayoutInflater từ context
        val layout = inflater.inflate(R.layout.custom_toast, null)
        if (error == true) {
            layout.background = ContextCompat.getDrawable(context, R.drawable.error)
            layout.findViewById<ImageView>(R.id.custom_Toast_Image).setImageResource(R.drawable.sad)
            layout.findViewById<TextView>(R.id.custom_Toast_Message).setText(message)
        } else {
            layout.background = ContextCompat.getDrawable(context, R.drawable.success)
            layout.findViewById<ImageView>(R.id.custom_Toast_Image)
                .setImageResource(R.drawable.happy)
            layout.findViewById<TextView>(R.id.custom_Toast_Message).setText(message)
        }
        val myToast = Toast(context)
        myToast.duration = Toast.LENGTH_LONG
        myToast.view = layout
        myToast.show()
    }
}
