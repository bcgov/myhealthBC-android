package ca.bc.gov.bchealth.ui.healthrecord.individual

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.ItemHealthRecordsAbstractBinding
import ca.bc.gov.common.utils.toDate
import ca.bc.gov.common.utils.toStartOfDayInstant

/**
 * @author Pinakin Kansara
 */
class HealthRecordsAdapter(
    private val itemClickListener: ItemClickListener,
    private val itemDeleteListener: ItemDeleteListener,
    private val itemUpdateListener: ItemUpdateListener,
    var canDeleteRecord: Boolean = false,
    var isUpdateRequested: Boolean
) :
    ListAdapter<HealthRecordItem, HealthRecordsAdapter.ViewHolder>(HealthRecordDiffCallBacks()),
    Filterable {

    private lateinit var defaultList: List<HealthRecordItem>

    fun interface ItemClickListener {
        fun onItemClick(record: HealthRecordItem)
    }

    fun interface ItemDeleteListener {
        fun onDeleteClick(record: HealthRecordItem)
    }

    fun interface ItemUpdateListener {
        fun onUpdateRequested(record: HealthRecordItem)
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
        var description = ""
        holder.binding.imgIcon.setImageResource(record.icon)
        when (record.healthRecordType) {
            HealthRecordType.VACCINE_RECORD -> {
                description = record.date.toDate()
                holder.binding.ivUnlink.isVisible = canDeleteRecord
                holder.binding.ivRightArrow.isVisible = !canDeleteRecord
                holder.binding.ivUnlink.setOnClickListener {
                    itemDeleteListener.onDeleteClick(record)
                }
            }
            HealthRecordType.COVID_TEST_RECORD -> {
                description = "${record.testOutcome} • ${record.date.toDate()}"
                holder.binding.ivUnlink.isVisible = canDeleteRecord
                holder.binding.ivRightArrow.isVisible = !canDeleteRecord
                holder.binding.ivUnlink.setOnClickListener {
                    itemDeleteListener.onDeleteClick(record)
                }
                /*
                * Retry fetching the covid test result for pending record
                * */
                if (record.testOutcome == holder.itemView.resources.getString(R.string.pending) &&
                    isUpdateRequested
                ) {
                    holder.binding.progressBar.visibility = View.VISIBLE
                    holder.binding.ivRightArrow.visibility = View.INVISIBLE
                    itemUpdateListener.onUpdateRequested(record)
                    isUpdateRequested = false
                } else {
                    holder.binding.progressBar.visibility = View.INVISIBLE
                    holder.binding.ivRightArrow.visibility =
                        if (canDeleteRecord) View.GONE else View.VISIBLE
                }
            }
            HealthRecordType.MEDICATION_RECORD -> {
                description = "${record.description} • ${record.date.toDate()}"
            }
            HealthRecordType.LAB_TEST -> {
                description = record.description
            }
            HealthRecordType.IMMUNIZATION_RECORD -> {
                description = record.date.toDate()
            }
        }

        holder.binding.tvDesc.text = description
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
                val filteredList = mutableListOf<HealthRecordItem>()
                val tempList = mutableListOf<HealthRecordItem>()
                if (charSequence.isNullOrBlank()) {
                    return FilterResults()
                } else {
                    val list = charSequence.split(",")
                    Log.i("RASHMI", "charSequence list: $list")

                    val fromDate = list.find { it.contains("FROM:") }?.substringAfter(":")
                    val toDate = list.find { it.contains("TO:") }?.substringAfter(":")

                    if (!fromDate.isNullOrBlank() && !toDate.isNullOrBlank()) {
                        tempList.addAll(defaultList.filter { it.date.toStartOfDayInstant() >= fromDate.toDate() && it.date <= toDate.toDate() })
                    } else if (!fromDate.isNullOrBlank()) {
                        tempList.addAll(defaultList.filter { it.date.toStartOfDayInstant() >= fromDate.toDate() })
                    } else if (!toDate.isNullOrBlank()) {
                        tempList.addAll(defaultList.filter { it.date.toStartOfDayInstant() <= toDate.toDate() })
                    } else {
                        tempList.addAll(defaultList)
                    }

                    for (i in list.indices) {
                        when (list[i]) {
                            "ALL" -> {
                                filteredList.addAll(tempList)
                            }
                            "MEDICATION" -> {
                                filteredList.addAll(tempList.filter { it.healthRecordType == HealthRecordType.MEDICATION_RECORD })
                            }
                            "LAB_TEST" -> {
                                filteredList.addAll(tempList.filter { it.healthRecordType == HealthRecordType.LAB_TEST })
                            }
                            "COVID_19_TEST" -> {
                                filteredList.addAll(tempList.filter { it.healthRecordType == HealthRecordType.COVID_TEST_RECORD })
                            }
                            "IMMUNIZATION" -> {
                                filteredList.addAll(tempList.filter { it.healthRecordType == HealthRecordType.IMMUNIZATION_RECORD })
                            }
                            "PROTECTIVE_WORD" -> {
                                filteredList.removeAll { it.healthRecordType == HealthRecordType.MEDICATION_RECORD }
                            }
                        }
                    }
                    Log.i("RASHMI", "filter list size: ${filteredList.size}")
                    // list.forEach { type ->
                    //     when (type) {
                    //         "ALL" -> {
                    //             filteredList.addAll(defaultList)
                    //         }
                    //         "MEDICATION" -> {
                    //             filteredList.addAll(defaultList.filter { it.healthRecordType == HealthRecordType.MEDICATION_RECORD })
                    //         }
                    //         "LAB_TEST" -> {
                    //             filteredList.addAll(defaultList.filter { it.healthRecordType == HealthRecordType.LAB_TEST })
                    //         }
                    //         "COVID_19_TEST" -> {
                    //             filteredList.addAll(defaultList.filter { it.healthRecordType == HealthRecordType.COVID_TEST_RECORD })
                    //         }
                    //         "IMMUNIZATION" -> {
                    //             filteredList.addAll(defaultList.filter { it.healthRecordType == HealthRecordType.IMMUNIZATION_RECORD })
                    //         }
                    //     }
                    // }
                    return FilterResults().apply { values = filteredList }
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
}

class HealthRecordDiffCallBacks : DiffUtil.ItemCallback<HealthRecordItem>() {
    override fun areItemsTheSame(oldItem: HealthRecordItem, newItem: HealthRecordItem): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: HealthRecordItem, newItem: HealthRecordItem): Boolean {
        return oldItem.title == newItem.title
    }
}
