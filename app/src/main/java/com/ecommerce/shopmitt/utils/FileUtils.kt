package com.ecommerce.shopmitt.utils

import android.net.Uri
import android.os.Environment
import com.ecommerce.shopmitt.BuildConfig
import java.io.File

class FileUtils {

    // private static final String TAG = "com.shopping.alis.utils.FileUtils";
    fun createApplicationFolder() {
        var f = File(Environment.getExternalStorageDirectory(), File.separator + Constants.APP_FOLDER)
        f.mkdirs()
        f = File(Environment.getExternalStorageDirectory(), File.separator + Constants.APP_FOLDER + Constants.FOLDER_PROFILE)
        f.mkdirs()
    }

    fun getFilePathName(folderName: String, fileName: String): File? {
        val storageDir = File(Environment.getExternalStorageDirectory().toString(),
                Constants.APP_FOLDER + File.separator + folderName)
        val b = storageDir.mkdirs()
        val fName = fileName + System.currentTimeMillis() + ".jpg"
        return File(storageDir, fName)
    }

    fun getUri(res: Int): Uri? {
        return Uri.parse("android.resource://" + BuildConfig.APPLICATION_ID + "/" + res)
    }

    private object HOLDER {
        val INSTANCE = FileUtils()
    }

    companion object {

        val instance: FileUtils by lazy { HOLDER.INSTANCE }
    }
}