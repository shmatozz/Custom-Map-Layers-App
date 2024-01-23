package com.example.custommaplayers

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.maps.android.data.geojson.GeoJsonLayer
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader


class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mapView: GoogleMap
    private lateinit var currentLayerDisplayed: GeoJsonLayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapView) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val drawFromFileButton = findViewById<Button>(R.id.draw_from_file_btn)
        val getFromServerButton = findViewById<Button>(R.id.get_from_server_btn)

        drawFromFileButton.setOnClickListener {
            getFromGeoJSON()
        }

        getFromServerButton.setOnClickListener {
            getFromServer()
        }
    }

    override fun onMapReady(map: GoogleMap) {
        mapView = map
    }

    private fun getFromGeoJSON() {
        val geoJSON = getJSONFromRawResource(applicationContext, R.raw.hse_cords)

        // put info from JSON
        putLayerOnMap(geoJSON)
    }

    private fun getJSONFromRawResource(context: Context, resourceId: Int): JSONObject {
        val inputStream: InputStream = context.resources.openRawResource(resourceId)
        val reader = BufferedReader(InputStreamReader(inputStream))
        val stringBuilder = StringBuilder()

        try {
            var line = reader.readLine()
            while (line != null) {
                stringBuilder.append(line).append('\n')
                line = reader.readLine()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            reader.close()
        }

        return JSONObject(stringBuilder.toString())
    }

    private fun getFromServer() {
        val db = Firebase.firestore
        val docRef = db.collection("geofiles").document("B1HY2BXZkYxrgu51quOa")
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d("custom", "DocumentSnapshot data: ${document.data}")
                    if (document.data != null) {
                        putLayerOnMap(JSONObject(document.data!!))
                    } else {
                        Log.d("custom", "Document data is null")
                    }
                } else {
                    Log.d("custom", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("custom", "get failed with ", exception)
            }
    }

    private fun putLayerOnMap(geoJSONObject: JSONObject) {
        mapView.clear()

        currentLayerDisplayed = GeoJsonLayer(mapView, geoJSONObject)
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

                val southwest = LatLng(south, west)
                val northeast = LatLng(north, east)
                val bounds = LatLngBounds(southwest, northeast)

                mapView.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
            }
        }
    }
}
