package com.example.custommaplayers.ui.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.testcomposemaps.ui.theme.CustomMapLayersTheme

@Composable
fun ServerFileSelectDialog(
    title: String,
    availableFilesList: List<String>,
    onDismissRequest: () -> Unit,
    onFileSelected: () -> Unit
) {
    Dialog(onDismissRequest = { onDismissRequest() }) {
        // Draw a rectangle shape with rounded corners inside the dialog
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(500.dp)
                .padding(16.dp),
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors(Color.White),
            border = BorderStroke(2.dp, Color.Black)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = title,
                    modifier = Modifier
                        .weight(0.1f)
                        .fillMaxSize(),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge
                )

                LazyColumn(
                    modifier = Modifier
                        .weight(0.9f)
                        .fillMaxSize()
                ) {
                    items(
                        items = availableFilesList,
                    ) {
                        ListItem(text = it) {
                            onFileSelected()
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun ServerFileSelectDialogPreview() {
    CustomMapLayersTheme {
        ServerFileSelectDialog(
            title = "Now available on server",
            availableFilesList = listOf("Rodionova", "Lvovskay", "BP"),
            onDismissRequest = { },
            onFileSelected = { }
        )
    }
}
