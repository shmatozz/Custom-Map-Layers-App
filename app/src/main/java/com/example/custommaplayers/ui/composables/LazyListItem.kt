package com.example.custommaplayers.ui.composables

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.testcomposemaps.ui.theme.CustomMapLayersTheme

@Composable
fun ListItem(
    text: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth()
            .height(40.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface)
    ) {
        Text(
            text = text,
            modifier = Modifier
                .padding(start = 10.dp, top = 5.dp, bottom = 5.dp, end = 10.dp)
                .fillMaxSize()
                .wrapContentHeight(align = Alignment.CenterVertically)
        )
    }
}

@Preview
@Composable
fun ListItemPreview() {
    CustomMapLayersTheme {
        ListItem(
            text = "Rodionova"
        ) {

        }
    }
}