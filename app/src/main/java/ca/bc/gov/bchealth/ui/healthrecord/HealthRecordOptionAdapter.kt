package ca.bc.gov.bchealth.ui.healthrecord

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ca.bc.gov.bchealth.databinding.ItemHealthRecordOptionBinding
import ca.bc.gov.bchealth.ui.healthrecord.add.HealthRecordOption
import ca.bc.gov.bchealth.ui.healthrecord.add.OptionType

/**
 * @author Pinakin Kansara
 */
class HealthRecordOptionAdapter(
    private val itemClickListener: ItemClickListener
) : ListAdapter<HealthRecordOption, HealthRecordOptionAdapter.ViewHolder>(
    HealthRecordOptionDiffCallBack()
) {

    fun interface ItemClickListener {
        fun onItemClick(optionType: OptionType)
    }

    class ViewHolder(val binding: ItemHealthRecordOptionBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = ItemHealthRecordOptionBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val option = getItem(position)
        holder.binding.tvGetVaccinationRecordTitle.setText(option.titleStringResource)
        holder.binding.tvGetVaccinationRecordSubtitle.setText(option.descriptionStringResource)
        holder.binding.ivVaccinationRecordIcon.setBackgroundResource(option.iconDrawableResource)

        holder.itemView.setOnClickListener {
            itemClickListener.onItemClick(option.type)
        }
    }
}

class HealthRecordOptionDiffCallBack : DiffUtil.ItemCallback<HealthRecordOption>() {
    override fun areItemsTheSame(
        oldItem: HealthRecordOption,
        newItem: HealthRecordOption
    ): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: HealthRecordOption,
        newItem: HealthRecordOption
    ): Boolean {
        return oldItem.titleStringResource == newItem.titleStringResource
    }
}