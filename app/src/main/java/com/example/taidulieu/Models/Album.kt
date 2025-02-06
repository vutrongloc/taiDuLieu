package com.example.taidulieu.Models

import java.io.Serializable

class Album(
    val AlbumID: String,
    val TenAlbum: String,
    val NgheSi_ID: MutableList<String>,
    val NgayPhatHanh: String,
    val Song_ID: MutableList<String>,
    val AnhAlbum: String
) : Serializable {
}