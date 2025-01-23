package com.example.appmusic.FireBase

import android.app.Activity
import com.example.taidulieu.MainActivity
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import java.io.Serializable

class FirestoreClass {
    fun taiDuLieuLenFirestore(
        collection: String,
        documented: String,
        Item: Serializable,
        activity: Activity
    ) {
        Firebase.firestore
            .collection(collection)
            .document(documented).set(Item).addOnSuccessListener {
                (activity as MainActivity).showToast(
                    activity, "Thêm thông tin vào firebase thành công!",
                    false
                )
            }.addOnFailureListener { e ->
                (activity as MainActivity).showToast(
                    activity,
                    "Thêm thông tin vào firebase thất bại! ${e}",
                    true
                )
            }
    }
}