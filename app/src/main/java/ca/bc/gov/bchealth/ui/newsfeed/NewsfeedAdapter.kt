package ca.bc.gov.bchealth.ui.newsfeed

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ca.bc.gov.bchealth.databinding.ItemNewsFeedRowBinding
import ca.bc.gov.bchealth.model.rss.Newsfeed
import ca.bc.gov.bchealth.utils.getNewsFeedDateTime
import java.text.SimpleDateFormat
import java.util.Date

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

        try {
            val sdf = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z")
            val mDate: Date = sdf.parse(item.pubDate)
            val timeInMilliseconds = mDate.time
            holder.binding.tvDate.text = timeInMilliseconds.getNewsFeedDateTime()
        } catch (e: Exception) {
            e.printStackTrace()
            holder.binding.tvDate.text = item.pubDate
        }

        holder.itemView.setOnClickListener {
            clickListener(item)
        }
    }

    override fun getItemCount(): Int {
        return newsFeeds.size
    }
}
