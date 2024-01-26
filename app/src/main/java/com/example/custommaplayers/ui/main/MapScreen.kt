package com.example.custommaplayers.ui.main

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat.getString
import com.example.custommaplayers.R
import com.example.custommaplayers.ui.composables.CustomButton
import com.google.android.gms.maps.GoogleMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun MapScreen(
    context: Context,
    getFromServer: () -> Unit,
    getFromJSONFile: () -> Unit,
    onMapReady: (GoogleMap) -> Unit
) {
    val mapView = rememberMapViewLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background,)
    ) {
        AndroidView(
            { mapView },
            modifier = Modifier.fillMaxHeight(0.92f)
        ) {
            CoroutineScope(Dispatchers.Main).launch {
                mapView.getMapAsync {
                    onMapReady(it)
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            CustomButton(
                modifier = Modifier.weight(1f),
                text = getString(context, R.string.get_from_server)
            ) {
                getFromServer()
            }

            Spacer(modifier = Modifier.weight(0.01f))

            CustomButton(
                modifier = Modifier.weight(1f),
                text = getString(context, R.string.from_file)
            ) {
                getFromJSONFile()
            }
        }
    }
}