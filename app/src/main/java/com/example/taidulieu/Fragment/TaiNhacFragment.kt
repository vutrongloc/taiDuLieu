package com.example.taidulieu.Fragment

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.appmusic.FireBase.Cloudinary
import com.example.appmusic.FireBase.FirestoreClass
import com.example.appmusic.Models.Constants
import com.example.appmusic.Models.Song
import com.example.taidulieu.MainActivity
import com.example.taidulieu.databinding.FragmentTaiNhacBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlin.concurrent.timerTask

class TaiNhacFragment : Fragment() {
    lateinit var binding: FragmentTaiNhacBinding
    lateinit var SongID: String

    // Interface để giao tiếp với Activity
    interface OnIDPass {
        fun onIDPass(id: String)
    }

    private var songPasser: OnIDPass? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnIDPass) {
            songPasser = context
        } else {
            throw RuntimeException("$context phải triển khai OnUserPass")
        }
    }

    // Phương thức truyền dữ liệu qua Interface
    fun passIDToActivity() {
        val ID: String = SongID
        songPasser?.onIDPass(ID)
    }

    override fun onDetach() {
        super.onDetach()
        songPasser = null // Tránh memory leak
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTaiNhacBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val instance = requireActivity() as MainActivity
        binding.btnTnTaiNhac.isEnabled = false
        binding.btnTnTaiAnh.isEnabled = false
        taiDuLieuLenFireStore(instance)
        chonNgay()
        taiAnhVaNhac()
        randomID()
    }

    fun checkTaiNhac(activity: MainActivity): Boolean {
        if (binding.edtTnTenBaiHat.text.toString().trim().isNullOrEmpty()) {
            binding.edtTnTenBaiHat.error = "Tên bài hát không được để trống!"
            return false
        } else if (binding.edtTnTheLoai.text.toString().trim().isNullOrEmpty()) {
            binding.edtTnTheLoai.error = "Thể loại nhạc không được để trống!"
            return false
        } else if (binding.txtTnLich.text.toString().trim().isNullOrEmpty()) {
            activity.showToast(activity, "Ngày phát hành không được để trống!", true)
            return false
        } else if (binding.edtTnTime.text.toString().trim().isNullOrEmpty()) {
            binding.edtTnTime.error = "Thời lượng bài hát không được để trống!"
            return false
        } else if (binding.txtTnLinkAnh.text.toString().isNullOrEmpty()) {
            activity.showToast(activity, "Hãy tải một bức ảnh!", true)
            return false
        } else if (binding.txtTnLinkNhac.text.toString().isNullOrEmpty()) {
            activity.showToast(activity, "Hãy tải một bài nhạc!", true)
            return false
        }
        return true
    }

    fun randomID() {
        if (binding.txtTnHienId.text.toString().isNullOrEmpty()) {
            (requireActivity() as MainActivity).showToast(
                requireActivity(),
                "click vào random id",
                true
            )
        }
        binding.btnTnRandomId.setOnClickListener() {
            SongID = Firebase.firestore.collection(Constants.SONG).document().id.toString()
            binding.txtTnHienId.setText(SongID)
            binding.btnTnTaiAnh.isEnabled = true
            binding.btnTnTaiNhac.isEnabled = true
        }
    }

    fun checkTaiTrungNhac(tenBaiHat: String): Task<Boolean> {
        return Firebase.firestore.collection(Constants.SONG)
            .whereEqualTo("tenBaiHat", tenBaiHat)
            .get()
            .continueWith { task ->
                val documents = task.result
                val isAvailable = documents.isEmpty // true nếu không tồn tại bài hát
                if (!isAvailable) {
                    (requireActivity() as MainActivity).showToast(
                        requireActivity(),
                        "Bài hát đã tồn tại!",
                        true
                    )
                }
                isAvailable
            }
    }

    fun taoThongTinSong(ID: String): Song {
        val dsRong: MutableList<String> = mutableListOf()
        var time: Int = 0
        if (!binding.edtTnTime.text.toString().trim().isNullOrEmpty()) {
            time = binding.edtTnTime.text.toString().toInt()
        }
        var id = "song"
        if (!ID.isNullOrEmpty()) {
            id = ID
        }
        return Song(
            id,
            binding.edtTnTenBaiHat.text.toString().trim(),
            dsRong,
            dsRong,
            binding.edtTnTheLoai.text.toString().trim(),
            binding.txtTnLich.text.toString().trim(),
            time,
            binding.txtTnLinkNhac.text.toString(),
            binding.txtTnLinkAnh.text.toString()
        )
    }

    fun taiDuLieuLenFireStore(activity: MainActivity) {
        binding.btnTnTaiDuLieu.setOnClickListener() {
            val song: Song = taoThongTinSong(SongID)
            if (checkTaiNhac(activity) == true) {
//                checkTaiTrungNhac(song.TenBaiHat)
//                    .addOnSuccessListener { isAvailable ->
//                        if (isAvailable) {
                FirestoreClass().taiDuLieuLenFirestore(
                    Constants.SONG,
                    song.SongID,
                    song,
                    requireActivity()
                )
//                        }
//                    }
            }
        }
    }

    fun chonNgay() {
        binding.imgTnLich.setOnClickListener() {
            DatePickerDialog(
                requireActivity(),
                DatePickerDialog.OnDateSetListener { datePicker, i, i2, i3 ->
                    binding.txtTnLich.setText("${i3}/${i2 + 1}/${i}")
                },
                2000,
                2,
                2
            ).show()
        }
    }

    fun taiAnhVaNhac() {
        binding.btnTnTaiNhac.setOnClickListener() {
            /*kiểm tra quyền đọc dữ liệu từ bộ nhớ ngoài nếu đã cấp quyền thì truyền dữ liệu về activity cha nếu
            không thì sẽ chạy vào hàm else yêu cầu người dùng cấp quyền
            */
            if (ContextCompat.checkSelfPermission(
                    requireActivity(),
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
                == PackageManager.PERMISSION_GRANTED
            ) {
                passIDToActivity()
                chonNhac()
            } else {
                ActivityCompat.requestPermissions(
                    requireActivity(), arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    2
                )
            }
        }
        binding.btnTnTaiAnh.setOnClickListener() {
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

    fun chonAnh() {
        val galleryIntent = Intent(Intent.ACTION_GET_CONTENT)
        galleryIntent.type = "image/*"
        requireActivity().startActivityForResult(galleryIntent, 1)
    }

    fun chonNhac() {
        val galleryIntent = Intent(Intent.ACTION_GET_CONTENT)
        galleryIntent.type = "audio/*"
        requireActivity().startActivityForResult(galleryIntent, 2)
    }

    //yeu cau nguoi dung cap phep de chon anh hoac nhac
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 2) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                chonNhac()
            } else {
                (requireActivity() as MainActivity).showToast(
                    requireActivity(),
                    "bạn vừa từ chối quyền truy cập bộ nhớ ngoài!",
                    true
                )
            }
        } else if (requestCode == 1) {
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