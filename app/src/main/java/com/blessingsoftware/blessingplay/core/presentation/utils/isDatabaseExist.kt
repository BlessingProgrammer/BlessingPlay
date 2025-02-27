package com.blessingsoftware.blessingplay.core.presentation.utils

import android.app.Application
import com.blessingsoftware.blessingplay.R

fun isDatabaseExist(application: Application): Boolean {
    val dbFile =
        application.getDatabasePath(application.getString(R.string.db_name))
    return dbFile.exists()
}