package ca.bc.gov.bchealth.ui.dependents

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ca.bc.gov.bchealth.databinding.ItemDependentAgedOutBinding
import ca.bc.gov.bchealth.databinding.ItemDependentBinding

private const val VIEW_TYPE_REGULAR = 0
private const val VIEW_TYPE_AGED_OUT = 1

class DependentAdapter(
    private val onTapItem: (DependentDetailItem) -> Unit,
    private val onTapRemove: (DependentDetailItem) -> Unit
) : ListAdapter<DependentDetailItem, RecyclerView.ViewHolder>(DependentDiffCallBacks()) {

    class DependentViewHolder(val binding: ItemDependentBinding) :
        RecyclerView.ViewHolder(binding.root)

    class DependentAgedOutViewHolder(val binding: ItemDependentAgedOutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun getItemViewType(position: Int): Int =
        if (getItem(position).agedOut) VIEW_TYPE_AGED_OUT else VIEW_TYPE_REGULAR

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        when (viewType) {
            VIEW_TYPE_REGULAR -> DependentViewHolder(
                ItemDependentBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
            VIEW_TYPE_AGED_OUT -> DependentAgedOutViewHolder(
                ItemDependentAgedOutBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
            else -> throw RuntimeException("Invalid ViewType: $viewType")
        }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val dependent = getItem(position)

        when (holder) {
            is DependentViewHolder -> onBindDependent(holder, dependent)
            is DependentAgedOutViewHolder -> onBindDependentAgedOut(holder, dependent)
        }
    }

    private fun onBindDependent(holder: DependentViewHolder, dependent: DependentDetailItem) =
        with(holder) {
            itemView.setOnClickListener { onTapItem(dependent) }
            binding.txtLabel.text = dependent.fullName
        }

    private fun onBindDependentAgedOut(
        holder: DependentAgedOutViewHolder,
        dependent: DependentDetailItem
    ) = with(holder.binding) {
        btnRemove.setOnClickListener { onTapRemove(dependent) }
        txtLabel.text = dependent.fullName
    }
}

class DependentDiffCallBacks : DiffUtil.ItemCallback<DependentDetailItem>() {
    override fun areItemsTheSame(
        oldItem: DependentDetailItem,
        newItem: DependentDetailItem
    ): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: DependentDetailItem,
        newItem: DependentDetailItem
    ): Boolean {
        return oldItem.patientId == newItem.patientId
    }
}
