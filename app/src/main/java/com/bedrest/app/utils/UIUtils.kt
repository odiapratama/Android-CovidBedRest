package com.bedrest.app.utils

import android.view.View
import android.view.View.*

object UIUtils {
    fun View.gone() {
        this.visibility = GONE
    }

    fun View.visible() {
        this.visibility = VISIBLE
    }

    fun View.invisible() {
        this.visibility = INVISIBLE
    }
}