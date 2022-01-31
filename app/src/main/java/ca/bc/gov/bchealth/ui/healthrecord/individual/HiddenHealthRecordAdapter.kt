package ca.bc.gov.bchealth.ui.healthrecord.individual

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ca.bc.gov.bchealth.databinding.ItemLoginForHiddenRecordsBinding

class HiddenHealthRecordAdapter(private val loginClickListener: ItemClickListener) :
    RecyclerView.Adapter<HiddenHealthRecordAdapter.ViewHolder>() {

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

        holder.binding.btnLogin.setOnClickListener {
            loginClickListener.onItemClick()
        }
    }

    override fun getItemCount(): Int {
        return 1
    }
}
