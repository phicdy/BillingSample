package com.phicdy.billingsample2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase.PurchaseState
import com.android.billingclient.api.PurchasesResult
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.android.billingclient.api.queryProductDetails
import com.android.billingclient.api.queryPurchasesAsync
import com.phicdy.billingsample2.ui.theme.BillingSampleTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    private val purchasesUpdatedListener =
        PurchasesUpdatedListener { billingResult, purchases ->
        }

    private val billingClient by lazy {
        BillingClient.newBuilder(applicationContext)
            .setListener(purchasesUpdatedListener)
            .enablePendingPurchases()
            .build()
    }

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            BillingSampleTheme {
                MainScreen(
                    onComposed = {
                        billingClient.startConnection(object : BillingClientStateListener {
                            override fun onBillingSetupFinished(billingResult: BillingResult) {
                                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                                    lifecycleScope.launchWhenStarted {
                                        val queryProductDetailsParams =
                                            QueryProductDetailsParams.newBuilder()
                                                .setProductList(
                                                    listOf(
                                                        QueryProductDetailsParams.Product.newBuilder()
                                                            .setProductId(BuildConfig.SUBSCRIPTION_PRUDUCT_ID)
                                                            .setProductType(BillingClient.ProductType.SUBS)
                                                            .build()
                                                    )
                                                )
                                                .build()

                                        // leverage queryProductDetails Kotlin extension function
                                        val productDetailsResult = withContext(Dispatchers.IO) {
                                            billingClient.queryProductDetails(
                                                queryProductDetailsParams
                                            )
                                        }

                                        val purchase = withContext(Dispatchers.IO) {
                                            val params = QueryPurchasesParams.newBuilder()
                                                .setProductType(BillingClient.ProductType.SUBS)
                                                .build()
                                            billingClient.queryPurchasesAsync(params)
                                        }

                                        productDetailsResult.productDetailsList?.let { productDetailsList ->
                                            viewModel.updateState(
                                                productDetailsList,
                                                productDetailsResult.billingResult,
                                                purchase
                                            )
                                        }
                                    }
                                }
                            }

                            override fun onBillingServiceDisconnected() {
                                // Try to restart the connection on the next request to
                                // Google Play by calling the startConnection() method.
                            }
                        })
                    },
                    mainViewModel = viewModel
                ) { productDetails, selectedOfferToken ->
                    val productDetailsParamsList = listOf(
                        BillingFlowParams.ProductDetailsParams.newBuilder()
                            .setProductDetails(productDetails)
                            .setOfferToken(selectedOfferToken)
                            .build()
                    )

                    val billingFlowParams = BillingFlowParams.newBuilder()
                        .setProductDetailsParamsList(productDetailsParamsList)
                        .build()

                    billingClient.launchBillingFlow(this, billingFlowParams)

                }
            }
        }
    }
}

@Composable
fun MainScreen(
    mainViewModel: MainViewModel,
    onComposed: () -> Unit = {},
    onPurchaseButtonClicked: (ProductDetails, String) -> Unit = { _, _ -> }
) {
    LaunchedEffect(Unit) {
        onComposed()
    }
    MainScreen(
        state = mainViewModel.state,
        onComposed = onComposed,
        onPurchaseButtonClicked = onPurchaseButtonClicked
    )
}

@Composable
fun MainScreen(
    state: MainState,
    onComposed: () -> Unit = {},
    onPurchaseButtonClicked: (ProductDetails, String) -> Unit = { _, _ -> }
) {
    LaunchedEffect(Unit) {
        onComposed()
    }
    // A surface container using the 'background' color from the theme
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colors.background
    ) {
        Column {
            if (state.subscriptionList.isEmpty()) {
                EmptySubscriptionText()
                BillingResult(billingResult = state.billingResult)
                LoadState(loaded = state.loaded)
            } else {
                SubscriptionResult(
                    subscriptionList = state.subscriptionList,
                    raw = state.raw,
                    billingResult = state.billingResult,
                    loaded = state.loaded,
                    onPurchaseButtonClicked = onPurchaseButtonClicked
                )
            }
            if (state.purchasesResult == null || state.purchasesResult.purchasesList.isEmpty()) {
                EmptyPurchaseText()
            } else {
                PurchaseBillingResult(purchasesResult = state.purchasesResult)
            }
        }
    }
}

@Composable
fun SubscriptionResult(
    subscriptionList: List<ProductDetailsResult>,
    raw: List<ProductDetails>,
    billingResult: String,
    loaded: Boolean,
    onPurchaseButtonClicked: (ProductDetails, String) -> Unit = { _, _ -> }
) {
    LazyColumn {
        item {
            Text(text = "Subscription List", modifier = Modifier.padding(top = 8.dp))
        }
        items(
            items = subscriptionList,
            key = { subscription -> subscription.productId }) { subscription ->
            Text(text = "Name: ${subscription.name}")
            Text(text = "Product Type: ${subscription.productType}")
            Text(text = "Product ID: ${subscription.productId}")
            Text(text = "Title: ${subscription.title}")
            Text(text = "oneTimePurchaseOfferDetails")
            Text(text = "FormattedPrice: ${subscription.oneTimePurchaseOfferDetailsFormattedPrice}")
            Text(text = "PriceCurrencyCode: ${subscription.oneTimePurchaseOfferDetailsPriceCurrencyCode}")
            Text(text = "PriceAmountMicros: ${subscription.oneTimePurchaseOfferDetailsPriceAmountMicros}")
            Text(text = "subscriptionOfferDetails First Item")
            Text(text = "OfferTag: ${subscription.subscriptionOfferDetailsFirstOfferTag}")
//            Text(text = "OfferToken: ${subscription.subscriptionOfferDetailsFirstOfferToken}")
            Text(text = "PricingPhases First Item")
            Text(text = "BillingCycleCount: ${subscription.subscriptionOfferDetailsFirstPricingPhasesFirstPricingPhaseBillingCycleCount}")
            Text(text = "BillingPeriod: ${subscription.subscriptionOfferDetailsFirstPricingPhasesFirstPricingPhaseBillingPeriod}")
            Text(text = "FormattedPrice: ${subscription.subscriptionOfferDetailsFirstPricingPhasesFirstPricingPhaseFormattedPrice}")
            Text(text = "AmountMicros: ${subscription.subscriptionOfferDetailsFirstPricingPhasesFirstPricingPhasePriceAmountMicros}")
            Text(text = "CurrencyCode: ${subscription.subscriptionOfferDetailsFirstPricingPhasesFirstPricingPhasePriceCurrencyCode}")
            Text(text = "RecurrenceMode: ${subscription.subscriptionOfferDetailsFirstPricingPhasesFirstPricingPhaseRecurrenceMode}")
            Spacer(modifier = Modifier.height(8.dp))
            subscription.subscriptionOfferDetailsFirstOfferToken?.let { token ->
                PurchaseButton(
                    productDetails = raw.first { it.productId == subscription.productId },
                    offerToken = token,
                    onClicked = onPurchaseButtonClicked
                )
            }
        }
        item {
            BillingResult(billingResult = billingResult)
        }
        item {
            LoadState(loaded = loaded)
        }
    }
}

@Composable
fun EmptySubscriptionText() {
    Text(text = "Subscription is empty")
}

@Composable
fun LoadState(loaded: Boolean) {
    Text(text = if (loaded) "loaded" else "not loaded", modifier = Modifier.padding(top = 8.dp))
}

@Composable
fun BillingResult(billingResult: String) {
    Text(text = "Billing Result", modifier = Modifier.padding(top = 8.dp))
    Text(text = billingResult)
}

@Composable
fun PurchaseButton(
    productDetails: ProductDetails,
    offerToken: String,
    onClicked: (ProductDetails, String) -> Unit = { _, _ -> }
) {
    Button(
        onClick = { onClicked(productDetails, offerToken) },
        modifier = Modifier
            .padding(8.dp)
            .width(128.dp)
            .height(32.dp)
    ) {
        Text(text = "Purchase")
    }
}

@Composable
fun EmptyPurchaseText() {
    Text(text = "Purchase is empty")
}

@Composable
fun PurchaseBillingResult(
    purchasesResult: PurchasesResult,
) {
    LazyColumn {
        item {
            Text(text = "Purchase Result", modifier = Modifier.padding(top = 8.dp))
        }
        items(
            items = purchasesResult.purchasesList,
            key = { purchase -> purchase.orderId }) { purchase ->
            Text(text = "OrderId: ${purchase.orderId}")
            Text(text = "Account Identifiers: ${purchase.accountIdentifiers}")
            Text(text = "Developer Payload: ${purchase.developerPayload}")
            Text(text = "isAcknowledged: ${purchase.isAcknowledged}")
            Text(text = "isAutoRenewing: ${purchase.isAutoRenewing}")
            Text(text = "Original Json: ${purchase.originalJson}")
            Text(text = "Package Name: ${purchase.packageName}")
            Text(text = "Products: ${purchase.products}")
            val purchaseStateString = when (purchase.purchaseState) {
                PurchaseState.PURCHASED -> "PURCHASED "
                PurchaseState.UNSPECIFIED_STATE -> "UNSPECIFIED_STATE "
                PurchaseState.PENDING -> "PENDING "
                else -> "UNKNOWN"
            }
            Text(text = "Purchase State: $purchaseStateString")
            Text(text = "Purchase Time: ${purchase.purchaseTime}")
            Text(text = "Purchase Token: ${purchase.purchaseToken}")
            Text(text = "Quantity: ${purchase.quantity}")
            Text(text = "Signature: ${purchase.signature}")
        }
        item {
            PurchaseBillingResult(billingResult = purchasesResult.billingResult)
        }
    }
}

@Composable
fun PurchaseBillingResult(billingResult: BillingResult) {
    Text(text = "Billing Result", modifier = Modifier.padding(top = 8.dp))
    Text(text = billingResult.toString())
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    BillingSampleTheme {
        MainScreen(
            state = MainState(
                subscriptionList = listOf(
                    ProductDetailsResult(
                        name = "test",
                        productId = "productId",
                        productType = "proudctType",
                        title = "title",
                        oneTimePurchaseOfferDetailsFormattedPrice = "",
                        oneTimePurchaseOfferDetailsPriceAmountMicros = 0L,
                        oneTimePurchaseOfferDetailsPriceCurrencyCode = "",
                        subscriptionOfferDetailsFirstOfferTag = listOf("tag"),
                        subscriptionOfferDetailsFirstOfferToken = "",
                        subscriptionOfferDetailsFirstPricingPhasesFirstPricingPhaseBillingPeriod = "",
                        subscriptionOfferDetailsFirstPricingPhasesFirstPricingPhaseFormattedPrice = "",
                        subscriptionOfferDetailsFirstPricingPhasesFirstPricingPhasePriceAmountMicros = 0L,
                        subscriptionOfferDetailsFirstPricingPhasesFirstPricingPhasePriceCurrencyCode = "",
                        subscriptionOfferDetailsFirstPricingPhasesFirstPricingPhaseRecurrenceMode = 0,
                        subscriptionOfferDetailsFirstPricingPhasesFirstPricingPhaseBillingCycleCount = 0,
                    )
                ),
                billingResult = "billing result",
                raw = listOf(),
                purchasesResult = null
            )
        )
    }
}