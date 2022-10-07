package com.phicdy.billingsample2

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {

    val state: State<MainState> = mutableStateOf(MainState(listOf()))
}