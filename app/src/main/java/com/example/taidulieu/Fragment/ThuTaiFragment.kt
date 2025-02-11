package com.example.taidulieu.Fragment

import android.content.ContentValues
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
import com.example.taidulieu.databinding.FragmentThuTaiBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL

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
            val fileUrl = "https://res.cloudinary.com/dmf1oito6/video/upload/v1738561701/illit-%EC%95%84%EC%9D%BC%EB%A6%BF-magnetic-official-mv_aogmxt.mp3"
            downloadFile(fileUrl)
        }
    }

    private fun downloadFile(fileUrl: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val context = requireContext()
            val fileName = "my_song.mp3"

            try {
                val outputStream: OutputStream?
                val uri: Uri?

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    // Android 10+ dùng MediaStore
                    val contentResolver = context.contentResolver
                    val contentValues = ContentValues().apply {
                        put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                        put(MediaStore.MediaColumns.MIME_TYPE, "audio/mpeg")
                        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                    }
                    uri = contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                    outputStream = uri?.let { contentResolver.openOutputStream(it) }
                } else {
                    // Android 9 trở xuống: Lưu vào thư mục /storage/emulated/0/Download/
                    val downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
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

                // Kết nối tải file
                val url = URL(fileUrl)
                val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.doInput = true
                connection.connect()

                val inputStream: InputStream = connection.inputStream
                inputStream.copyTo(outputStream)

                outputStream.close()
                inputStream.close()

                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Tải xuống thành công vào thư mục Download", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Lỗi khi tải file: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
