package com.phicdy.billingsample2

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {

    var state by mutableStateOf(MainState(listOf()))
        private set

    fun updateSubscriptionList(list: List<String>) {
        state = MainState(list)
    }
}