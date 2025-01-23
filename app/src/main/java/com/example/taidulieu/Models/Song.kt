package com.example.appmusic.Models

import java.io.Serializable

class Song(
    val SongID: String,
    val TenBaiHat: String,
    val Album_ID: MutableList<String>,
    val NgheSi_ID: MutableList<String>,
    val TheLoai: String,
    val NgayPhatHanh: String,
    val Time: Int,
    val LinkNhac: String,
    val AnhSong: String
) : Serializable {
}