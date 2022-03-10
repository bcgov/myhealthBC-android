package ca.bc.gov.bchealth.ui.healthrecord.protectiveword

import androidx.lifecycle.ViewModel
import ca.bc.gov.repository.MedicationRecordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProtectiveWordViewModel @Inject constructor
    (private val medicationRecordRepository: MedicationRecordRepository) : ViewModel() {

    fun clearIsProtectiveWordRequired() {
        return medicationRecordRepository.clearIsProtectiveWordRequired()
    }

    fun getProtectiveWord(): String? {
        return medicationRecordRepository.getProtectiveWord()
    }

    fun saveProtectiveWord(word: String) {
        medicationRecordRepository.saveProtectiveWord(word)
    }

    fun isProtectiveWordValid(word: String): Boolean {
        return if (medicationRecordRepository.getProtectiveWord() == null) {
            true
        } else {
            word == medicationRecordRepository.getProtectiveWord()
        }
    }
}