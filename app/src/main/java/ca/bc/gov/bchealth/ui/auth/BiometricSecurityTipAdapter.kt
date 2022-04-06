package ca.bc.gov.bchealth.ui.auth

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ca.bc.gov.bchealth.databinding.ItemBiometricSecurityTipBinding

class BiometricSecurityTipAdapter :
    ListAdapter<SecurityTipItem, BiometricSecurityTipAdapter.BiometricSecurityTipViewHolder>(
        BiometricSecurityTipDiffCallBacks()
    ) {

    class BiometricSecurityTipViewHolder(val binding: ItemBiometricSecurityTipBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BiometricSecurityTipViewHolder {
        val binding = ItemBiometricSecurityTipBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )

        return BiometricSecurityTipViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BiometricSecurityTipViewHolder, position: Int) {

        holder.binding.apply {
            ivSecurityTip.setBackgroundResource(getItem(position).icon)
            txtSecurityTip.setText(getItem(position).title)
        }
    }
}

class BiometricSecurityTipDiffCallBacks : DiffUtil.ItemCallback<SecurityTipItem>() {
    override fun areItemsTheSame(oldItem: SecurityTipItem, newItem: SecurityTipItem): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: SecurityTipItem, newItem: SecurityTipItem): Boolean {
        return oldItem.title == newItem.title
    }
}
