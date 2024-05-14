package com.example.custommaplayers.ui.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.custommaplayers.viewmodel.MapViewModel

@Composable
fun ServerFileSelectDialog(
    title: String,
    viewModel: MapViewModel,
    onDismissRequest: () -> Unit,
    onFileSelected: (String) -> Unit
) {
    Dialog(onDismissRequest = { onDismissRequest() }) {
        // Draw a rectangle shape with rounded corners inside the dialog
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(500.dp),
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
                        .wrapContentHeight()
                        .padding(8.dp),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge
                )

                val objectsState = viewModel.objectsState
                if (objectsState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.padding(top = 10.dp),
                        color = Color.Black
                    )
                }

                LazyColumn(
                    modifier = Modifier
                        .weight(0.9f)
                        .fillMaxSize()
                ) {
                    items(items = objectsState.objectsList?.toList() ?: listOf()) {
                        ListItem(text = it) {
                            onFileSelected(it)
                        }
                    }
                }
            }
        }
    }
}
