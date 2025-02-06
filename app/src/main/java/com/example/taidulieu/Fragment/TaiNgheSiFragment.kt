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
import com.example.appmusic.Models.Song
import com.example.taidulieu.MainActivity
import com.example.taidulieu.Models.NgheSi
import com.example.taidulieu.R
import com.example.taidulieu.databinding.FragmentTaiNgheSiBinding
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class TaiNgheSiFragment : Fragment() {
    lateinit var binding: FragmentTaiNgheSiBinding
    lateinit var NgheSiID: String

    // Interface để giao tiếp với Activity
    interface OnIDPass {
        fun onIDPass(id: String)
    }

    private var nghesiPasser: OnIDPass? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnIDPass) {
            nghesiPasser = context
        } else {
            throw RuntimeException("$context phải triển khai OnIDPass")
        }
    }

    // Phương thức truyền dữ liệu qua Interface
    fun passIDToActivity() {
        val ID: String = NgheSiID
        nghesiPasser?.onIDPass(ID)
    }

    override fun onDetach() {
        super.onDetach()
        nghesiPasser = null // Tránh memory leak
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTaiNgheSiBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        taiDuLieu()
        taiAnh()
        chonNgay()
        randomId()
    }

    private fun checkTaiThongTinNgheSi(): Boolean {
        if (binding.edtTnsTenNgheSi.text.trim().toString().isNullOrEmpty()) {
            (requireActivity() as MainActivity).showToast(
                requireActivity(),
                "Tên nghệ sĩ không được để trống!",
                true
            )
            return false
        } else if (binding.txtTnsLich.text.toString().isNullOrEmpty()) {
            (requireActivity() as MainActivity).showToast(
                requireActivity(),
                "Ngày sinh nghệ sĩ không dược để trống!",
                true
            )
            return false
        } else if (binding.edtTnsQueQuan.text.toString().isNullOrEmpty()) {
            (requireActivity() as MainActivity).showToast(
                requireActivity(),
                "Quê quán nghệ sĩ không được để trống!",
                true
            )
            return false
        } else if (binding.edtTnsNgheDanh.text.trim().toString().isNullOrEmpty()) {
            (requireActivity() as MainActivity).showToast(
                requireActivity(),
                "Nghệ danh nghệ sĩ không được để trống!",
                true
            )
            return false
        } else if (binding.edtTnsThongTin.text.trim().toString().isNullOrEmpty()) {
            (requireActivity() as MainActivity).showToast(
                requireActivity(),
                "Thông tin nghệ sĩ không được để trống!",
                true
            )
            return false
        } else if (binding.edtTnsDsBaiHat.text.trim().toString().isNullOrEmpty()) {
            (requireActivity() as MainActivity).showToast(
                requireActivity(),
                "Danh sạc bài hát không được để trống!",
                true
            )
            return false
        } else if (binding.txtTnLinkAnh.text.trim().toString().isNullOrEmpty()) {
            (requireActivity() as MainActivity).showToast(
                requireActivity(),
                "Bạn chưa chọn ảnh cho nghệ sĩ",
                true
            )
            return false
        }
        return true
    }

    private fun timDsIdBaiHat(dsBaiHat: String, callback: (MutableList<String>) -> Unit) {
        val dsIdBaiHat: MutableList<String> = mutableListOf()
        val dsTenBaiHat: MutableList<String> = dsBaiHat.split(",").map { it.trim() }.toMutableList()

        Firebase.firestore.collection(Constants.SONG)
            .whereIn("tenBaiHat", dsTenBaiHat)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    document.getString("songID")?.let { dsIdBaiHat.add(it) }
                }
                callback(dsIdBaiHat)
            }
            .addOnFailureListener {
                callback(mutableListOf())
            }
    }

    private fun randomId() {
        binding.btnTnsTaiAnh.isEnabled = false
        if (binding.txtTnsRandomId.text.toString().isNullOrEmpty()) {
            (requireActivity() as MainActivity).showToast(
                requireActivity(),
                "Click vào nút Random ID",
                true
            )
        }
        binding.btnTnsRandomId.setOnClickListener() {
            NgheSiID = Firebase.firestore.collection(Constants.NGHESI).document().id
            binding.txtTnsRandomId.setText(NgheSiID)
            binding.btnTnsTaiAnh.isEnabled = true
        }
    }

    private fun layThongTin(callback: (NgheSi) -> Unit) {
        val tenNgheSi = binding.edtTnsTenNgheSi.text.trim().toString()
        val ngaySinh = binding.txtTnsLich.text.toString()
        val queQuan = binding.edtTnsQueQuan.text.trim().toString()
        val ngheDanh = binding.edtTnsNgheDanh.text.trim().toString()
        val thongTin = binding.edtTnsThongTin.text.trim().toString()
        val linkAnh = binding.txtTnLinkAnh.text.toString()

        timDsIdBaiHat(binding.edtTnsDsBaiHat.text.trim().toString()) { dsIDBaiHat ->
            val ngheSi = NgheSi(
                NgheSiID,
                tenNgheSi,
                ngaySinh,
                queQuan,
                ngheDanh,
                thongTin,
                dsIDBaiHat,
                linkAnh
            )
            callback(ngheSi)
        }
    }

    private fun taiDuLieu() {
        binding.btnTnsTaiDuLieu.setOnClickListener {
            if (checkTaiThongTinNgheSi()) {
                layThongTin { ngheSi ->
                    FirestoreClass().taiDuLieuLenFirestore(
                        Constants.NGHESI,
                        NgheSiID,
                        ngheSi,
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

    fun chonNgay() {
        binding.imgTnsLich.setOnClickListener() {
            DatePickerDialog(
                requireActivity(),
                DatePickerDialog.OnDateSetListener { datePicker, i, i2, i3 ->
                    binding.txtTnsLich.setText("${i3}/${i2 + 1}/${i}")
                },
                2000,
                2,
                2
            ).show()
        }
    }

    private fun chonAnh() {
        val galleryIntent = Intent(Intent.ACTION_GET_CONTENT)
        galleryIntent.type = "image/*"
        requireActivity().startActivityForResult(galleryIntent, 1)
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