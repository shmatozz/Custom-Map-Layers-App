package com.example.custommaplayers

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.WindowCompat
import com.example.custommaplayers.data.DataProvider
import com.example.custommaplayers.ui.main.MapScreen
import com.example.custommaplayers.ui.theme.rgbToHue
import com.example.testcomposemaps.ui.theme.CustomMapLayersTheme
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolygonOptions
import com.google.android.gms.maps.model.PolylineOptions
import org.json.JSONArray
import org.json.JSONObject


class MainActivity : ComponentActivity() {

    private lateinit var map: GoogleMap
    private val dataProvider = DataProvider()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            CustomMapLayersTheme {
                MapScreen(
                    context = applicationContext,
                    getFromServer = { getFromServer(it) },
                    getFromJSONFile = { chooseFile() },
                    dataProvider = dataProvider,
                ) {
                    map = it
                    map.uiSettings.isZoomControlsEnabled = true
                }
            }
        }
    }

    private fun chooseFile() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "application/json"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        pickFileLauncher.launch(intent)
    }

    private val pickFileLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { uri ->
                    val fileName = getFileName(uri)
                    if (fileName != null) {
                        try {
                            val geoJSON = dataProvider.getJSONFromUri(applicationContext, uri)
                            putLayerOnMap(geoJSON)
                        } catch (e: Exception) {
                            showLoadErrorDialog()
                            Log.d("working", e.toString())
                        }
                    }
                }
            }
        }

    private fun getFileName(uri: Uri): String? {
        return contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            cursor.moveToFirst()
            cursor.getString(nameIndex)
        }
    }

    private fun getFromServer(objectID: String) {
        dataProvider.getFromServer(objectID) {
            putLayerOnMap(it)
        }
    }

    private fun putLayerOnMap(geoJSONObject: JSONObject) {
        map.clear()

        Log.d("working", "putLayerOnMap $geoJSONObject")

        try {
            if (geoJSONObject.has("features")) {
                val features = geoJSONObject.getJSONArray("features")
                for (i in 0 until features.length()) {
                    val feature = features.getJSONObject(i)
                    if (feature.has("geometry")) {
                        val geometry = feature.getJSONObject("geometry")
                        when (geometry.getString("type")) {
                            "Point" -> {
                                val coordinates = geometry.getJSONArray("coordinates")
                                val properties = feature.getJSONObject("properties")
                                val color = properties.getString("color")
                                val hint = properties.getString("hint")
                                val header = properties.getString("header")
                                val body = properties.getString("body")

                                val markerOptions = MarkerOptions()
                                    .position(LatLng(coordinates.getDouble(0), coordinates.getDouble(1)))
                                    .title(header)
                                    .snippet(body)
                                    .icon(BitmapDescriptorFactory.defaultMarker(rgbToHue(color)))

                                map.addMarker(markerOptions)
                            }
                            "LineString" -> {
                                val coordinates = geometry.getJSONArray("coordinates")
                                val points = ArrayList<LatLng>()
                                for (j in 0 until coordinates.length()) {
                                    val coords = coordinates.getJSONArray(j)
                                    val latLng = LatLng(coords.getDouble(0), coords.getDouble(1))
                                    points.add(latLng)
                                }
                                map.addPolyline(PolylineOptions().addAll(points).color(Color.Red.toArgb()))
                            }
                            "Polygon" -> {
                                val coordinates = geometry.getJSONArray("coordinates").get(0) as JSONArray
                                val points = ArrayList<LatLng>()
                                for (j in 0 until coordinates.length()) {
                                    val coords = coordinates.getJSONArray(j)
                                    val latLng = LatLng(coords.getDouble(0), coords.getDouble(1))
                                    points.add(latLng)
                                }
                                map.addPolygon(PolygonOptions().addAll(points).fillColor(Color(0x64FF0000).toArgb()).strokeColor(Color.Red.toArgb()))
                            }
                        }
                    }
                }
                /* Zoom map by Bounding Box */
                if (geoJSONObject.has("bbox")) {
                    val bboxArray = geoJSONObject.getJSONArray("bbox")

                    val south = bboxArray.getDouble(0)
                    val west = bboxArray.getDouble(1)
                    val north = bboxArray.getDouble(2)
                    val east = bboxArray.getDouble(3)

                    val bounds = LatLngBounds(LatLng(south, west), LatLng(north, east))

                    map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
                }
            }
        } catch (error: Exception) {
            Log.d("working", error.toString())
            showLoadErrorDialog()
        }
    }

    private fun showLoadErrorDialog() {
        AlertDialog.Builder(this)
            .setTitle(R.string.file_error)
            .setMessage(R.string.check_and_retry)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}