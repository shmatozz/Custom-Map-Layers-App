package com.example.custommaplayers.data

import android.content.Context
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

class DataProvider {
    fun getFromServer() : JSONObject {
        val db = Firebase.firestore
        val docRef = db.collection("geofiles").document("B1HY2BXZkYxrgu51quOa")
        var geoJSONObject = JSONObject()


        /* TODO fix returning earlier than receiving result from base*/
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    if (document.data != null) {
                        Log.d("custom", "DocumentSnapshot data: ${document.data}")
                        geoJSONObject = JSONObject(document.data!!)
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

        return geoJSONObject
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
}