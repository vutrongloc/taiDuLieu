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
import com.example.taidulieu.Fragment.TaiNhacFragment

class MainActivity : BaseActivity(), TaiNhacFragment.OnSongPass {
    lateinit var songtruyen:Song
    val cloudinary = Cloudinary()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        cloudinary.init(this)
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.frameLayout,TaiNhacFragment())
            commit()
        }
    }
    // Nhận dữ liệu từ Fragment qua Interface
    override fun onSongPass(song: Song) {
        // Xử lý dữ liệu nhận được
        songtruyen = song
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            val song: Song = songtruyen
            // Kiểm tra data có không null
            val fileUri = Uri.parse(data?.data.toString())
            if (fileUri != null) {
                // Tiến hành upload file lên
                if (requestCode == 1) {
                    // Nếu chọn ảnh
                    val anh =  findViewById<TextView>(R.id.txtTn_LinkAnh)
                    findViewById<ImageView>(R.id.imgTn_Avatar).setImageURI(fileUri)
                    cloudinary.uploadFileToCloudinary(fileUri, this, song.SongID+"anh",anh)
                } else if (requestCode == 2) {
                    // Nếu chọn nhạc
                    val nhac = findViewById<TextView>(R.id.txtTn_LinkNhac)
                    cloudinary.uploadFileToCloudinary(fileUri, this, song.SongID+"nhac",nhac)
                }
            } else {
                showToast(this, "FileUri bị null", true)
            }
        } else {
            showToast(this, "Hủy bỏ chọn ảnh/nhạc", true)
        }
    }
}