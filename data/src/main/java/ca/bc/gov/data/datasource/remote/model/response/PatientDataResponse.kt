package ca.bc.gov.data.datasource.remote.model.response

import ca.bc.gov.data.datasource.remote.model.base.patientdata.PatientData

/**
 * @author Pinakin Kansara
 * uses api = v2
 */
data class PatientDataResponse(
    /**
     * Hold multiple type of patient data.
     */
    val items: List<PatientData> = emptyList()
)
