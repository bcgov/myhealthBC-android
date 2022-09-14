package ca.bc.gov.bchealth.ui.recommendations

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.databinding.FragmentRecommendationsBinding
import ca.bc.gov.bchealth.ui.BaseFragment
import ca.bc.gov.bchealth.utils.makeLinks
import ca.bc.gov.bchealth.utils.redirect
import ca.bc.gov.bchealth.utils.viewBindings
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecommendationsFragment : BaseFragment(R.layout.fragment_recommendations) {
    private val binding by viewBindings(FragmentRecommendationsBinding::bind)

    override fun setToolBar(appBarConfiguration: AppBarConfiguration) {
        with(binding.layoutToolbar.appbar) {
            stateListAnimator = null
            elevation = 0f
        }
        with(binding.layoutToolbar.topAppBar) {
            setNavigationIcon(R.drawable.ic_toolbar_back)
            setNavigationOnClickListener {
                findNavController().popBackStack()
            }
            title = getString(R.string.recommendations_home_title)
            inflateMenu(R.menu.settings_menu)
            setOnMenuItemClickListener { menu ->
                when (menu.itemId) {
                    R.id.menu_settings -> {
                        findNavController().navigate(R.id.profileFragment)
                    }
                }
                return@setOnMenuItemClickListener true
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        super.onViewCreated(view, savedInstanceState)
        tvDescription.makeLinks(
            getString(R.string.recommendations_description_clickable) to
                View.OnClickListener {
                    it.context.redirect(getString(R.string.url_immunize_bc))
                }
        )

        val list = listOf(
            Recommendation("Title 01", "Eligible", "Oct 1, 2022", false),
            Recommendation("Title 02", "Overdue", "Oct 2, 2022", false),
            Recommendation("Title 03", null, "Oct 3, 2022", false),
            Recommendation("Title 04", "Completed", "Oct 4, 2022", true),
            Recommendation("Title 05", "status5", "Oct 5, 2022", false),
            Recommendation("Title 06", "status6", "Oct 6, 2022", false),
            Recommendation("Title 07", "status7", "Oct 7, 2022", false),
            Recommendation("Title 08", "status8", "Oct 8, 2022", false),
            Recommendation("Title 09", "status9", "Oct 8, 2022", false),
            Recommendation("Title 10", "status10", "Oct 10, 2022", false),
        )

        val adapter = RecommendationAdapter()
        rvRecommendations.adapter = adapter

        adapter.submitList(list)
    }
}
