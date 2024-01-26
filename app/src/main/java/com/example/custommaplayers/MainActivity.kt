package com.example.custommaplayers

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.custommaplayers.data.DataProvider
import com.example.custommaplayers.ui.main.MapScreen
import com.example.testcomposemaps.ui.theme.CustomMapLayersTheme
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.data.geojson.GeoJsonLayer
import org.json.JSONObject


class MainActivity : ComponentActivity() {

    private lateinit var map: GoogleMap
    private lateinit var currentLayerDisplayed: GeoJsonLayer
    private val dataProvider = DataProvider()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            CustomMapLayersTheme {
                MapScreen(
                    context = applicationContext,
                    getFromServer = { getFromServer() },
                    getFromJSONFile = { getFromJSONFile() }
                ) {
                    map = it
                    map.uiSettings.isZoomControlsEnabled = true
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