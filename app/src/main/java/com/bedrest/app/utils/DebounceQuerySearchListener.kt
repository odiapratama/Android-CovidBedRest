package com.bedrest.app.utils

import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.coroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DebounceQuerySearchListener(
    lifecycle: Lifecycle,
    private val period: Long = 1000,
    private val onDebounceListener: (String) -> Unit
) : SearchView.OnQueryTextListener {

    private val coroutineScope = lifecycle.coroutineScope
    private var job: Job? = null

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        job?.cancel()
        job = coroutineScope.launch {
            newText?.let {
                delay(period)
                onDebounceListener(it)
            }
        }
        return false
    }
}