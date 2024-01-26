package com.example.custommaplayers.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun CustomButton(
    modifier: Modifier,
    text: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .border(2.dp, Color.Black, MaterialTheme.shapes.medium)
            .height(60.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White,
            contentColor = Color.Black
        ),
        shape = MaterialTheme.shapes.medium,
        contentPadding = PaddingValues(4.dp)

    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall
        )
    }
}

@Preview
@Composable
fun CustomButtonPreview() {
    CustomButton(modifier = Modifier, text = "Test123") {
        
    }
}
