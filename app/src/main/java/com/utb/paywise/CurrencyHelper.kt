package com.utb.paywise.util

import java.text.NumberFormat
import java.util.*

fun formatRupiah(amount: Int): String {
    val localeID = Locale("in", "ID")
    val formatter = NumberFormat.getCurrencyInstance(localeID)
    return formatter.format(amount).replace("Rp", "Rp ").replace(",00", "")
}
