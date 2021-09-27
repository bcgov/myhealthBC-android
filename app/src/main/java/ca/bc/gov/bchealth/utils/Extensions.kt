package ca.bc.gov.bchealth.utils

import android.content.Context
import android.widget.Toast

/**
 * Helper function to read file from asset
 * and return String JSON.
 */
fun Context.readJsonFromAsset(fileName: String) =
    this.assets.open(fileName).bufferedReader().use { it.readText() }

/*
* For displaying Toast
* */
fun Context.toast(message: String)
        = Toast.makeText(this, message, Toast.LENGTH_LONG).show()

