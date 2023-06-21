package ca.bc.gov.bchealth.ui.healthrecord.individual

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.ItemLoginForHiddenRecordsBinding

class HiddenHealthRecordAdapter(private val loginClickListener: ItemClickListener) :
    ListAdapter<HiddenRecordItem, HiddenHealthRecordAdapter.ViewHolder>(HiddenRecordDiffCallBacks()) {

    fun interface ItemClickListener {
        fun onItemClick()
    }

    class ViewHolder(val binding: ItemLoginForHiddenRecordsBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemLoginForHiddenRecordsBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.tvRecordsCount.text = holder.itemView.context.getString(R.string.session_time_out)

        holder.binding.btnLogin.setOnClickListener {
            loginClickListener.onItemClick()
        }
    }
}

class HiddenRecordDiffCallBacks : DiffUtil.ItemCallback<HiddenRecordItem>() {
    override fun areItemsTheSame(oldItem: HiddenRecordItem, newItem: HiddenRecordItem): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: HiddenRecordItem, newItem: HiddenRecordItem): Boolean {
        return oldItem.countOfRecords == newItem.countOfRecords
    }
}
