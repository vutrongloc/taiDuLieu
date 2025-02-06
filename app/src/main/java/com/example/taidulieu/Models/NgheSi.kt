package com.example.taidulieu.Models

import java.io.Serializable

class NgheSi(
    val NgheSiID: String,
    val TenNgheSi: String,
    val NgaySinh: String,
    val QueQuan: String,
    val NgheDanh: String,
    val ThongTin: String,
    val Song_ID: MutableList<String>,
    val AnhNgheSi: String
) : Serializable {
}
