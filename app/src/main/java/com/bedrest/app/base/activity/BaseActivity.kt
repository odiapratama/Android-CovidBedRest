package com.bedrest.app.base.activity

import android.graphics.Color
import android.os.Build
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
        initBinding(DataBindingUtil.setContentView(this, setLayout()), this)
        binding.lifecycleOwner = this
        viewOnReady()
    }
}