package com.blessingsoftware.blessingplay.core.presentation.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun formatDateModified(timestamp: Long): String {
    val sdf = SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault())
    return sdf.format(Date(timestamp * 1000))
}
