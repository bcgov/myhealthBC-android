package ca.bc.gov.bchealth.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ca.bc.gov.bchealth.databinding.ItemHomeWithButtonBgBinding
import ca.bc.gov.bchealth.databinding.ItemHomeWithoutButtonBgBinding

/**
 * @author: Created by Rashmi Bambhania on 21,March,2022
 */
class HomeAdapter(
    private val itemClick: (HomeNavigationType) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var records: List<HomeRecordItem> = emptyList()

    fun setData(records: List<HomeRecordItem>) {
        this.records = records
    }

    class HomeWithButtonBgViewHolder(val binding: ItemHomeWithButtonBgBinding) :
        RecyclerView.ViewHolder(binding.root)

    class HomeWithoutButtonBgViewHolder(val binding: ItemHomeWithoutButtonBgBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            HomeNavigationType.HEALTH_RECORD.ordinal -> {
                val binding = ItemHomeWithButtonBgBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                HomeWithButtonBgViewHolder(binding)
            }

            HomeNavigationType.VACCINE_PROOF.ordinal, HomeNavigationType.RESOURCES.ordinal, HomeNavigationType.RECOMMENDATIONS.ordinal -> {
                val binding = ItemHomeWithoutButtonBgBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                HomeWithoutButtonBgViewHolder(binding)
            }

            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val record = records[position]
        holder.itemView.setOnClickListener {
            itemClick.invoke(record.recordType)
        }
        when (holder) {
            is HomeWithButtonBgViewHolder -> {
                holder.binding.imgIcon.setBackgroundResource(record.iconTitle)
                holder.binding.tvTitle.text = holder.itemView.context.getString(record.title)
                holder.binding.tvDesc.text = holder.itemView.context.getString(record.description)
                holder.binding.btnContinue.setCompoundDrawablesWithIntrinsicBounds(
                    record.icon,
                    0,
                    0,
                    0
                )
                holder.binding.btnContinue.text = holder.itemView.context.getString(record.btnTitle)
            }

            is HomeWithoutButtonBgViewHolder -> {
                holder.binding.imgIcon.setBackgroundResource(record.iconTitle)
                holder.binding.tvTitle.text = holder.itemView.context.getString(record.title)
                holder.binding.tvDesc.text = holder.itemView.context.getString(record.description)
                holder.binding.imgContinue.setBackgroundResource(record.icon)
                holder.binding.tvContinue.text = holder.itemView.context.getString(record.btnTitle)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return records[position].recordType.ordinal
    }

    override fun getItemCount(): Int = records.size
}
