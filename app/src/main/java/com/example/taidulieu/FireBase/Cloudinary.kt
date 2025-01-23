package com.example.appmusic.FireBase

import android.content.Context
import android.net.Uri
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.example.taidulieu.MainActivity

class Cloudinary {
    fun init(context: Context) {
        val config: HashMap<String, String> = hashMapOf(
            "cloud_name" to "dmf1oito6",
            "api_key" to "257479671739524",
            "api_secret" to "eFqn1R83Hd0RhgzhRWh6n3e8wxw"
        )
        MediaManager.init(context, config)
    }

    fun uploadFileToCloudinary(uri: Uri, context: Context,id:String,view: TextView) {
        val activity = MainActivity()
        activity.showProgressDialog(context)
        MediaManager.get().upload(uri).option("resource_type","auto").option("public_id",id).callback(object : UploadCallback {
            override fun onSuccess(requestId: String?, resultData: MutableMap<Any?, Any?>?) {
                Toast.makeText(context, "Tải lên thành công!", Toast.LENGTH_SHORT).show()
                view.setText(resultData?.get("secure_url").toString())
                activity.hideProgressDialog()
            }

            override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {

            }

            override fun onReschedule(requestId: String?, error: ErrorInfo?) {

            }

            override fun onError(requestId: String?, error: ErrorInfo?) {
                activity.hideProgressDialog()
                Toast.makeText(
                    context,
                    "Tải lên thất bại!" + error?.description,
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onStart(requestId: String?) {
            }
        }).dispatch()
    }
}