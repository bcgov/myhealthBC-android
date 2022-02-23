package ca.bc.gov.bchealth.ui.newsfeed

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentNewsfeedBinding
import ca.bc.gov.bchealth.model.rss.Newsfeed
import ca.bc.gov.bchealth.utils.AlertDialogHelper
import ca.bc.gov.bchealth.utils.redirect
import ca.bc.gov.bchealth.utils.viewBindings
import ca.bc.gov.bchealth.viewmodel.AnalyticsFeatureViewModel
import ca.bc.gov.common.model.analytics.AnalyticsAction
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NewsfeedFragment : Fragment(R.layout.fragment_newsfeed) {

    private lateinit var newsfeedAdapter: NewsfeedAdapter
    private var newsFeeds: MutableList<Newsfeed> = mutableListOf()
    private val binding by viewBindings(FragmentNewsfeedBinding::bind)
    private val viewModel: NewsfeedViewModel by viewModels()
    private val analyticsFeatureViewModel: AnalyticsFeatureViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolBar()

        newsfeedAdapter = NewsfeedAdapter(newsFeeds) {
            it.link?.let { it1 ->
                requireActivity().redirect(it1)
                // Snowplow event
                analyticsFeatureViewModel.track(AnalyticsAction.NEWS_FEED_SELECTED, it1)
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
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.fetchNewsFeed(getString(R.string.url_news_feed))
            }
        }

        viewModel.newsfeedLiveData.observe(viewLifecycleOwner) {
            if (it.isNullOrEmpty()) {
                AlertDialogHelper.showAlertDialog(
                    context = requireContext(),
                    title = getString(R.string.error),
                    msg = getString(R.string.error_message),
                    positiveBtnMsg = getString(R.string.btn_ok)
                )
            } else {
                newsfeedAdapter.newsFeeds = it
                newsfeedAdapter.notifyDataSetChanged()
                binding.progressBar.visibility = View.INVISIBLE
            }
        }
    }

    private fun setupToolBar() {
        binding.toolbar.ivRightOption.apply {
            visibility = View.VISIBLE
            setOnClickListener {
                findNavController().navigate(R.id.profileFragment)
            }
        }
    }
}
