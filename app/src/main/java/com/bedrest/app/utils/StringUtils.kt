package com.bedrest.app.utils

import android.content.Context
import java.util.*

object StringUtils {

    fun String.toCapitalized() = replaceFirstChar { char ->
        if (char.isLowerCase()) char.titlecase(Locale.getDefault())
        else char.toString()
    }

    fun String.toKeywordPattern() = lowercase(Locale.getDefault()).replace(" ", "_")

    fun Context.getStringArray(id: Int): Array<String> {
        return resources.getStringArray(id)
    }

    fun String.checkLatPattern(): Boolean {
        val latPattern = Regex("^[-+]?([1-8]?\\d(\\.\\d+)?|90(\\.0+)?)")
        return matches(latPattern)
    }

    fun String.checkLonPattern(): Boolean {
        val lonPattern = Regex("\\s*[-+]?(180(\\.0+)?|((1[0-7]\\d)|([1-9]?\\d))(\\.\\d+)?)\$")
        return matches(lonPattern)
    }
}