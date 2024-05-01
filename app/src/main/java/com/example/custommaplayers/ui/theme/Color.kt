package com.example.custommaplayers.ui.theme

import androidx.compose.ui.graphics.Color
import android.graphics.Color as AndroidColor

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

// on below line we are adding different colors.
val greenColor = Color(0xFF0F9D58)

fun rgbToHue(rgbColorString: String): Float {
    if (rgbColorString.isEmpty()) {
        return 0.0f
    }

    val color = android.graphics.Color.parseColor(rgbColorString)

    val hsv = FloatArray(3)
    android.graphics.Color.RGBToHSV(AndroidColor.red(color), AndroidColor.green(color), AndroidColor.blue(color), hsv)

    return hsv[0]
}