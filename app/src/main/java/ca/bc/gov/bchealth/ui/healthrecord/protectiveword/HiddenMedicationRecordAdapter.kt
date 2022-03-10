package ca.bc.gov.bchealth.ui.healthrecord.protectiveword

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ca.bc.gov.bchealth.databinding.ItemHiddenMedicationRecordsBinding

class HiddenMedicationRecordAdapter(private val accessClickListener: ItemClickListener) :
    RecyclerView.Adapter<HiddenMedicationRecordAdapter.HiddenMedicationRecordViewHolder>() {

    fun interface ItemClickListener {
        fun onItemClick()
    }

    class HiddenMedicationRecordViewHolder(val binding: ItemHiddenMedicationRecordsBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HiddenMedicationRecordViewHolder {
        return HiddenMedicationRecordViewHolder(
            ItemHiddenMedicationRecordsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: HiddenMedicationRecordViewHolder, position: Int) {
        holder.binding.btnAccess.setOnClickListener {
            accessClickListener.onItemClick()
        }
    }

    override fun getItemCount(): Int {
        return 1
    }
}
