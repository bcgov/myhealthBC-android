package ca.bc.gov.bchealth.ui.healthrecord

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ca.bc.gov.bchealth.databinding.ItemHealthRecordLoginBinding
import ca.bc.gov.bchealth.databinding.ItemHealthRecordOptionBinding
import ca.bc.gov.bchealth.ui.healthrecord.add.HealthRecordOption
import ca.bc.gov.bchealth.ui.healthrecord.add.OptionType

/**
 * @author Pinakin Kansara
 */
class HealthRecordOptionAdapter(
    private val itemClickListener: ItemClickListener
) : ListAdapter<HealthRecordOption, RecyclerView.ViewHolder>(
    HealthRecordOptionDiffCallBack()
) {

    fun interface ItemClickListener {
        fun onItemClick(optionType: OptionType)
    }

    class ViewHolderOptions(val binding: ItemHealthRecordOptionBinding) :
        RecyclerView.ViewHolder(binding.root)

    class ViewHolderLogin(val binding: ItemHealthRecordLoginBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return when (viewType) {
            OptionType.VACCINE.ordinal,
            OptionType.TEST.ordinal -> {
                val binding = ItemHealthRecordOptionBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                ViewHolderOptions(binding)
            }
            OptionType.LOGIN.ordinal -> {
                val binding = ItemHealthRecordLoginBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                ViewHolderLogin(binding)
            }
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val option = getItem(position)
        when (holder) {
            is ViewHolderOptions -> {
                holder.binding.tvGetVaccinationRecordTitle.setText(option.titleStringResource)
                holder.binding.tvGetVaccinationRecordSubtitle.setText(option.descriptionStringResource)
                holder.binding.ivVaccinationRecordIcon.setBackgroundResource(option.iconDrawableResource)
                holder.itemView.setOnClickListener {
                    itemClickListener.onItemClick(option.type)
                }
            }

            is ViewHolderLogin -> {
                holder.binding.tvTitle.setText(option.titleStringResource)
                holder.binding.tvLoginMsg.setText(option.descriptionStringResource)
                holder.binding.btnLogin.setOnClickListener {
                    itemClickListener.onItemClick(option.type)
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position).type.ordinal
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
