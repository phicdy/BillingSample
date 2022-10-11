package com.phicdy.billingsample2

import android.os.Parcelable
import androidx.compose.runtime.Stable
import kotlinx.parcelize.Parcelize

@Stable
@Parcelize
data class MainState(
    val subscriptionList: List<ProductDetailsResult>,
    val billingResult: String,
    val loaded: Boolean = false,
) : Parcelable