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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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

@Composable
fun NotificationScreen() {
    LazyColumn(Modifier.padding(horizontal = 32.dp)) {
        items(
            listOf(
                "2021-Oct-12, 7:00 PM" to "You have a new Laboratory result.",
                "2021-Oct-12, 7:00 PM" to "The Health Gateway is currently intermittent delay, support is currently looking into this issue."
            )
        ) {
            Text(
                modifier = Modifier.padding(top = 16.dp),
                text = it.first,
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
                        text = it.second,
                        style = MyHealthTypography.body2,
                    )

                    Box(
                        modifier = Modifier
                            .size(minButtonSize)
                            .clickable {}
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

                Text(
                    modifier = Modifier
                        .padding(top = 16.dp, start = 16.dp, bottom = 16.dp)
                        .clickable { },
                    text = stringResource(id = R.string.notifications_view_details),
                    style = MyHealthTypography.h6.copy(textDecoration = TextDecoration.Underline),
                )
            }

            Spacer(modifier = Modifier.padding(8.dp))
        }
    }
}

@BasePreview
@Composable
private fun Preview() {
    MyHealthTheme {
        NotificationScreen()
    }
}
