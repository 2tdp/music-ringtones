package com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.view.Gravity
import android.widget.Toast
import androidx.core.content.FileProvider
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.R

import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.helpers.GMAIL
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.helpers.NAME_DEV
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object ActionUtils {

    private const val AUTHORITY = "com.remi.ringtones.audiocutter.ringtonemaker.freeringtone"
    private const val POLICY = ""
    private const val HOW_TO_USE = ""
    private const val ID_DEV = ""
    private const val LINK_FACE = "https://www.facebook.com/REMI-Studio-111335474750038"
    private const val LINK_FACE_ID = "111335474750038"
    private const val LINK_INS = "remi_studio_app"

    fun openOtherApps(c: Context) {
        val intent = Intent(Intent.ACTION_VIEW)
        val uriBuilder = Uri.parse("https://play.google.com/store/apps/dev")
            .buildUpon()
            .appendQueryParameter("id", ID_DEV)
        intent.data = uriBuilder.build()
        try {
            c.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
        }
    }

    fun openPolicy(c: Context) {
        openLink(c, POLICY)
    }

    fun openHowToUse(c: Context) {
        openLink(c, HOW_TO_USE)
    }

    fun openFacebook(c: Context) {
        try {
            val applicationInfo = c.packageManager.getApplicationInfo("com.facebook.katana", 0)
            if (applicationInfo.enabled) {
                val uri = Uri.parse("fb://page/$LINK_FACE_ID")
                val intent = Intent(Intent.ACTION_VIEW, uri)
                c.startActivity(intent)
                return
            }
        } catch (ignored: Exception) {
        }
        openLink(c, LINK_FACE)
    }

    fun openInstagram(c: Context) {
        val appUri = Uri.parse("https://instagram.com/_u/$LINK_INS")
        try {
            val appIntent: Intent? =
                c.packageManager.getLaunchIntentForPackage("com.instagram.android")
            if (appIntent != null) {
                appIntent.action = Intent.ACTION_VIEW
                appIntent.data = appUri
                c.startActivity(appIntent)
                return
            }
        } catch (ignored: Exception) {
        }
        openLink(c, "https://instagram.com/$LINK_INS")
    }

    fun rateApp(context: Context) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("https://play.google.com/store/apps/details?id=" + context.packageName)
        }
        context.startActivity(intent)
    }

    fun shareApp(context: Context) {
        try {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, context.resources.getString(R.string.app_name))
            }
            var shareMessage = "Let me recommend you this application\nDownload now:\n\n"
            shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + context.packageName
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
            context.startActivity(Intent.createChooser(shareIntent, "choose one"))
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun sendFeedback(context: Context) {
        val selectorIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
        }
        val emailIntent = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_EMAIL, arrayOf(GMAIL))
            putExtra(Intent.EXTRA_SUBJECT, context.resources.getString(R.string.app_name) + " feedback")
            putExtra(Intent.EXTRA_TEXT, "")
            selector = selectorIntent
        }
        try {
            context.startActivity(Intent.createChooser(emailIntent, ""))
        } catch (ex: ActivityNotFoundException) {
            Toast.makeText(context, "No email clients installed.", Toast.LENGTH_SHORT).show()
        }
    }

    fun moreApps(context: Context) {
        try {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=pub:$NAME_DEV")))
        } catch (anfe: ActivityNotFoundException) {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/search?q=pub:$NAME_DEV")))
        }
    }

    fun shareFile(context: Context, bitmap: Bitmap, application: String?) {
        val uri = saveImageExternal(context, bitmap)
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            setPackage(application)
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            setDataAndType(uri, "image/*")
        }
        context.startActivity(Intent.createChooser(shareIntent, context.resources.getString(R.string.app_name)))
    }

    private fun saveImageExternal(context: Context, image: Bitmap): Uri? {
        var uri: Uri? = null
        try {
            val file = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "imgRemi.png")
            val stream = FileOutputStream(file)
            image.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.close()
            uri = FileProvider.getUriForFile(context, AUTHORITY, file)
        } catch (e: IOException) {
            Log.d("TAG", "IOException while trying to write file for sharing: " + e.message)
        }
        return uri
    }

    private fun openLink(c: Context, url: String) {
        try {
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url.replace("HTTPS", "https"))
            c.startActivity(i)
        } catch (e: ActivityNotFoundException) {
            Utils.showToast(c, c.getString(R.string.no_browser), Gravity.CENTER)
        }
    }
}