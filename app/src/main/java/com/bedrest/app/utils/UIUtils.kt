package com.bedrest.app.utils

import android.view.View
import android.view.View.*
import android.view.ViewTreeObserver

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

    inline fun <T : View> T.afterMeasured(crossinline action: T.() -> Unit) {
        viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                if (measuredWidth > 0 && measuredHeight > 0) {
                    viewTreeObserver.removeOnGlobalLayoutListener(this)
                    action()
                }
            }
        })
    }

}