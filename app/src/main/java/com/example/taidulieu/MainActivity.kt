package com.example.taidulieu

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.appmusic.FireBase.Cloudinary
import com.example.appmusic.Models.Song
import com.example.taidulieu.Fragment.LuaChonFragment
import com.example.taidulieu.Fragment.TaiAlbumFragment
import com.example.taidulieu.Fragment.TaiNgheSiFragment
import com.example.taidulieu.Fragment.TaiNhacFragment
import com.example.taidulieu.Fragment.ThuTaiFragment

class MainActivity : BaseActivity(), TaiNhacFragment.OnIDPass, TaiNgheSiFragment.OnIDPass,
    TaiAlbumFragment.OnIDPass {
    lateinit var idTruyen: String
    val cloudinary = Cloudinary()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        cloudinary.init(this)
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.frameLayout, LuaChonFragment())
            commit()
        }
    }

    // Nhận dữ liệu từ Fragment qua Interface
    override fun onIDPass(id: String) {
        // Xử lý dữ liệu nhận được
        idTruyen = id
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            val ID: String = idTruyen
            // Kiểm tra data có không null
            val fileUri = Uri.parse(data?.data.toString())
            if (fileUri != null) {
                // Tiến hành upload file lên
                if (requestCode == 1) {
                    // Nếu chọn ảnh
                    val anh = findViewById<TextView>(R.id.txtTn_LinkAnh)
                    findViewById<ImageView>(R.id.imgTn_Avatar).setImageURI(fileUri)
                    cloudinary.uploadFileToCloudinary(fileUri, this, ID + "anh", anh)
                } else if (requestCode == 2) {
                    // Nếu chọn nhạc
                    val nhac = findViewById<TextView>(R.id.txtTn_LinkNhac)
                    cloudinary.uploadFileToCloudinary(fileUri, this, ID + "nhac", nhac)
                }
            } else {
                showToast(this, "FileUri bị null", true)
            }
        } else {
            showToast(this, "Hủy bỏ chọn ảnh/nhạc", true)
        }
    }
}