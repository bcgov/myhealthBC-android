package ca.bc.gov.data.datasource.remote.model.base.labtest

/**
 * @author Pinakin Kansara
 */
data class LabTestOrder(
    val labPdfId: String,
    val reportingSource: String?,
    val reportId: String?,
    val collectionDateTime: String?,
    val timelineDateTime: String,
    val commonName: String?,
    val orderingProvider: String?,
    val testStatus: String?,
    val orderStatus: String?,
    val reportAvailable: Boolean,
    val laboratoryTests: List<LaboratoryTest>
)
