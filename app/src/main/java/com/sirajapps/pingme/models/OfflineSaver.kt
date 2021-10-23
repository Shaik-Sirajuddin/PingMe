package com.sirajapps.pingme.models

import android.app.Activity
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.lang.Exception

class OfflineSaver{
    companion object{
        const val STATUS_PATH = "/PingMe/Media/Status/"
        const val CHAT_IMAGE_PATH = "/PingMe/Media/PingMeImages/"
        const val CHAT_VIDEO_PATH = "/PingMe/Media/PingMeVideos/"
        const val PROFILE_IMAGES_PATH = "/PingMe/.Secret/ProfileImages/"
        const val VIDEO = ".mp4"
        const val IMAGE = ".jpeg"

        fun getFileUri(context:Activity,filePath: String, fileName: String): Uri {
            val path = File(context.externalMediaDirs[0].absolutePath + filePath + fileName)
            return Uri.fromFile(path)
        }
        fun getAbsoluteFilePath(context: Activity,filePath:String,fileName: String):String{
            return File(context.externalMediaDirs[0].absolutePath+filePath+fileName).absolutePath
        }
        fun copyFile(context: Activity,fileUri: Uri? = null,
                              inputStream: FileInputStream? = null,
                              fileName:String, outputPath: String): String {
            try {
                val input = inputStream ?: context.contentResolver.openInputStream(fileUri!!)!!
                val imageFinalPath = getAbsoluteFilePath(context,outputPath,fileName)
                val imgpath = getAbsoluteFilePath(context,outputPath,"")
                if(!File(imgpath).exists()){
                    File(imgpath).mkdirs()
                }
                val out = FileOutputStream(imageFinalPath)
                val buffer = ByteArray(1024)
                var read: Int
                while (input.read(buffer).also { read = it } != -1) {
                    out.write(buffer, 0, read)
                }
                input.close()
                out.flush()
                out.close()
                return imageFinalPath.toUri().toString()
            } catch (exception: FileNotFoundException) {
                Log.e("copyFile", exception.message.toString())
                return ""
            } catch (e: Exception) {
                Log.e("copyFile", e.message.toString())
                return ""
            }

        }
        fun downloadImage(context: Activity,uri: Uri,onDownloadComplete:(bitmap: Bitmap)->Unit) {
            CoroutineScope(Dispatchers.IO).launch {
                onDownloadComplete(Glide.with(context)
                    .asBitmap()
                    .load(uri)
                    .placeholder(android.R.drawable.progress_indeterminate_horizontal)
                    .error(android.R.drawable.stat_notify_error)
                    .submit()
                    .get())
            }
        }
    }
}