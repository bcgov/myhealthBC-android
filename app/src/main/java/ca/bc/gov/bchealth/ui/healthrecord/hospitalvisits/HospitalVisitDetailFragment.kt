package ca.bc.gov.bchealth.ui.healthrecord.hospitalvisits

import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.compose.theme.HealthGatewayTheme
import ca.bc.gov.bchealth.ui.BaseFragment
import ca.bc.gov.bchealth.ui.comment.CommentsSummary
import ca.bc.gov.bchealth.ui.comment.CommentsViewModel
import ca.bc.gov.bchealth.ui.custom.MyHealthToolbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HospitalVisitDetailFragment : BaseFragment(null) {
    private val args: HospitalVisitDetailFragmentArgs by navArgs()
    private val viewModel: HospitalVisitDetailViewModel by viewModels()
    private val commentsViewModel: CommentsViewModel by viewModels()

    @Composable
    override fun GetComposableLayout() {
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

        HealthGatewayTheme {
            Scaffold(
                topBar = {
                    MyHealthToolbar(
                        navigationAction = { findNavController().popBackStack() },
                        title = uiState.toolbarTitle
                    )
                },
                content = {
                    HospitalVisitDetailScreen(
                        onClickComments = ::onClickComments,
                        Modifier
                            .statusBarsPadding()
                            .navigationBarsPadding()
                            .padding(it),
                        viewModel,
                        commentsViewModel,
                        id = args.hospitalVisitId
                    )
                }
            )
        }
    }

    private fun onClickComments(commentsSummary: CommentsSummary) {
        findNavController().navigate(
            R.id.commentsFragment,
            bundleOf(
                "parentEntryId" to commentsSummary.parentEntryId,
                "recordType" to commentsSummary.entryTypeCode,
            )
        )
    }
}
