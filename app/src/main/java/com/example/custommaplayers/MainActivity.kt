package com.example.custommaplayers

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.custommaplayers.data.DataProvider
import com.example.custommaplayers.ui.CustomButton
import com.example.testcomposemaps.ui.theme.CustomMapLayersTheme
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.data.geojson.GeoJsonLayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject


class MainActivity : ComponentActivity() {

    private lateinit var map: GoogleMap
    private lateinit var currentLayerDisplayed: GeoJsonLayer
    private val dataProvider = DataProvider()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            CustomMapLayersTheme {
                val mapView = rememberMapViewLifecycle()

                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxSize()
                        .background(
                            color = MaterialTheme.colorScheme.background,
                        )
                ) {
                    AndroidView(
                        { mapView },
                        modifier = Modifier.fillMaxHeight(0.92f)
                    ) {
                        CoroutineScope(Dispatchers.Main).launch {
                            mapView.getMapAsync {
                                map = it
                                map.uiSettings.isZoomControlsEnabled = true
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
                        CustomButton(modifier = Modifier.weight(1f), text = getString(R.string.get_from_server)) {
                            getFromServer()
                        }
                        
                        Spacer(modifier = Modifier.weight(0.01f))

                        CustomButton(modifier = Modifier.weight(1f), text = getString(R.string.from_file)) {
                            getFromJSONFile()
                        }
                    }
                }
            }
        }
    }

    private fun getFromJSONFile() {
        val geoJSON = dataProvider.getJSONFromRawResource(applicationContext, R.raw.hse_cords)

        putLayerOnMap(geoJSON)
    }

    private fun getFromServer() {
        val geoJSONObject = dataProvider.getFromServer()

        putLayerOnMap(geoJSONObject)
    }

    private fun putLayerOnMap(geoJSONObject: JSONObject) {
        map.clear()

        Log.d("custom", geoJSONObject.toString())

        currentLayerDisplayed = GeoJsonLayer(map, geoJSONObject)
        currentLayerDisplayed.addLayerToMap()

        // zoom map
        if (geoJSONObject.has("properties")) {
            val properties = geoJSONObject.getJSONObject("properties")
            if (properties.has("bbox")) {
                val bboxArray = properties.getJSONArray("bbox")

                val west = bboxArray.getDouble(0)
                val south = bboxArray.getDouble(1)
                val east = bboxArray.getDouble(2)
                val north = bboxArray.getDouble(3)

                val bounds = LatLngBounds(LatLng(south, west), LatLng(north, east))

                map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
            }
        }
    }
}