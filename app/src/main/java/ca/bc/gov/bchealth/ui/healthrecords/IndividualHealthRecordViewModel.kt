package ca.bc.gov.bchealth.ui.healthrecords

import androidx.lifecycle.ViewModel
import ca.bc.gov.bchealth.model.HealthCardDto
import ca.bc.gov.bchealth.model.healthrecords.HealthRecord
import ca.bc.gov.bchealth.model.healthrecords.VaccineData
import ca.bc.gov.bchealth.repository.CardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/*
* Created by amit_metri on 25,November,2021
*/
@HiltViewModel
class IndividualHealthRecordViewModel @Inject constructor(
        cardRepository: CardRepository
) : ViewModel() {

    fun prepareVaccineDataList(healthRecordDto: HealthCardDto): HealthRecord {

        val vaccineDataList: MutableList<VaccineData> = mutableListOf()
        healthRecordDto.immunizationEntries?.forEachIndexed { index, entry ->

            vaccineDataList.add(
                    VaccineData(healthRecordDto.name,
                            (index + 1).toString(),
                            entry.resource.occurrenceDateTime,
                            "",
                            "",
                            entry.resource.performer?.last()?.actor?.display,
                            entry.resource.lotNumber)
            )
        }
        return HealthRecord(healthRecordDto.name,
                healthRecordDto.status,
                healthRecordDto.issueDate,
                vaccineDataList,
                mutableListOf())
    }
}