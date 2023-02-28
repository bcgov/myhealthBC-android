package ca.bc.gov.bchealth.ui.healthrecord.individual

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.ItemImmunizationBannerBinding
import ca.bc.gov.bchealth.utils.makeLinks

class ImmunizationBannerAdapter(
    private val onClickLink: () -> Unit,
    private val onClickClose: () -> Unit
) :
    RecyclerView.Adapter<ImmunizationBannerAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemImmunizationBannerBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = ViewHolder(
        ItemImmunizationBannerBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply {
            val clickableText = root.resources.getString(R.string.records_immunization_banner_click)
            tvBody.makeLinks(clickableText to View.OnClickListener { onClickLink.invoke() })
            btnClose.setOnClickListener { onClickClose() }
        }
    }

    override fun getItemCount(): Int = 1
}
