package ca.bc.gov.bchealth.utils

fun String?.orPlaceholder(placeholder: String = "--"): String =
    this ?: placeholder
