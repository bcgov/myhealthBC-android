package ca.bc.gov.bchealth.ui.custom

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ca.bc.gov.bchealth.compose.MyHealthTheme

@Composable
fun MyHealthScaffold(
    title: String?,
    navigationAction: (() -> Unit),
    content: @Composable ColumnScope.() -> Unit
) = MyHealthTheme {
    Scaffold(topBar = {
        MyHealthToolbar(title = title.orEmpty(), navigationAction)
    }) { innerPadding ->
        Column(
            modifier = Modifier
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(innerPadding),
            content = content
        )
    }
}
