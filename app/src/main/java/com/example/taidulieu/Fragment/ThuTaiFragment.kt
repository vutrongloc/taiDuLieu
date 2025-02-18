package com.example.taidulieu.Fragment

import android.content.ContentValues
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.appmusic.Models.Constants
import com.example.taidulieu.databinding.FragmentThuTaiBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ThuTaiFragment : Fragment() {
    private lateinit var binding: FragmentThuTaiBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentThuTaiBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnThuTai.setOnClickListener {
            val fileUrl =
                "https://res.cloudinary.com/dmf1oito6/video/upload/v1737188798/7Vq8dze0hbae34VDfNdcnhac.mp3"
            downloadFile(fileUrl)
        }
    }
    fun layThongTinTuMeTaDaTa(localFilePath:String){
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(localFilePath) // Đường dẫn file offline

        val title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE) ?: "Không có tiêu đề"
        val artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST) ?: "Không rõ nghệ sĩ"
        //val album = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM) ?: "Không có album"
        retriever.release()
        binding.txtNgheSi.setText(artist)
        binding.txtTieuDe.setText(title)
    }
    private suspend fun layTenNhac(linkNhac: String):String {
        var tenFile = "rong"
        var idNgheSi: MutableList<String> = mutableListOf()
        val song = Firebase.firestore.collection(Constants.SONG).whereEqualTo("linkNhac", linkNhac).get().await()
        for (document in song) {
            tenFile = document.getString("tenBaiHat") + "-"
            idNgheSi = document.get("ngheSi_ID") as MutableList<String>
        }

        var ngheSi = Firebase.firestore.collection(Constants.NGHESI)
            .whereEqualTo("ngheSiID", idNgheSi[0])
            .get().await()
        for (document in ngheSi) {
            tenFile += document.getString("tenNgheSi").toString()
        }
        return tenFile
    }
    private fun downloadFile(fileUrl: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val context = requireContext()
                val tenFile = layTenNhac(fileUrl)
                val fileName = "$tenFile.mp3"
                val outputStream: OutputStream?
                val uri: Uri?

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val contentResolver = context.contentResolver
                    val contentValues = ContentValues().apply {
                        put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                        put(MediaStore.MediaColumns.MIME_TYPE, "audio/mpeg")
                        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                    }
                    uri = contentResolver.insert(
                        MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                        contentValues
                    )
                    outputStream = uri?.let { contentResolver.openOutputStream(it) }
                } else {
                    val downloadDir =
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    val file = File(downloadDir, fileName)
                    outputStream = FileOutputStream(file)
                    uri = Uri.fromFile(file)
                }

                if (outputStream == null) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Lỗi khi tạo file", Toast.LENGTH_SHORT).show()
                    }
                    return@launch
                }

                val url = URL(fileUrl)
                val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.doInput = true
                connection.connect()

                val inputStream: InputStream = connection.inputStream
                outputStream.use { out -> inputStream.use { inp -> inp.copyTo(out) } }

                // Lấy đường dẫn file offline
                val filePath = uri?.path ?: "Không lấy được đường dẫn"
                //Log.d("DownloadPath", "File offline lưu tại: $filePath")
                layThongTinTuMeTaDaTa(filePath)
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Tải xuống thành công: $filePath", Toast.LENGTH_LONG)
                        .show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        requireContext(),
                        "Lỗi khi tải file: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}