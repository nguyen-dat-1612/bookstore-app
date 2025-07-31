package com.dat.bookstore_app.presentation.features.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainSharedViewModel @Inject constructor() : ViewModel() {
    private val _tabToSwitch = MutableLiveData<String>()
    val tabToSwitch: LiveData<String> = _tabToSwitch

    fun switchTab(tab: String) {
        _tabToSwitch.value = tab
    }
}