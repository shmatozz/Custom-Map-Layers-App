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
import android.graphics.Color as AndroidColor
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.WindowCompat
import com.example.custommaplayers.data.DataProvider
import com.example.custommaplayers.ui.main.MapScreen
import com.example.testcomposemaps.ui.theme.CustomMapLayersTheme
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.data.geojson.GeoJsonLayer
import org.json.JSONException
import org.json.JSONObject


class MainActivity : ComponentActivity() {

    private lateinit var map: GoogleMap
    private lateinit var currentLayerDisplayed: GeoJsonLayer
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
                        } catch (e: JSONException) {
                            showLoadErrorDialog()
                            Log.d("working", "invalid file loaded")
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
            try {
                putLayerOnMap(it)
            } catch (e: JSONException) {
                showLoadErrorDialog()
                Log.d("working", "invalid file loaded")
            }
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

                                Log.d("working", argbToHue(color).toString())

                                val markerOptions = MarkerOptions()
                                    .position(LatLng(coordinates.getDouble(0), coordinates.getDouble(1)))
                                    .title(header)
                                    .snippet(body)
                                    .icon(BitmapDescriptorFactory.defaultMarker(argbToHue(color)))

                                map.addMarker(markerOptions)
                            }
                            "LineString" -> {
                                currentLayerDisplayed = GeoJsonLayer(map, feature)
                                currentLayerDisplayed.defaultLineStringStyle.color = Color.Red.toArgb()
                                currentLayerDisplayed.addLayerToMap()
                            }
                            "Polygon" -> {
                                currentLayerDisplayed = GeoJsonLayer(map, feature)
                                currentLayerDisplayed.defaultPolygonStyle.fillColor = Color(0x64FF0000).toArgb()
                                currentLayerDisplayed.defaultPolygonStyle.strokeColor = Color.Red.toArgb()
                                currentLayerDisplayed.addLayerToMap()
                            }
                        }
                    }
                }
            } else {
                currentLayerDisplayed = GeoJsonLayer(map, geoJSONObject)

                currentLayerDisplayed.defaultLineStringStyle.color = Color.Red.toArgb()
                currentLayerDisplayed.defaultPolygonStyle.fillColor = Color(0x64FF0000).toArgb()
                currentLayerDisplayed.defaultPolygonStyle.strokeColor = Color.Red.toArgb()

                currentLayerDisplayed.addLayerToMap()
            }
        } catch (error: Exception) {
            Log.d("working", error.toString())
        }


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

    private fun argbToHue(rgbColorString: String): Float {
        if (rgbColorString.isEmpty()) {
            return 0.0f
        }

        val color = AndroidColor.parseColor(rgbColorString)

        val hsv = FloatArray(3)
        AndroidColor.RGBToHSV(AndroidColor.red(color), AndroidColor.green(color), AndroidColor.blue(color), hsv)

        return hsv[0]
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