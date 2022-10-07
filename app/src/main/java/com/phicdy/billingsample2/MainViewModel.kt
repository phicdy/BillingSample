package com.phicdy.billingsample2

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ProductDetails

class MainViewModel : ViewModel() {

    var state by mutableStateOf(
        MainState(
            subscriptionList = listOf(),
            billingResult = "",
            loaded = false
        )
    )
        private set

    fun updateState(productDetailsList: List<ProductDetails>, billingResult: BillingResult) {
        state = MainState(
            subscriptionList = productDetailsList.map { it.toString() },
            billingResult = billingResult.toString(),
            loaded = true
        )
    }
}