package com.phicdy.billingsample2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.queryProductDetails
import com.phicdy.billingsample2.ui.theme.BillingSampleTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    private val purchasesUpdatedListener =
        PurchasesUpdatedListener { billingResult, purchases ->
            // To be implemented in a later section.
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

                                        productDetailsResult.productDetailsList?.let { productDetailsList ->
                                            viewModel.updateState(
                                                productDetailsList,
                                                productDetailsResult.billingResult
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
                )
            }
        }
    }
}

@Composable
fun MainScreen(
    mainViewModel: MainViewModel,
    onComposed: () -> Unit = {},
) {
    LaunchedEffect(Unit) {
        onComposed()
    }
    MainScreen(
        mainViewModel.state,
        onComposed
    )
}

@Composable
fun MainScreen(
    state: MainState,
    onComposed: () -> Unit = {},
) {
    LaunchedEffect(Unit) {
        onComposed()
    }
    // A surface container using the 'background' color from the theme
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colors.background
    ) {
        if (state.subscriptionList.isEmpty()) {
            Column {
                EmptyText()
                BillingResult(billingResult = state.billingResult)
                LoadState(loaded = state.loaded)
            }
        } else {
            SubscriptionResult(state.subscriptionList, state.billingResult, state.loaded)
        }
    }
}

@Composable
fun SubscriptionResult(subscriptionList: List<String>, billingResult: String, loaded: Boolean) {
    LazyColumn {
        item {
            Text(text = "Subscription List", modifier = Modifier.padding(top = 8.dp))
        }
        items(
            items = subscriptionList,
            key = { subscription -> subscription }) { subscription ->
            Text(text = subscription, modifier = Modifier.padding(top = 8.dp))
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
fun EmptyText() {
    Text(text = "empty")
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

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    BillingSampleTheme {
        MainScreen(
            state = MainState(
                subscriptionList = listOf("subscription"),
                billingResult = "billing result"
            )
        )
    }
}