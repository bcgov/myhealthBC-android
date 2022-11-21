package ca.bc.gov.bchealth.ui.dependents

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ca.bc.gov.bchealth.databinding.ItemDependentBinding

class DependentAdapter(
    private val action: (DependentDetailItem) -> Unit
) : ListAdapter<DependentDetailItem, DependentAdapter.ViewHolder>(DependentDiffCallBacks()) {

    class ViewHolder(val binding: ItemDependentBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemDependentBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dependent = getItem(position)
        holder.itemView.setOnClickListener {
            action(dependent)
        }
        holder.binding.txtLabel.text = dependent.fullName
    }
}

class DependentDiffCallBacks : DiffUtil.ItemCallback<DependentDetailItem>() {
    override fun areItemsTheSame(oldItem: DependentDetailItem, newItem: DependentDetailItem): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: DependentDetailItem, newItem: DependentDetailItem): Boolean {
        return oldItem.patientId == newItem.patientId
    }
}
