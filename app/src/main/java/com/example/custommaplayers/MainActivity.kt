package com.example.custommaplayers

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.custommaplayers.data.DataProvider
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.data.geojson.GeoJsonLayer
import org.json.JSONObject


class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mapView: GoogleMap
    private lateinit var currentLayerDisplayed: GeoJsonLayer
    private val dataProvider = DataProvider()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapView) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val drawFromFileButton = findViewById<Button>(R.id.draw_from_file_btn)
        val getFromServerButton = findViewById<Button>(R.id.get_from_server_btn)

        drawFromFileButton.setOnClickListener {
            getFromJSONFile()
        }

        getFromServerButton.setOnClickListener {
            getFromServer()
        }
    }

    override fun onMapReady(map: GoogleMap) {
        mapView = map
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

                val bounds = LatLngBounds(LatLng(south, west), LatLng(north, east))

                mapView.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
            }
        }
    }
}
