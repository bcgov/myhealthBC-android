package ca.bc.gov.repository.model.mapper

import ca.bc.gov.common.model.CreateVaccineRecordDto
import ca.bc.gov.common.model.VaccineRecord
import ca.bc.gov.data.local.entity.VaccineRecordEntity



fun VaccineRecord.toCreateVaccineRecordDto(patientId: Long) = CreateVaccineRecordDto(
    id = id,
    patientId,
    qrIssueDate,
    status,
    shcUri!!,
    federalPass,
    mode,
)

fun VaccineRecordEntity.toVaccineRecord() = VaccineRecord(
    id = id,
    patientId = patientId,
    qrIssueDate = qrIssueDate,
    status = status,
    shcUri = shcUri,
    qrCodeImage = null,
    federalPass = federalPass,
    mode = dataSource
)