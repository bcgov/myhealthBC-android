package ca.bc.gov.common.utils

import android.content.Context

/**
 * Helper function to read file from asset
 * and return String JSON.
 */
fun Context.readJsonFromAsset(fileName: String) =
    this.assets.open(fileName).bufferedReader().use { it.readText() }

fun String.toUniquePatientName(): String {
    var uniqueName = this
    val nameList = this.split(" ")
    if (nameList.isNotEmpty() && nameList.size > 1) {
        val firstName = nameList.first()
        val lastInitial = nameList.lastOrNull()?.toCharArray()?.first()?.toString() ?: ""
        uniqueName = "$firstName $lastInitial"
    }
    return uniqueName
}
