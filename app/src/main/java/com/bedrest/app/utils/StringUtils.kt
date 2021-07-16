package com.bedrest.app.utils

import java.util.*

object StringUtils {

    fun String.toCapitalized() = replaceFirstChar { char ->
        if (char.isLowerCase()) char.titlecase(Locale.getDefault())
        else char.toString()
    }

    fun String.toKeywordPattern() = lowercase(Locale.getDefault()).replace(" ", "_")
}