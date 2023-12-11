package ca.bc.gov.bchealth.ui.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.Visibility
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.compose.MultiDevicePreview
import ca.bc.gov.bchealth.compose.component.m3.HGButton
import ca.bc.gov.bchealth.compose.component.m3.HGTextButton
import ca.bc.gov.bchealth.compose.theme.m3.HealthGatewayTheme
import kotlinx.coroutines.launch

/**
 * @author pinakin.kansara
 * Created 2023-10-12 at 3:22 p.m.
 */
private const val PAGER_ID = "pager_id"
private const val PAGER_INDICATOR_ID = "pager_indicator_id"
private const val BTN_NEXT_ID = "btn_next_id"
private const val BTN_SKIP_INTRO_ID = "btn_skip_intro_id"

@Composable
fun OnBoardingScreen(
    onGetStartedClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: OnBoardingViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    OnBoardingScreenContent(onGetStartedClick, modifier, uiState)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun OnBoardingScreenContent(
    onGetStartedClick: () -> Unit,
    modifier: Modifier = Modifier,
    uiState: OnBoardingUIState
) {
    val pageCount = uiState.pageCount
    val pagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope()

    BoxWithConstraints {
        val constraints = when {
            maxWidth < 600.dp -> compactConstraint(uiState.isExistingUser)
            maxWidth < 840.dp -> mediumConstraint(uiState.isExistingUser)
            else -> largeConstraint(uiState.isExistingUser)
        }
        ConstraintLayout(
            modifier = modifier.fillMaxSize(),
            constraintSet = constraints
        ) {
            HorizontalPager(
                pageCount = pageCount,
                state = pagerState,
                modifier = Modifier.layoutId(PAGER_ID)
            ) {
                OnBoardingSliderUI(
                    onBoardingSliderItem = uiState.onBoardingSliderItems[it],
                    isExistingUser = uiState.isExistingUser
                )
            }

            AnimatedVisibility(
                modifier = Modifier.layoutId(PAGER_INDICATOR_ID),
                visible = !uiState.isExistingUser
            ) {
                OnBoardingPagerIndicatorUI(
                    pageCount = pageCount,
                    currentPage = pagerState.currentPage,
                    targetPage = pagerState.targetPage,
                    currentPageOffsetFraction = pagerState.currentPageOffsetFraction
                )
            }

            val isLastPage = (pageCount - 1) == pagerState.currentPage
            HGButton(
                onClick = {
                    if (isLastPage) {
                        onGetStartedClick()
                    } else {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    }
                },
                modifier = Modifier.layoutId(BTN_NEXT_ID),
                text = stringResource(
                    id = if (isLastPage) {
                        R.string.get_started
                    } else {
                        R.string.next
                    }
                )
            )
            HGTextButton(
                onClick = onGetStartedClick,
                modifier = Modifier.layoutId(BTN_SKIP_INTRO_ID),
                text = stringResource(id = R.string.skip),
                enabled = !isLastPage
            )
        }
    }
}

private fun compactConstraint(isExistingUser: Boolean): ConstraintSet {
    return ConstraintSet {
        val pagerId = createRefFor(PAGER_ID)
        val btnNextId = createRefFor(BTN_NEXT_ID)
        val btnSkipIntroId = createRefFor(BTN_SKIP_INTRO_ID)
        val pagerIndicatorId = createRefFor(PAGER_INDICATOR_ID)
        val bottomGuideline = createGuidelineFromBottom(0.4f)

        constrain(pagerId) {
            start.linkTo(parent.start)
            top.linkTo(parent.top)
            end.linkTo(parent.end)
            bottom.linkTo(bottomGuideline)
            height = Dimension.preferredWrapContent
        }

        constrain(pagerIndicatorId) {
            start.linkTo(parent.start)
            top.linkTo(bottomGuideline, 64.dp)
            end.linkTo(parent.end)
        }

        constrain(btnNextId) {
            start.linkTo(parent.start)
            top.linkTo(pagerIndicatorId.bottom, 32.dp)
            end.linkTo(parent.end)
        }

        constrain(btnSkipIntroId) {
            start.linkTo(btnNextId.start)
            top.linkTo(btnNextId.bottom, 16.dp)
            end.linkTo(btnNextId.end)
            visibility = if (isExistingUser) {
                Visibility.Gone
            } else { Visibility.Visible }
        }
    }
}

private fun mediumConstraint(isExistingUser: Boolean): ConstraintSet {
    return ConstraintSet {
        val pagerId = createRefFor(PAGER_ID)
        val btnNextId = createRefFor(BTN_NEXT_ID)
        val btnSkipIntroId = createRefFor(BTN_SKIP_INTRO_ID)
        val pagerIndicatorId = createRefFor(PAGER_INDICATOR_ID)
        val bottomGuideline = createGuidelineFromBottom(0.5f)
        constrain(pagerId) {
            start.linkTo(parent.start)
            top.linkTo(parent.top)
            end.linkTo(parent.end)
            bottom.linkTo(bottomGuideline)
            height = Dimension.preferredWrapContent
        }

        constrain(pagerIndicatorId) {
            start.linkTo(parent.start)
            top.linkTo(bottomGuideline, 32.dp)
            end.linkTo(parent.end)
        }

        constrain(btnNextId) {
            start.linkTo(parent.start)
            top.linkTo(pagerIndicatorId.bottom, 32.dp)
            end.linkTo(parent.end)
        }

        constrain(btnSkipIntroId) {
            start.linkTo(btnNextId.start)
            top.linkTo(btnNextId.bottom, 16.dp)
            end.linkTo(btnNextId.end)
            visibility = if (isExistingUser) {
                Visibility.Gone
            } else { Visibility.Visible }
        }
    }
}

private fun largeConstraint(isExistingUser: Boolean): ConstraintSet {
    return ConstraintSet {
        val pagerId = createRefFor(PAGER_ID)
        val btnNextId = createRefFor(BTN_NEXT_ID)
        val btnSkipIntroId = createRefFor(BTN_SKIP_INTRO_ID)
        val pagerIndicatorId = createRefFor(PAGER_INDICATOR_ID)
        val bottomGuideline = createGuidelineFromBottom(0.5f)

        constrain(pagerId) {
            start.linkTo(parent.start)
            top.linkTo(parent.top)
            end.linkTo(parent.end)
            bottom.linkTo(bottomGuideline)
            height = Dimension.preferredWrapContent
        }

        constrain(pagerIndicatorId) {
            start.linkTo(parent.start)
            top.linkTo(bottomGuideline, 32.dp)
            end.linkTo(parent.end)
        }
        val startGuideline = createGuidelineFromStart(0.30f)
        val endGuideline = createGuidelineFromEnd(0.30f)
        val chain = createHorizontalChain(
            btnSkipIntroId,
            btnNextId,
            chainStyle = ChainStyle.SpreadInside
        )

        constrain(chain) {
            start.linkTo(startGuideline)
            end.linkTo(endGuideline)
        }
        constrain(btnSkipIntroId) {
            top.linkTo(pagerIndicatorId.bottom, 64.dp)
            end.linkTo(btnNextId.start)
            visibility = if (isExistingUser) {
                Visibility.Gone
            } else {
                Visibility.Visible
            }
        }
        constrain(btnNextId) {
            start.linkTo(btnSkipIntroId.end)
            top.linkTo(btnSkipIntroId.top)
            bottom.linkTo(btnSkipIntroId.bottom)
        }
    }
}

@MultiDevicePreview
@Composable
private fun OnBoardingScreenPreview(
    @PreviewParameter(OnBoardingUIStateProvider::class) uiState: OnBoardingUIState
) {
    HealthGatewayTheme {
        OnBoardingScreenContent(onGetStartedClick = { /*TODO*/ }, uiState = uiState)
    }
}

internal class OnBoardingUIStateProvider : PreviewParameterProvider<OnBoardingUIState> {

    override val values: Sequence<OnBoardingUIState> = sequenceOf(
        OnBoardingUIState(
            pageCount = onBoardingList().size,
            onBoardingSliderItems = onBoardingList()
        )
    )

    private fun onBoardingList() = listOf(
        OnBoardingSliderItem(
            titleResId = R.string.onboarding_health_records_title,
            descriptionResId = R.string.onboarding_health_records_desc,
            iconResId = R.drawable.ic_onboarding_health_records
        ),
        OnBoardingSliderItem(
            titleResId = R.string.onboarding_dependents_title,
            descriptionResId = R.string.onboarding_dependents_desc,
            iconResId = R.drawable.ic_onboarding_dependent
        ),
        OnBoardingSliderItem(
            titleResId = R.string.onboarding_health_passes_title,
            descriptionResId = R.string.onboarding_health_passes_desc,
            iconResId = R.drawable.ic_onboarding_health_passes
        ),
        OnBoardingSliderItem(
            titleResId = R.string.onboarding_services_title,
            descriptionResId = R.string.onboarding_services_desc,
            iconResId = R.drawable.ic_onboarding_services
        )
    )
}
