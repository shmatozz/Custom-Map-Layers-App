package com.example.custommaplayers.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val RoundedShapes = Shapes(
    small = RoundedCornerShape(size = 4.dp),
    medium = RoundedCornerShape(size = 8.dp),
    large = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
)