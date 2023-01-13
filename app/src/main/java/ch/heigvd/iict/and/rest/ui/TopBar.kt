package ch.heigvd.iict.and.rest.ui

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import ch.heigvd.iict.and.rest.R
import ch.heigvd.iict.and.rest.navigation.AppScreens

@Composable
fun TopBar(
    currentScreen: AppScreens,
    canNavigateBack : Boolean,
    onNavigateBack : () -> Unit = {},
    actionList : @Composable RowScope.() -> Unit = {}
) {
    TopAppBar(
        title = { Text(text = stringResource(currentScreen.title)) },
        actions = actionList,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_button)
                    )
                }
            }
        }
    )
}