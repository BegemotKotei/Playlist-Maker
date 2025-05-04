package com.example.playlistmaker.presentation

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Parcelable
import android.util.TypedValue

inline fun <reified T : Parcelable> Intent.parcelable(key: String): T? = when {
    SDK_INT >= Build.VERSION_CODES.TIRAMISU -> getParcelableExtra(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelableExtra(key) as? T
}

private const val RADIUS_CUT_IMAGE = 10f

fun getRadiusCutImage(): Float {
    return RADIUS_CUT_IMAGE
}

fun dpToPx(dp: Float, context: Context): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp,
        context.resources.displayMetrics
    ).toInt()
}