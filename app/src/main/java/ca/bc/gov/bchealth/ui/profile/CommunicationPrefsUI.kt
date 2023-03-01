package ca.bc.gov.bchealth.ui.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.compose.BasePreview
import ca.bc.gov.bchealth.compose.MyHealthTypography
import ca.bc.gov.bchealth.compose.green
import ca.bc.gov.bchealth.compose.red
import ca.bc.gov.bchealth.ui.custom.DecorativeImage
import ca.bc.gov.bchealth.ui.custom.MyHealthClickableText

@Composable
fun CommunicationPreferences(
    email: String?,
    verified: Boolean,
    phone: String?,
    onClick: () -> Unit
) {
    Column(Modifier.padding(top = 20.dp, start = 32.dp, end = 32.dp, bottom = 55.dp)) {
        Text(
            text = stringResource(id = R.string.profile_communication_prefs_title),
            style = MyHealthTypography.h3
        )

        Text(
            modifier = Modifier.padding(top = 4.dp),
            text = stringResource(id = R.string.profile_communication_prefs_body_1),
            style = MyHealthTypography.caption,
        )

        MyHealthClickableText(
            modifier = Modifier.padding(top = 4.dp),
            fullText = stringResource(id = R.string.profile_communication_prefs_body_2),
            clickableText = stringResource(id = R.string.profile_communication_prefs_click),
            style = MyHealthTypography.caption,
            action = onClick
        )

        Text(
            modifier = Modifier.padding(top = 20.dp),
            text = stringResource(id = R.string.profile_communication_prefs_email),
            style = MyHealthTypography.body1
        )

        Spacer(modifier = Modifier.size(4.dp))

        EmailField(email, verified)

        Text(
            modifier = Modifier.padding(top = 20.dp),
            text = stringResource(id = R.string.profile_communication_prefs_phone),
            style = MyHealthTypography.body1
        )

        Spacer(modifier = Modifier.size(4.dp))

        PhoneField(phone = phone)
    }
}

@Composable
private fun EmailField(email: String?, verified: Boolean) {
    if (email.isNullOrBlank()) {
        Text(
            text = stringResource(id = R.string.profile_communication_prefs_email_empty),
            style = MyHealthTypography.caption.copy(fontSize = 13.sp)
        )
    } else {
        Column {
            Text(
                text = email,
                style = MyHealthTypography.body2
            )

            Spacer(modifier = Modifier.size(4.dp))

            VerifiedBadge(verified = verified)
        }
    }
}

@Composable
private fun VerifiedBadge(verified: Boolean, modifier: Modifier = Modifier) {
    val text: String
    val style: TextStyle
    val resourceId: Int

    if (verified) {
        text = stringResource(id = R.string.profile_communication_prefs_email_verified)
        style = MyHealthTypography.overline.copy(color = green)
        resourceId = R.drawable.ic_verified
    } else {
        text = stringResource(id = R.string.profile_communication_prefs_email_not_verified)
        style = MyHealthTypography.overline.copy(color = red)
        resourceId = R.drawable.ic_not_verified
    }

    Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier) {
        DecorativeImage(resourceId = resourceId)
        Spacer(Modifier.padding(2.dp))
        Text(text = text, style = style)
    }
}

@Composable
private fun PhoneField(phone: String?) {
    val text: String
    val style: TextStyle

    if (phone.isNullOrBlank()) {
        text = stringResource(id = R.string.profile_communication_prefs_phone_empty)
        style = MyHealthTypography.caption.copy(fontSize = 13.sp)
    } else {
        text = phone
        style = MyHealthTypography.body2
    }

    Text(text = text, style = style)
}

@BasePreview
@Composable
private fun PreviewVerifiedBadge() {
    Column {
        VerifiedBadge(verified = true)
        VerifiedBadge(verified = false)
    }
}

@BasePreview
@Composable
private fun PreviewEmailField() {
    Column {
        EmailField("email@ca.ey.com", true)
        EmailField("email@ca.ey.com", false)
        EmailField("really-long-email-address-relly-long-email-address@ca.ey.com", true)
        EmailField("really-long-email-address-relly-long-email-address@ca.ey.com", false)
        EmailField(null, false)
    }
}

@BasePreview
@Composable
private fun PreviewPhoneField() {
    Column {
        PhoneField(null)
        PhoneField("")
        PhoneField(" ")
        PhoneField("(123) 456 7890")
    }
}

@BasePreview
@Composable
private fun PreviewCommunicationPreferences() {
    Column {
        CommunicationPreferences(
            "email-address@ca.ey.com",
            true,
            ""
        ) {}
    }
}
