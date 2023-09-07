package ca.bc.gov.bchealth.ui.healthrecord.individual

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ca.bc.gov.bchealth.databinding.ItemHealthRecordsAbstractBinding
import ca.bc.gov.bchealth.ui.filter.TimelineTypeFilter
import ca.bc.gov.bchealth.ui.healthrecord.HealthRecordItem
import ca.bc.gov.common.utils.toDate
import ca.bc.gov.common.utils.toStartOfDayInstant

/**
 * @author Pinakin Kansara
 */
class HealthRecordsAdapter(
    private val itemClickListener: ItemClickListener
) :
    ListAdapter<HealthRecordItem, HealthRecordsAdapter.ViewHolder>(HealthRecordDiffCallBacks()),
    Filterable {

    private lateinit var defaultList: List<HealthRecordItem>

    fun interface ItemClickListener {
        fun onItemClick(record: HealthRecordItem)
    }

    class ViewHolder(val binding: ItemHealthRecordsAbstractBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ItemHealthRecordsAbstractBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val record = getItem(position)
        holder.binding.tvTitle.text = record.title
        holder.binding.imgIcon.setImageResource(record.icon)
        holder.binding.tvDesc.text = record.description
        holder.itemView.setOnClickListener {
            itemClickListener.onItemClick(record)
        }
    }

    fun setData(list: List<HealthRecordItem>) {
        defaultList = list
        submitList(list)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence?): FilterResults {
                val resultList = mutableListOf<HealthRecordItem>()

                if (charSequence.isNullOrBlank()) {
                    return FilterResults()
                } else {
                    val queries = charSequence.split(",")

                    val fromDate = queries.find { it.contains("FROM:") }?.substringAfter(":")
                    val toDate = queries.find { it.contains("TO:") }?.substringAfter(":")
                    val search = queries.find { it.contains("SEARCH:") }?.substringAfter(":")

                    val listFilteredByDate = getFilterByDate(fromDate, toDate)

                    val listFilteredBySearch = if (search != null && search.isNotBlank()) {
                        listFilteredByDate.filter { record ->
                            record.title.contains(search, true)
                        }
                    } else {
                        listFilteredByDate
                    }

                    queries.forEach { query ->
                        when (query) {
                            TimelineTypeFilter.ALL.name -> resultList.addAll(listFilteredBySearch)

                            else -> {
                                val typeFilter = TimelineTypeFilter.findByName(query)
                                typeFilter?.let {
                                    val itemsToAdd = listFilteredBySearch.filter {
                                        it.healthRecordType == typeFilter.recordType
                                    }
                                    resultList.addAll(itemsToAdd)
                                }
                            }
                        }
                    }
                    return FilterResults().apply {
                        values = resultList.sortedByDescending { it.date }
                    }
                }
            }

            override fun publishResults(constraint: CharSequence?, result: FilterResults?) {
                if (result?.values == null) {
                    submitList(emptyList())
                } else {
                    submitList(result.values as List<HealthRecordItem>)
                }
            }
        }
    }

    private fun getFilterByDate(fromDate: String?, toDate: String?): MutableList<HealthRecordItem> {
        return if (!fromDate.isNullOrBlank() && !toDate.isNullOrBlank()) {
            defaultList.filter { it.date.toStartOfDayInstant() >= fromDate.toDate() && it.date <= toDate.toDate() }
                .toMutableList()
        } else if (!fromDate.isNullOrBlank()) {
            defaultList.filter { it.date.toStartOfDayInstant() >= fromDate.toDate() }
                .toMutableList()
        } else if (!toDate.isNullOrBlank()) {
            defaultList.filter { it.date.toStartOfDayInstant() <= toDate.toDate() }.toMutableList()
        } else {
            defaultList.toMutableList()
        }
    }
}

class HealthRecordDiffCallBacks : DiffUtil.ItemCallback<HealthRecordItem>() {
    override fun areItemsTheSame(oldItem: HealthRecordItem, newItem: HealthRecordItem): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: HealthRecordItem, newItem: HealthRecordItem): Boolean {
        return oldItem.title == newItem.title
    }
}
