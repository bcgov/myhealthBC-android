package ca.bc.gov.bchealth.ui.custom

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import ca.bc.gov.bchealth.compose.MyHealthTheme

@Composable
fun MyHealthScaffold(
    title: String?,
    isLoading: Boolean = false,
    navigationAction: (() -> Unit),
    content: @Composable BoxScope.() -> Unit
) = MyHealthTheme {
    Scaffold(topBar = {
        MyHealthToolbar(title = title, Modifier, navigationAction)
    }) { innerPadding ->
        Box(
            modifier = Modifier
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(innerPadding)
                .fillMaxSize(),
            content = {
                content()
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                    )
                }
            }
        )
    }
}
