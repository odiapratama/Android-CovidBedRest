package com.bedrest.app.utils

import android.content.Context
import android.content.Intent
import android.net.Uri

object IntentUtils {

    fun Context.openMaps(lat: String, long: String, key: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse("geo:${lat}, ${long}?q=$key")
        startActivity(intent)
    }

    fun Context.openWeb(url: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        startActivity(intent)
    }

    fun Context.openDialer(number: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse("tel:$number")
        startActivity(intent)
    }
}