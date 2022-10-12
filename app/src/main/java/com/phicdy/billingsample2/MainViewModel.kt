package com.phicdy.billingsample2

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.PurchasesResult

class MainViewModel : ViewModel() {

    var state by mutableStateOf(
        MainState(
            subscriptionList = listOf(),
            billingResult = "",
            loaded = false,
            raw = listOf(),
            purchasesResult = null
        )
    )
        private set

    fun updateState(
        productDetailsList: List<ProductDetails>,
        billingResult: BillingResult,
        purchase: PurchasesResult
    ) {
        state = MainState(
            subscriptionList = productDetailsList.map { productDetails ->
                ProductDetailsResult(
                    name = productDetails.name,
                    productId = productDetails.productId,
                    productType = productDetails.productType,
                    title = productDetails.title,
                    oneTimePurchaseOfferDetailsFormattedPrice = productDetails.oneTimePurchaseOfferDetails?.formattedPrice,
                    oneTimePurchaseOfferDetailsPriceCurrencyCode = productDetails.oneTimePurchaseOfferDetails?.priceCurrencyCode,
                    oneTimePurchaseOfferDetailsPriceAmountMicros = productDetails.oneTimePurchaseOfferDetails?.priceAmountMicros,
                    subscriptionOfferDetailsFirstOfferTag = productDetails.subscriptionOfferDetails?.get(
                        0
                    )?.offerTags,
                    subscriptionOfferDetailsFirstOfferToken = productDetails.subscriptionOfferDetails?.get(
                        0
                    )?.offerToken,
                    subscriptionOfferDetailsFirstPricingPhasesFirstPricingPhaseFormattedPrice = productDetails.subscriptionOfferDetails?.get(
                        0
                    )?.pricingPhases?.pricingPhaseList?.get(0)?.formattedPrice,
                    subscriptionOfferDetailsFirstPricingPhasesFirstPricingPhasePriceCurrencyCode = productDetails.subscriptionOfferDetails?.get(
                        0
                    )?.pricingPhases?.pricingPhaseList?.get(0)?.priceCurrencyCode,
                    subscriptionOfferDetailsFirstPricingPhasesFirstPricingPhasePriceAmountMicros = productDetails.subscriptionOfferDetails?.get(
                        0
                    )?.pricingPhases?.pricingPhaseList?.get(0)?.priceAmountMicros,
                    subscriptionOfferDetailsFirstPricingPhasesFirstPricingPhaseBillingCycleCount = productDetails.subscriptionOfferDetails?.get(
                        0
                    )?.pricingPhases?.pricingPhaseList?.get(0)?.billingCycleCount,
                    subscriptionOfferDetailsFirstPricingPhasesFirstPricingPhaseBillingPeriod = productDetails.subscriptionOfferDetails?.get(
                        0
                    )?.pricingPhases?.pricingPhaseList?.get(0)?.billingPeriod,
                    subscriptionOfferDetailsFirstPricingPhasesFirstPricingPhaseRecurrenceMode = productDetails.subscriptionOfferDetails?.get(
                        0
                    )?.pricingPhases?.pricingPhaseList?.get(0)?.recurrenceMode,
                )
            },
            billingResult = billingResult.toString(),
            loaded = true,
            raw = productDetailsList,
            purchasesResult = purchase
        )
    }
}