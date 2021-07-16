package com.bedrest.app.utils

import android.content.Context
import java.util.*

object StringUtils {

    fun String.toCapitalized() = replaceFirstChar { char ->
        if (char.isLowerCase()) char.titlecase(Locale.getDefault())
        else char.toString()
    }

    fun String.toKeywordPattern() = lowercase(Locale.getDefault()).replace(" ", "_")

    fun Context.getStringArray(id: Int): Array<out String> {
        return resources.getStringArray(id)
    }
}