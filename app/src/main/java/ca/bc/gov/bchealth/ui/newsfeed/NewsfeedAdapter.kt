package ca.bc.gov.bchealth.ui.newsfeed

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ca.bc.gov.bchealth.databinding.ItemNewsFeedRowBinding
import ca.bc.gov.bchealth.model.rss.Newsfeed
import ca.bc.gov.common.utils.eee_dd_mmm_yyyy_hh_mm_ss_z
import ca.bc.gov.common.utils.toDate
import ca.bc.gov.common.utils.toDateTime
import java.time.format.DateTimeFormatter

/**
 * [NewsfeedAdapter]
 *
 * @author amit metri
 */
class NewsfeedAdapter(
    var newsFeeds: MutableList<Newsfeed>,
    val clickListener: (Newsfeed) -> Unit
) : RecyclerView.Adapter<NewsfeedAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemNewsFeedRowBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemNewsFeedRowBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            )
        )
    }

    @SuppressLint("SimpleDateFormat")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = newsFeeds[position]

        holder.binding.tvTitle.text = item.title
        holder.binding.tvDesc.text = item.description

        holder.binding.tvDate.text =
            item.pubDate?.toDateTime(DateTimeFormatter.ofPattern(eee_dd_mmm_yyyy_hh_mm_ss_z))
                ?.toDate()
        holder.itemView.setOnClickListener {
            clickListener(item)
        }
    }

    override fun getItemCount(): Int {
        return newsFeeds.size
    }
}
