package com.phicdy.billingsample2

data class MainState(
    val subscriptionList: List<String>,
    val billingResult: String,
    val loaded: Boolean = false,
)