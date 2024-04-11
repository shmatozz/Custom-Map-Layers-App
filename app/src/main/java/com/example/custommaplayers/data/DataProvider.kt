package com.example.custommaplayers.data

import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

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
                        /* Get data */
                        val data = JSONObject(document.data!!)

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

                        /* Save data */
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
                /* If no errors -> put layer on map */
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

    fun getJSONFromUri(context: Context, uri: Uri): JSONObject {
        val inputStream = context.contentResolver.openInputStream(uri)
        val json = inputStream?.bufferedReader().use {
            it?.readText()
        }
        inputStream?.close()
        return JSONObject(json!!)
    }

    suspend fun getObjectListFromServer(): List<String> = withContext(Dispatchers.IO) {
        val db = Firebase.firestore
        val collectionRef = db.collection("geofiles").get().await()
        val objects = mutableListOf<String>()

        for (document in collectionRef) {
            Log.d("working", document.data.toString())
            objects.add(document.id)
        }

        return@withContext objects
    }
}