package com.phicdy.billingsample2

import androidx.compose.runtime.Stable
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.PurchasesResult

@Stable
data class MainState(
    val subscriptionList: List<ProductDetailsResult>,
    val billingResult: String,
    val loaded: Boolean = false,
    val raw: List<ProductDetails>,
    val purchasesResult: PurchasesResult?
)