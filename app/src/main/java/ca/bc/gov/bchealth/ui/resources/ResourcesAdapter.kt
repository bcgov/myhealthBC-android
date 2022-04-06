package ca.bc.gov.bchealth.ui.resources

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ca.bc.gov.bchealth.databinding.ItemResourceBinding

/**
 * @author: Created by Rashmi Bambhania on 21,March,2022
 */
class ResourcesAdapter(
    private val itemClickListener: ItemClickListener
) : ListAdapter<Resources, ResourcesAdapter.ViewHolder>(ResourcesDiffCallBacks()) {

    fun interface ItemClickListener {
        fun onItemClick(link: String)
    }

    class ViewHolder(val binding: ItemResourceBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemResourceBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val resource = getItem(position)
        holder.itemView.setOnClickListener {
            itemClickListener.onItemClick(holder.itemView.resources.getString(resource.link))
        }
        holder.binding.imgIcon.setBackgroundResource(resource.icon)
        holder.binding.txtLabel.text = holder.itemView.resources.getString(resource.title)
    }
}

class ResourcesDiffCallBacks : DiffUtil.ItemCallback<Resources>() {
    override fun areItemsTheSame(oldItem: Resources, newItem: Resources): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Resources, newItem: Resources): Boolean {
        return oldItem.title == newItem.title
    }
}
