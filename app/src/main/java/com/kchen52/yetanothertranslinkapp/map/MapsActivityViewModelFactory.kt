package com.kchen52.yetanothertranslinkapp.map

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class MapsActivityViewModelFactory(val context: Context, val coroutineScope: CoroutineScope): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MapsActivityViewModel(context, coroutineScope) as T
    }
}