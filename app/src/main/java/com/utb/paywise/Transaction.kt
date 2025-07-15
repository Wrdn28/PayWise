package com.utb.paywise

data class Transaction(
    val id: String,
    val title: String,
    val amount: Int,
    val isIncome: Boolean
)

