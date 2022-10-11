package com.phicdy.billingsample2

import android.os.Parcelable
import androidx.compose.runtime.Stable
import kotlinx.parcelize.Parcelize

@Stable
@Parcelize
data class ProductDetailsResult(
    val name: String,
    val productId: String,
    val productType: String,
    val title: String,
    val oneTimePurchaseOfferDetailsFormattedPrice: String?,
    val oneTimePurchaseOfferDetailsPriceCurrencyCode: String?,
    val oneTimePurchaseOfferDetailsPriceAmountMicros: Long?,
    val subscriptionOfferDetailsFirstOfferTag: List<String>?,
    val subscriptionOfferDetailsFirstOfferToken: String?,
    val subscriptionOfferDetailsFirstPricingPhasesFirstPricingPhaseFormattedPrice: String?,
    val subscriptionOfferDetailsFirstPricingPhasesFirstPricingPhasePriceCurrencyCode: String?,
    val subscriptionOfferDetailsFirstPricingPhasesFirstPricingPhasePriceAmountMicros: Long?,
    val subscriptionOfferDetailsFirstPricingPhasesFirstPricingPhaseBillingCycleCount: Int?,
    val subscriptionOfferDetailsFirstPricingPhasesFirstPricingPhaseBillingPeriod: String?,
    val subscriptionOfferDetailsFirstPricingPhasesFirstPricingPhaseRecurrenceMode: Int?,
): Parcelable