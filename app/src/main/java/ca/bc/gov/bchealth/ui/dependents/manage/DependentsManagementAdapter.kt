package ca.bc.gov.bchealth.ui.dependents.manage

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ca.bc.gov.bchealth.databinding.ItemDependentBinding
import ca.bc.gov.bchealth.utils.show
import ca.bc.gov.common.model.dependents.DependentDto

class DependentsManagementAdapter(
    var dependents: List<DependentDto>,
    private val onDelete: (DependentDto) -> Unit
) : RecyclerView.Adapter<DependentsManagementAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemDependentBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        ItemDependentBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dependent = dependents[position]

        holder.binding.apply {
            icReorder.show()
            imgUnlink.show()
            imgUnlink.setOnClickListener {
                onDelete.invoke(dependent)
            }
            txtLabel.text = dependent.getFullName()
        }
    }

    override fun getItemCount(): Int = dependents.size
}
