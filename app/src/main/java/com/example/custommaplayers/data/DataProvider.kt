package com.example.custommaplayers.data

import android.content.Context
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.gson.JsonArray
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import kotlin.reflect.typeOf

class DataProvider {
     fun getFromServer(
         objectID: String,
         putLayerOnMap: (JSONObject) -> Unit
     ) {
        val db = Firebase.firestore
        val docRef = db.collection("geofiles").document(objectID).get()
        var geoJSONObject = JSONObject()

        docRef
            .addOnSuccessListener { document ->
                if (document != null) {
                    if (document.data != null) {
                        Log.d("working", "DocumentSnapshot data: ${document.data}")
                        // TODO: FIX POLYGON
                        val data = JSONObject(document.data!!)
                        val coordinates = mutableListOf<List<Double>>()
                        val parts = data.getJSONObject("geometry").getString("coordinates").split(",")
                        for (i in parts.indices step 2) {
                            val x = parts[i].toDouble()
                            val y = parts[i + 1].toDouble()
                            coordinates.add(listOf(y, x))
                        }
                        data.getJSONObject("geometry").put("coordinates", JSONArray(coordinates))
                        Log.d("working", "data $data")
                        geoJSONObject = data
                    } else {
                        Log.d("working", "Document data is null")
                    }
                } else {
                    Log.d("working", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("working", "get failed with ", exception)
            }
            .addOnCompleteListener {
                putLayerOnMap(geoJSONObject)
            }
    }

    fun getJSONFromRawResource(context: Context, resourceId: Int): JSONObject {
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

    fun getObjectListFromServer() : List<String> {
        val db = Firebase.firestore
        val collectionRef = db.collection("geofiles").get()
        val objects = mutableListOf<String>()

        collectionRef
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.d("working", "${document.id} => ${document.data}")
                    objects.add(document.id)
                }
            }
            .addOnFailureListener { exception ->
                Log.d("working", "Error getting documents: ", exception)
            }

        Log.d("working", objects.toString())
        return objects
    }
}