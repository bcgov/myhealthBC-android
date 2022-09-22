package ca.bc.gov.bchealth.ui.home.banner

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.AppBarConfiguration
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentBannerDetailBinding
import ca.bc.gov.bchealth.ui.BaseFragment
import ca.bc.gov.bchealth.utils.fromHtml
import ca.bc.gov.bchealth.utils.viewBindings
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BannerDetailFragment : BaseFragment(R.layout.fragment_banner_detail) {
    private val binding by viewBindings(FragmentBannerDetailBinding::bind)
    private val args: BannerDetailFragmentArgs by navArgs()

    override fun setToolBar(appBarConfiguration: AppBarConfiguration) {
        with(binding.layoutToolbar.topAppBar) {
            setNavigationIcon(R.drawable.ic_toolbar_back)
            setNavigationOnClickListener { findNavController().popBackStack() }
            title = getString(R.string.home_banner_toolbar_title)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val title = args.title
        val date = args.date
        val body = args.body

        binding.apply {
            tvTitle.text = title
            tvDate.text = date
            tvBody.text = body.fromHtml()
        }
    }
}