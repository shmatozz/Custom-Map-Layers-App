package com.example.custommaplayers.ui.main

import android.content.Context
import android.util.Log
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat.getString
import com.example.custommaplayers.R
import com.example.custommaplayers.data.DataProvider
import com.example.custommaplayers.ui.composables.CustomButton
import com.example.custommaplayers.ui.composables.ServerFileSelectDialog
import com.google.android.gms.maps.GoogleMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@Composable
fun MapScreen(
    context: Context,
    getFromServer: (String) -> Unit,
    getFromJSONFile: () -> Unit,
    dataProvider: DataProvider, // Changed name to avoid confusion
    onMapReady: (GoogleMap) -> Unit
) {
    val mapView = rememberMapViewLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
            .padding(bottom = 15.dp)
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
            var objectsList by remember { mutableStateOf(emptyList<String>()) }
            var needDialog by remember { mutableStateOf(false) }

            CustomButton(
                modifier = Modifier.weight(1f),
                text = getString(context, R.string.get_from_server)
            ) {
                getObjectsListFromServer(dataProvider) { newObjectsList ->
                    objectsList = newObjectsList
                    for (obg in objectsList) {
                        Log.d("working", "aa $obg")
                    }
                }
                needDialog = true
            }

            Spacer(modifier = Modifier.weight(0.01f))

            CustomButton(
                modifier = Modifier.weight(1f),
                text = getString(context, R.string.from_file)
            ) {
                getFromJSONFile()
            }

            if (needDialog) {
                ServerFileSelectDialog(
                    title = getString(context, R.string.cords_available),
                    availableFilesList = objectsList,
                    onDismissRequest = { needDialog = false }
                ) { selectedObject ->
                    getFromServer(selectedObject) // Update data with selected object
                    needDialog = false
                }
            }
        }
    }
}

@OptIn(DelicateCoroutinesApi::class)
fun getObjectsListFromServer(dataProvider: DataProvider, callback: (List<String>) -> Unit) {
    GlobalScope.launch(Dispatchers.Main) {
        val objectsList = dataProvider.getObjectListFromServer()
        callback(objectsList)
    }
}
