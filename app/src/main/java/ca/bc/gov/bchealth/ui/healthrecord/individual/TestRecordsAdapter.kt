package ca.bc.gov.bchealth.ui.healthrecord.individual

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ca.bc.gov.bchealth.databinding.ItemHealthRecordsAbstractBinding
import ca.bc.gov.common.model.test.TestResult
import ca.bc.gov.common.utils.toDateTimeString

/**
 * @author Pinakin Kansara
 */
class TestRecordsAdapter(
    private val itemClickListener: ItemClickListener
) :
    ListAdapter<TestResult, TestRecordsAdapter.ViewHolder>(TestRecordsDiffCallBacks()) {

    fun interface ItemClickListener {
        fun onItemClick(result: TestResult)
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
        val testResult = getItem(position)
        val name = "COVID-19 TEST RESULT"
        holder.binding.tvVaccineName.text = name
        holder.binding.tvVaccineStatus.text = testResult.collectionDate.toDateTimeString()

        holder.itemView.setOnClickListener {
            itemClickListener.onItemClick(testResult)
        }
    }
}

class TestRecordsDiffCallBacks : DiffUtil.ItemCallback<TestResult>() {
    override fun areItemsTheSame(oldItem: TestResult, newItem: TestResult): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: TestResult, newItem: TestResult): Boolean {
        return oldItem == newItem
    }
}