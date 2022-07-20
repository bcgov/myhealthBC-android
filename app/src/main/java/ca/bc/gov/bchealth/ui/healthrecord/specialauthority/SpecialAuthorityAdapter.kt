package ca.bc.gov.bchealth.ui.healthrecord.specialauthority

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ca.bc.gov.bchealth.databinding.ItemSpecialAuthorityBinding

/*
* Created by amit_metri on 29,June,2022
*/
class SpecialAuthorityAdapter() : ListAdapter<SpecialAuthorityDetailItem, SpecialAuthorityAdapter.SpecialAuthorityViewHolder>(
    DiffCallBacks()
) {

    class SpecialAuthorityViewHolder(val binding: ItemSpecialAuthorityBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpecialAuthorityViewHolder {
        val binding = ItemSpecialAuthorityBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return SpecialAuthorityViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SpecialAuthorityViewHolder, position: Int) {
        val item = getItem(position)
        holder.binding.tvTitle.text = holder.itemView.resources.getString(item.title)
        holder.binding.tvDesc.text = item.desc
    }
}

class DiffCallBacks : DiffUtil.ItemCallback<SpecialAuthorityDetailItem>() {
    override fun areItemsTheSame(
        oldItem: SpecialAuthorityDetailItem,
        newItem: SpecialAuthorityDetailItem
    ): Boolean {
        return oldItem.title == newItem.title
    }

    override fun areContentsTheSame(
        oldItem: SpecialAuthorityDetailItem,
        newItem: SpecialAuthorityDetailItem
    ): Boolean {
        return oldItem == newItem
    }
}
