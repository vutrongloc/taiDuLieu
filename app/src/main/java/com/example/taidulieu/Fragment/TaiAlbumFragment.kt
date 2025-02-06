package com.example.taidulieu.Fragment

import android.Manifest
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.appmusic.FireBase.FirestoreClass
import com.example.appmusic.Models.Constants
import com.example.taidulieu.MainActivity
import com.example.taidulieu.Models.Album
import com.example.taidulieu.R
import com.example.taidulieu.databinding.FragmentTaiAlbumBinding
import com.example.taidulieu.databinding.FragmentTaiNgheSiBinding
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.callbackFlow

class TaiAlbumFragment : Fragment() {
    lateinit var binding: FragmentTaiAlbumBinding
    lateinit var AlbumID: String

    // Interface để giao tiếp với Activity
    interface OnIDPass {
        fun onIDPass(id: String)
    }

    private var albumPasser: OnIDPass? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnIDPass) {
            albumPasser = context
        } else {
            throw RuntimeException("$context phải triển khai OnIDPass")
        }
    }

    // Phương thức truyền dữ liệu qua Interface
    fun passIDToActivity() {
        val ID: String = AlbumID
        albumPasser?.onIDPass(ID)
    }

    override fun onDetach() {
        super.onDetach()
        albumPasser = null // Tránh memory leak
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTaiAlbumBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        randomID()
        taiAnh()
        taiDuLieu()
        chonNgay()
    }

    private fun randomID() {
        binding.btnTnsTaiAnh.isEnabled = false
        if (binding.txtTaRandomId.text.toString().isNullOrEmpty()) {
            (requireActivity() as MainActivity).showToast(
                requireActivity(),
                "Click vào nút Random ID",
                true
            )
        }
        binding.btnTaRandomId.setOnClickListener() {
            AlbumID = Firebase.firestore.collection(Constants.ALBUM).document().id
            binding.txtTaRandomId.setText(AlbumID)
            binding.btnTnsTaiAnh.isEnabled = true
        }
    }

    private fun checkTaiAlbum(): Boolean {
        if (binding.edtTaTenAlbum.text.toString().isNullOrEmpty()) {
            (requireActivity() as MainActivity).showToast(
                requireActivity(),
                "Tên Album không được để trống!",
                true
            )
            return false
        } else if (binding.edtTaDsNgheSi.text.toString().isNullOrEmpty()) {
            (requireActivity() as MainActivity).showToast(
                requireActivity(),
                "Danh sách tên nghệ sĩ không được để trống!",
                true
            )
            return false
        } else if (binding.edtTaDsBaiHat.text.toString().isNullOrEmpty()) {
            (requireActivity() as MainActivity).showToast(
                requireActivity(),
                "Danh sách tên bài hát không được để trống!",
                true
            )
            return false
        } else if (binding.txtTaLich.text.toString().isNullOrEmpty()) {
            (requireActivity() as MainActivity).showToast(
                requireActivity(),
                "Ngày phát hành không được để trống!",
                true
            )
            return false
        } else if (binding.txtTnLinkAnh.text.toString().isNullOrEmpty()) {
            (requireActivity() as MainActivity).showToast(
                requireActivity(),
                "Bạn chưa chọn ảnh!",
                true
            )
            return false
        }
        return true
    }

    private fun timDsBaiHat(dsBaiHat: String, callback: (MutableList<String>) -> Unit) {
        val dsIdBaiHat: MutableList<String> = mutableListOf()
        var dsTenBaiHat: MutableList<String> = mutableListOf()
        if (dsBaiHat.contains(",")) {
            dsTenBaiHat = dsBaiHat.split(",").toMutableList()
        } else {
            dsTenBaiHat.add(dsBaiHat)
        }
        for (BaiHat in dsTenBaiHat.indices) {
            dsTenBaiHat[BaiHat] = dsTenBaiHat[BaiHat].trim()
        }
        Firebase.firestore.collection(Constants.SONG).whereIn("tenBaiHat", dsTenBaiHat).get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    dsIdBaiHat.add(document.getString("songID").toString())
                }
                callback(dsIdBaiHat)
            }.addOnFailureListener {
                callback(mutableListOf())
            }
    }

    private fun timDsNgheSi(dsNgheSi: String, callback: (MutableList<String>) -> Unit) {
        val dsIdNgheSi: MutableList<String> = mutableListOf()
        var dsTenNgheSi: MutableList<String> = mutableListOf()
        if (dsNgheSi.contains(",")) {
            dsTenNgheSi = dsNgheSi.split(",").toMutableList()
        } else {
            dsTenNgheSi.add(dsNgheSi)
        }
        for (NgheSi in dsTenNgheSi.indices) {
            dsTenNgheSi[NgheSi] = dsTenNgheSi[NgheSi].trim()
        }
        Firebase.firestore.collection(Constants.NGHESI).whereIn("tenNgheSi", dsTenNgheSi).get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    dsIdNgheSi.add(document.getString("ngheSiID").toString())
                }
                callback(dsIdNgheSi)
            }.addOnFailureListener {
                callback(mutableListOf())
            }
    }

    private fun layThongTin(callback: (Album) -> Unit) {
        val tenAlbum: String = binding.edtTaTenAlbum.text.toString().trim()
        var count: Int = 0
        val ngayPhatHanh: String = binding.txtTaLich.text.toString()
        val anhAlbum: String = binding.txtTnLinkAnh.text.toString()
        var ngheSi_ID: MutableList<String> = mutableListOf()
        var song_ID: MutableList<String> = mutableListOf()
        timDsBaiHat(binding.edtTaDsBaiHat.text.toString().trim(), { dsIDSong ->
            if (dsIDSong.size == 0) {
                (requireActivity() as MainActivity).showToast(
                    requireActivity(),
                    "Danh sách Id bài hát bị rỗng!",
                    true
                )
            } else {
                song_ID.addAll(dsIDSong)
            }
            count++
            if (count == 2) {
                callback(Album(AlbumID, tenAlbum, ngheSi_ID, ngayPhatHanh, song_ID, anhAlbum))
            }
        })
        timDsNgheSi(binding.edtTaDsNgheSi.text.toString().trim(), { dsIDNgheSi ->
            if (dsIDNgheSi.size == 0) {
                (requireActivity() as MainActivity).showToast(
                    requireActivity(),
                    "Danh sách Id nghệ sĩ bị rỗng!",
                    true
                )
            } else {
                ngheSi_ID.addAll(dsIDNgheSi)
            }
            count++
            if (count == 2) {
                callback(Album(AlbumID, tenAlbum, ngheSi_ID, ngayPhatHanh, song_ID, anhAlbum))
            }
        })
    }

    private fun taiDuLieu() {
        binding.btnTnsTaiDuLieu.setOnClickListener() {
            if (checkTaiAlbum()) {
                layThongTin { Album ->
                    FirestoreClass().taiDuLieuLenFirestore(
                        Constants.ALBUM,
                        AlbumID,
                        Album,
                        requireActivity()
                    )
                }
            }
        }
    }

    private fun taiAnh() {
        binding.btnTnsTaiAnh.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    requireActivity(),
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
                == PackageManager.PERMISSION_GRANTED
            ) {
                passIDToActivity()
                chonAnh()
            } else {
                ActivityCompat.requestPermissions(
                    requireActivity(), arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    1
                )
            }
        }
    }

    private fun chonAnh() {
        val galleryIntent = Intent(Intent.ACTION_GET_CONTENT)
        galleryIntent.type = "image/*"
        requireActivity().startActivityForResult(galleryIntent, 1)
    }

    fun chonNgay() {
        binding.imgTaLich.setOnClickListener() {
            DatePickerDialog(
                requireActivity(),
                DatePickerDialog.OnDateSetListener { datePicker, i, i2, i3 ->
                    binding.txtTaLich.setText("${i3}/${i2 + 1}/${i}")
                },
                2000,
                2,
                2
            ).show()
        }
    }

    //yeu cau nguoi dung cap phep de chon anh hoac nhac
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                chonAnh()
            } else {
                (requireActivity() as MainActivity).showToast(
                    requireActivity(),
                    "bạn vừa từ chối quyền truy cập bộ nhớ ngoài!",
                    true
                )
            }
        }
    }
}