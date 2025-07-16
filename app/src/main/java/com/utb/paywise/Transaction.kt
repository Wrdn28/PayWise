package com.utb.paywise

import java.util.Date

data class Transaction(
    val id: String,
    val title: String,
    val amount: Int,
    val isIncome: Boolean,
    val date: Date? = null
)

