package com.phicdy.billingsample2

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MainState(
    val subscriptionList: List<String>,
    val billingResult: String,
    val loaded: Boolean = false,
) : Parcelable