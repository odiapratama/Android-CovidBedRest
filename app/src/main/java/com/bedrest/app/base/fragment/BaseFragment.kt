package com.bedrest.app.base.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment

abstract class BaseFragment<V: ViewDataBinding>: Fragment(), BaseViewBindingFragment<V> by BaseViewBindingFragmentImpl<V>() {

    @LayoutRes
    abstract fun setLayout(): Int

    abstract fun viewOnReady()

    open fun observeData() = Unit

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return initBinding(DataBindingUtil.inflate(layoutInflater, setLayout(), container, false), this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = this
        viewOnReady()
        observeData()
    }
}