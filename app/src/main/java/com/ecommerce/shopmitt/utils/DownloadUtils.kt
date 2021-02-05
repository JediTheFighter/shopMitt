package com.ecommerce.shopmitt.utils

import android.app.DownloadManager
import android.content.*
import android.net.Uri
import android.os.Build
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import com.ecommerce.shopmitt.R
import java.io.File
import java.util.*

class DownloadUtils() {


    fun getReceiver(): BroadcastReceiver? {
        return attachmentDownloadCompleteReceiver
    }

    /**
     * Attachment download complete receiver.
     *
     *
     * 1. Receiver gets called once attachment download completed.
     * 2. Open the downloaded file.
     */
    private val attachmentDownloadCompleteReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (DownloadManager.ACTION_DOWNLOAD_COMPLETE == action) {
                val downloadId = intent.getLongExtra(
                        DownloadManager.EXTRA_DOWNLOAD_ID, 0)
                openDownloadedAttachment(context, downloadId)
            }
        }
    }

    /**
     * Used to open the downloaded attachment.
     *
     * @param context    Content.
     * @param downloadId Id of the downloaded file to open.
     */
    private fun openDownloadedAttachment(context: Context, downloadId: Long) {
        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val query = DownloadManager.Query()
        query.setFilterById(downloadId)
        if (downloadManager != null) {
            val cursor = downloadManager.query(query)
            if (cursor.moveToFirst()) {
                val downloadStatus = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                val downloadLocalUri = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI))
                val downloadMimeType = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_MEDIA_TYPE))
                if (downloadStatus == DownloadManager.STATUS_SUCCESSFUL && downloadLocalUri != null) {
                    openDownloadedAttachment(context, Uri.parse(downloadLocalUri), downloadMimeType)
                }
            }
            cursor.close()
        }
    }

    /**
     * Used to open the downloaded attachment.
     *
     *
     * 1. Fire intent to open download file using external application.
     *
     *
     * 2. Note:
     * 2.a. We can't share fileUri directly to other application (because we will get FileUriExposedException from Android7.0).
     * 2.b. Hence we can only share content uri with other application.
     * 2.c. We must have declared FileProvider in manifest.
     * 2.c. Refer - https://developer.android.com/reference/android/support/v4/content/FileProvider.html
     *
     * @param context            Context.
     * @param attachmentUri      Uri of the downloaded attachment to be opened.
     * @param attachmentMimeType MimeType of the downloaded attachment.
     */
    private fun openDownloadedAttachment(context: Context,
                                         attachmentUri: Uri, attachmentMimeType: String) {
        var attachmentUri: Uri? = attachmentUri
        if (attachmentUri != null) { // Get Content Uri.
            if (ContentResolver.SCHEME_FILE == attachmentUri.scheme) { // FileUri - Convert it to contentUri.
                val file = File(Objects.requireNonNull(attachmentUri.path))
                attachmentUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    FileProvider.getUriForFile(AlisApplication.instance.applicationContext, AlisApplication.instance.getPackageName().toString() + ".provider", file)
                } else {
                    Uri.fromFile(file)
                }
            }
            val openAttachmentIntent = Intent(Intent.ACTION_VIEW)
            openAttachmentIntent.setDataAndType(attachmentUri, attachmentMimeType)
            openAttachmentIntent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            try { /*ActivityOptions options =
                        ActivityOptions.makeCustomAnimation(context, R.anim.slide_in_left, R.anim.slide_out_left);
                context.startActivity(openAttachmentIntent, options.toBundle());*/
                context.startActivity(openAttachmentIntent)
            } catch (e: ActivityNotFoundException) {
                ToastHelper.instance.show(context.getString(R.string.download_completed))
            }
        }
    }

    /**
     * Used to get MimeType from url.
     *
     * @param url Url.s
     * @return Mime Type for the given url.
     */
    fun getMimeType(url: String?): String? {
        var type: String? = null
        val extension = MimeTypeMap.getFileExtensionFromUrl(url)
        if (extension != null) {
            val mime = MimeTypeMap.getSingleton()
            type = mime.getMimeTypeFromExtension(extension)
        }
        return type
        //        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        //        intent .setType("*/*");
        //        String[] mimeTypes = {"image/*", "application/pdf"};
        //        intent .putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
    }

    private object HOLDER {
        val INSTANCE = DownloadUtils()
    }

    companion object {

        val instance: DownloadUtils by lazy { HOLDER.INSTANCE }
    }

}