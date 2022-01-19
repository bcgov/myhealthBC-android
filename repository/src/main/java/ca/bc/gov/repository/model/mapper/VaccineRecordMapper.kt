package ca.bc.gov.repository.model.mapper

import ca.bc.gov.common.model.VaccineRecordDto
import ca.bc.gov.data.local.entity.VaccineRecordEntity

fun VaccineRecordEntity.toVaccineRecord() = VaccineRecordDto(
    id = id,
    patientId = patientId,
    qrIssueDate = qrIssueDate,
    status = status,
    shcUri = shcUri,
    qrCodeImage = null,
    federalPass = federalPass,
    mode = dataSource
)
