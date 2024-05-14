package com.example.custommaplayers.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.custommaplayers.data.DataProvider
import com.example.custommaplayers.model.ObjectsState
import com.example.custommaplayers.ui.theme.rgbToHue
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolygonOptions
import com.google.android.gms.maps.model.PolylineOptions
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

class MapViewModel : ViewModel() {
    var showServerFilesDialog by mutableStateOf(false)
    var showErrorDialog by mutableStateOf(false)

    var objectsList by mutableStateOf(emptyList<String>())
    var objectsState by mutableStateOf(ObjectsState())

    val dataProvider = DataProvider()

    lateinit var map: GoogleMap

    fun loadObjects() {
        viewModelScope.launch {
            objectsState = objectsState.copy(
                objectsList = null,
                isLoading = true
            )
            dataProvider.getObjectsListFromServer { newObjectsList ->
                for (obg in newObjectsList) {
                    Log.d("working", "aa $obg")
                }
                objectsState = objectsState.copy(
                    objectsList = newObjectsList,
                    isLoading = false
                )
            }
        }
    }

    fun getFromServer(objectID: String) {
        dataProvider.getFromServer(objectID) {
            putLayerOnMap(it)
        }
    }

    fun putLayerOnMap(geoJSONObject: JSONObject) {
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
                                map.addPolygon(
                                    PolygonOptions().addAll(points).fillColor(Color(0x64FF0000).toArgb()).strokeColor(
                                        Color.Red.toArgb()))
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
            showErrorDialog = true
        }
    }
}