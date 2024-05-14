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
import androidx.activity.viewModels
import androidx.core.view.WindowCompat
import com.example.custommaplayers.ui.main.MapScreen
import com.example.custommaplayers.viewmodel.MapViewModel
import com.example.testcomposemaps.ui.theme.CustomMapLayersTheme


class MainActivity : ComponentActivity() {

    private val mapViewModel: MapViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            CustomMapLayersTheme {
                MapScreen(
                    context = applicationContext,
                    mapViewModel = mapViewModel,
                    getFromJSONFile = { chooseFile() }
                )

                if (mapViewModel.showErrorDialog) {
                    showLoadErrorDialog()
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
                            val geoJSON = mapViewModel.dataProvider.getJSONFromUri(applicationContext, uri)
                            mapViewModel.putLayerOnMap(geoJSON)
                        } catch (e: Exception) {
                            mapViewModel.showErrorDialog = true
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