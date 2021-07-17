package com.bedrest.app.base.activity

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

abstract class BaseActivity<V: ViewDataBinding>: AppCompatActivity(), BaseViewBindingActivity<V> by BaseViewBindingActivityImpl() {

    @LayoutRes
    abstract fun setLayout(): Int

    abstract fun viewOnReady()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.apply {
            systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN
        }
        initBinding(DataBindingUtil.setContentView(this, setLayout()), this)
        binding.lifecycleOwner = this
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        viewOnReady()
    }
}