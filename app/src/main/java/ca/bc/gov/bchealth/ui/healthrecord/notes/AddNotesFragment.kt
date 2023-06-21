package ca.bc.gov.bchealth.ui.healthrecord.notes

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.BottomAppBar
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.fragment.findNavController
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.compose.BasePreview
import ca.bc.gov.bchealth.compose.MyHealthTheme
import ca.bc.gov.bchealth.compose.blue
import ca.bc.gov.bchealth.compose.lightGrey
import ca.bc.gov.bchealth.ui.BaseFragment
import ca.bc.gov.bchealth.ui.custom.MyHealthToolBar
import dagger.hilt.android.AndroidEntryPoint

/**
 * @author Pinakin Kansara
 */
@AndroidEntryPoint
class AddNotesFragment : BaseFragment(null) {

    @Composable
    override fun GetComposableLayout() {
        MyHealthTheme {
            Scaffold(
                topBar = {
                    MyHealthToolBar(
                        title = stringResource(id = R.string.add_note_title),
                        isCenterAligned = true,
                        navigationIcon = {
                            IconButton(onClick = { findNavController().popBackStack() }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_toolbar_back),
                                    tint = MaterialTheme.colors.primary,
                                    contentDescription = stringResource(
                                        id = R.string.back
                                    )
                                )
                            }
                        },
                        actions = {
                            IconButton(
                                modifier = Modifier.padding(end = 16.dp),
                                onClick = { findNavController().navigate(R.id.settingsFragment) }
                            ) {
                                Text(
                                    text = stringResource(id = R.string.add_note_create),
                                    style = MaterialTheme.typography.body2,
                                    color = blue
                                )
                            }
                        }
                    )
                },
                bottomBar = {
                    BottomAppBar(
                        modifier = Modifier.fillMaxWidth(),
                        backgroundColor = lightGrey,
                        contentPadding = PaddingValues(start = 24.dp, end = 24.dp)
                    ) {

                        IconButton(
                            onClick = { findNavController().navigate(R.id.settingsFragment) }
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_camera_menu),
                                tint = MaterialTheme.colors.primary,
                                contentDescription = stringResource(
                                    id = R.string.back
                                )
                            )
                        }
                        IconButton(
                            onClick = { findNavController().navigate(R.id.settingsFragment) }
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_photo_menu),
                                tint = MaterialTheme.colors.primary,
                                contentDescription = stringResource(
                                    id = R.string.back
                                )
                            )
                        }
                        IconButton(
                            onClick = { findNavController().navigate(R.id.settingsFragment) }
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_attach_menu),
                                tint = MaterialTheme.colors.primary,
                                contentDescription = stringResource(
                                    id = R.string.back
                                )
                            )
                        }
                    }
                },
                content = { it ->
                    AddNotesScreen(
                        modifier = Modifier
                            .statusBarsPadding()
                            .navigationBarsPadding()
                            .padding(it)
                    )
                },
                contentColor = contentColorFor(backgroundColor = MaterialTheme.colors.background)
            )
        }
    }

    @Composable
    @BasePreview
    private fun AddNotesScreenPreview() {
        GetComposableLayout()
    }
}
