package ca.bc.gov.bchealth.compose.component.menu

import androidx.annotation.DrawableRes

sealed interface TopAppBarActionItem {
    val title: String
    val onClick: () -> Unit

    sealed interface IconActionItem : TopAppBarActionItem {
        @get:DrawableRes val icon: Int
        val contentDescription: String

        data class AlwaysShown(
            override val onClick: () -> Unit,
            override val title: String,
            override val icon: Int,
            override val contentDescription: String
        ) : IconActionItem

        data class ShowIfRoom(
            override val onClick: () -> Unit,
            override val title: String,
            override val icon: Int,
            override val contentDescription: String
        ) : IconActionItem
    }
    data class NeverShown(
        override val title: String,
        override val onClick: () -> Unit,
    ) : TopAppBarActionItem
}
