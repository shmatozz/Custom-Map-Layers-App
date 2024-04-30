package com.example.custommaplayers.data

import org.json.JSONArray
import org.json.JSONObject

class FeaturesParser {
    fun parseFeatureCollection(data: JSONObject) : JSONObject {
        val features: JSONArray = data.getJSONArray("features")

        for (i in 0 until features.length()) {
            features.put(i, parseFeature(features.getJSONObject(i)))
        }

        return data
    }

    fun parseFeature(data: JSONObject) : JSONObject {
        if (data.getJSONObject("geometry").getString("type") != "Point") {
            /* Parse coordinates from string to array */
            val coordinates = mutableListOf<List<Double>>()
            val parts = data.getJSONObject("geometry").getString("coordinates").split(",")
            for (i in parts.indices step 2) {
                val x = parts[i].toDouble()
                val y = parts[i + 1].toDouble()
                coordinates.add(listOf(y, x))
            }

            /* Check geometry type, if "Polygon" -> add one more array layer */
            if (data.getJSONObject("geometry").getString("type") == "Polygon") {
                data.getJSONObject("geometry").put("coordinates", JSONArray(mutableListOf(coordinates)))
            } else {
                data.getJSONObject("geometry").put("coordinates", JSONArray(coordinates))
            }
        } else {
            /* Switch coordinates */
            val coordinates = data.getJSONObject("geometry").getJSONArray("coordinates")
            val temp = coordinates.getDouble(0)
            coordinates.put(0, coordinates.getDouble(1))
            coordinates.put(1, temp)
        }

        return data
    }
}