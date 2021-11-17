package ca.bc.gov.bchealth.ui.newsfeed

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.analytics.AnalyticsAction
import ca.bc.gov.bchealth.analytics.SelfDescribingEvent
import ca.bc.gov.bchealth.databinding.FragmentNewsfeedBinding
import ca.bc.gov.bchealth.model.rss.Newsfeed
import ca.bc.gov.bchealth.utils.redirect
import ca.bc.gov.bchealth.utils.viewBindings
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.snowplowanalytics.snowplow.Snowplow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NewsfeedFragment : Fragment(R.layout.fragment_newsfeed) {

    private lateinit var newsfeedAdapter: NewsfeedAdapter

    private var newsFeeds: MutableList<Newsfeed> = mutableListOf()

    private val binding by viewBindings(FragmentNewsfeedBinding::bind)

    private val viewModel: NewsfeedViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        newsfeedAdapter = NewsfeedAdapter(newsFeeds) {
            it.link?.let { it1 ->
                requireActivity().redirect(it1)

                // Snowplow event
                Snowplow.getDefaultTracker()?.track(
                    SelfDescribingEvent.get(AnalyticsAction.NewsLinkSelected.value, it1)
                )
            }
        }

        binding.recItems.adapter = newsfeedAdapter

        binding.recItems
            .addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    LinearLayoutManager.VERTICAL
                )
            )

        binding.recItems.layoutManager = LinearLayoutManager(requireContext())

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.fetchNewsFeed(getString(R.string.url_news_feed))
            }
        }

        viewModel.newsfeedLiveData.observe(viewLifecycleOwner, {
            if (it.isNullOrEmpty()) {
                showError(
                    getString(R.string.error),
                    getString(R.string.error_message)
                )
            } else {
                newsfeedAdapter.newsFeeds = it
                newsfeedAdapter.notifyDataSetChanged()
                binding.progressBar.visibility = View.INVISIBLE
            }
        })
    }

    private fun showError(title: String, message: String) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(title)
            .setCancelable(false)
            .setMessage(message)
            .setPositiveButton(getString(android.R.string.ok)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}
