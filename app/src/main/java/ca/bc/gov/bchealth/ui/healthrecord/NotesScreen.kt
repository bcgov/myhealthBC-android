package ca.bc.gov.bchealth.ui.healthrecord

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import ca.bc.gov.bchealth.R
import ca.bc.gov.bchealth.compose.BasePreview
import ca.bc.gov.bchealth.compose.MyHealthTheme
import ca.bc.gov.bchealth.ui.component.EmptyStateUI
import ca.bc.gov.bchealth.ui.component.SearchBarUI

@Composable
fun NotesScreen(
    modifier: Modifier = Modifier,
    onAddNotesClicked: () -> Unit
) {
    NotesScreenContent(modifier, onAddNotesClicked)
}

@Composable
private fun NotesScreenContent(
    modifier: Modifier = Modifier,
    onAddNotesClicked: () -> Unit = {}
) {
    Box(modifier = modifier.fillMaxSize()) {

        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize()
        ) {
            SearchBarUI(modifier = Modifier.fillMaxWidth(), enableFilter = false)
            Spacer(modifier = Modifier.height(16.dp))
            EmptyStateUI(
                modifier,
                image = R.drawable.ic_empty_note,
                title = R.string.notes_empty_title,
                description = R.string.notes_empty_description
            )
        }
        FloatingActionButton(
            onClick = onAddNotesClicked,
            modifier = Modifier
                .padding(all = 16.dp)
                .align(alignment = Alignment.BottomEnd)
        ) {
            Icon(painterResource(id = R.drawable.ic_add_notes), "AddNote")
        }
    }
}

@Composable
@BasePreview
private fun NotesScreenPreview() {
    MyHealthTheme {
        NotesScreenContent()
    }
}
