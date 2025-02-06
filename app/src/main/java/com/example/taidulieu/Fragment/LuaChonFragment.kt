package com.example.taidulieu.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.taidulieu.R
import com.example.taidulieu.databinding.FragmentLuaChonBinding
import com.example.taidulieu.databinding.FragmentTaiNhacBinding

class LuaChonFragment : Fragment() {
    lateinit var binding: FragmentLuaChonBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLuaChonBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnLcTaiBaiHat.setOnClickListener {
            chuyenSangTrangTaiNhac()
        }
        binding.btnLcTaiNgheSi.setOnClickListener {
            chuyenSangTrangTaiThongTinNgheSi()
        }
        binding.btnLcTaiAlbum.setOnClickListener {
            chuyenSangTrangTaiThongTinAlbum()
        }
    }

    private fun chuyenSangTrangTaiNhac() {
        requireActivity().supportFragmentManager.beginTransaction().apply {
            replace(R.id.frameLayout, TaiNhacFragment())
            commit()
        }
    }

    private fun chuyenSangTrangTaiThongTinNgheSi() {
        requireActivity().supportFragmentManager.beginTransaction().apply {
            replace(R.id.frameLayout, TaiNgheSiFragment())
            commit()
        }
    }

    private fun chuyenSangTrangTaiThongTinAlbum() {
        requireActivity().supportFragmentManager.beginTransaction().apply {
            replace(R.id.frameLayout, TaiAlbumFragment())
            commit()
        }
    }
}