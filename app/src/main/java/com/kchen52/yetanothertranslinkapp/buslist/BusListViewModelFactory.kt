package com.kchen52.yetanothertranslinkapp.buslist

import android.content.Context
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.CoroutineScope

class BusListViewModelFactory(val context: Context, val coroutineScope: CoroutineScope): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return BusListViewModel(context, coroutineScope) as T
    }
}
