package ca.bc.gov.bchealth.ui.notification

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.compose.BasePreview
import ca.bc.gov.bchealth.compose.MyHealthTheme
import ca.bc.gov.bchealth.compose.MyHealthTypography
import ca.bc.gov.bchealth.compose.grey
import ca.bc.gov.bchealth.compose.greyBg
import ca.bc.gov.bchealth.compose.minButtonSize
import ca.bc.gov.common.model.notification.NotificationActionTypeDto

@Composable
fun NotificationScreen(
    uiState: NotificationViewModel.NotificationsUIState,
    onClickDelete: (String) -> Unit,
    onClickAction: (NotificationViewModel.NotificationItem) -> Unit
) {

    Box {
        LazyColumn(Modifier.padding(horizontal = 32.dp)) {
            items(uiState.list) {
                Text(
                    modifier = Modifier.padding(top = 16.dp),
                    text = it.date,
                    style = MyHealthTypography.caption.copy(color = grey),
                )

                Column(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(greyBg)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            modifier = Modifier
                                .weight(1f)
                                .padding(top = 16.dp, start = 16.dp),
                            text = it.content,
                            style = MyHealthTypography.body2,
                        )

                        Box(
                            modifier = Modifier
                                .size(minButtonSize)
                                .clickable {
                                    if (uiState.loading.not()) {
                                        onClickDelete.invoke(it.notificationId)
                                    }
                                }
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_close),
                                contentScale = ContentScale.Inside,
                                modifier = Modifier
                                    .width(14.dp)
                                    .height(14.dp)
                                    .align(Center),
                                contentDescription = stringResource(id = R.string.notifications_remove)
                            )
                        }
                    }

                    ActionText(it, onClickAction)
                }

                Spacer(modifier = Modifier.padding(8.dp))
            }
        }
        if (uiState.loading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Center),
            )
        }
    }
}

@Composable
private fun ActionText(
    notificationItem: NotificationViewModel.NotificationItem,
    onClickAction: (NotificationViewModel.NotificationItem) -> Unit
) {
    if (notificationItem.actionType == NotificationActionTypeDto.NONE) {
        Spacer(modifier = Modifier.size(16.dp))
    } else {
        Box(
            modifier = Modifier
                .clickable { onClickAction.invoke(notificationItem) }
                .wrapContentWidth()
                .wrapContentHeight()
                .padding(16.dp)

        ) {
            Text(
                text = stringResource(
                    id = when (notificationItem.actionType) {
                        NotificationActionTypeDto.EXTERNAL -> R.string.notifications_more_info
                        NotificationActionTypeDto.INTERNAL -> R.string.notifications_view_details
                        else -> throw RuntimeException("Type ${notificationItem.actionType} not mapped")
                    }
                ),
                style = MyHealthTypography.h6.copy(textDecoration = TextDecoration.Underline),
            )
        }
    }
}

@BasePreview
@Composable
private fun Preview() {
    val sample = NotificationViewModel.NotificationItem(
        notificationId = "",
        content = "You have a new COVID19Laboratory result",
        actionUrl = "",
        actionType = NotificationActionTypeDto.INTERNAL,
        date = "2023-May-28 08:20pm",
    )

    MyHealthTheme {
        NotificationScreen(
            NotificationViewModel.NotificationsUIState(
                list = listOf(
                    sample,
                    sample.copy(
                        actionType = NotificationActionTypeDto.EXTERNAL,
                        content = "The Health Gateway is currently intermittent delay, support is currently looking into this issue."
                    ),
                    sample.copy(
                        actionType = NotificationActionTypeDto.NONE,
                    ),
                )
            ),
            {}, {}
        )
    }
}
