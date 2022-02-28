package ca.bc.gov.data.remote.model.base.labtest

/**
 * @author Pinakin Kansara
 */
data class LabTestOrder(
    val laboratoryReportId: String,
    val reportingSource: String?,
    val reportId: String?,
    val collectionDateTime: String,
    val commonName: String?,
    val orderingProvider: String?,
    val testStatus: String?,
    val reportAvailable: Boolean,
    val laboratoryTests: List<LaboratoryTest>
)
