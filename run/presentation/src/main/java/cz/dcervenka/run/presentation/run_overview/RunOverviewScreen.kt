@file:OptIn(ExperimentalMaterial3Api::class)

package cz.dcervenka.run.presentation.run_overview

import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cz.dcervenka.core.presentation.designsystem.AnalyticsIcon
import cz.dcervenka.core.presentation.designsystem.LogoIcon
import cz.dcervenka.core.presentation.designsystem.LogoutIcon
import cz.dcervenka.core.presentation.designsystem.RunIcon
import cz.dcervenka.core.presentation.designsystem.RunKeeperTheme
import cz.dcervenka.core.presentation.designsystem.components.RunKeeperFab
import cz.dcervenka.core.presentation.designsystem.components.RunKeeperScaffold
import cz.dcervenka.core.presentation.designsystem.components.RunKeeperToolbar
import cz.dcervenka.core.presentation.designsystem.components.util.DropDownItem
import cz.dcervenka.run.presentation.R
import org.koin.androidx.compose.koinViewModel

@Composable
fun RunOverviewScreenRoot(
    onStartRunClick: () -> Unit,
    viewModel: RunOverviewViewModel = koinViewModel(),
) {
    RunOverviewScreen(
        onAction = { action ->
            when (action) {
                RunOverviewAction.OnStartClick -> onStartRunClick()
                else -> Unit
            }
            viewModel.onAction(action)
        }
    )
}

@Composable
private fun RunOverviewScreen(
    onAction: (RunOverviewAction) -> Unit
) {
    val topAppBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
        state = topAppBarState
    )
    RunKeeperScaffold(
        topAppBar = {
            RunKeeperToolbar(
                showBackButton = false,
                title = stringResource(id = R.string.runkeeper),
                scrollBehavior = scrollBehavior,
                menuItems = listOf(
                    DropDownItem(
                        icon = AnalyticsIcon,
                        title = stringResource(id = R.string.analytics)
                    ),
                    DropDownItem(
                        icon = LogoutIcon,
                        title = stringResource(id = R.string.logout)
                    ),
                ),
                onMenuItemClick = { index ->
                    when (index) {
                        0 -> onAction(RunOverviewAction.OnAnalyticsClick)
                        1 -> onAction(RunOverviewAction.OnLogoutClick)
                    }
                },
                startContent = {
                    Icon(
                        imageVector = LogoIcon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(30.dp)
                    )
                }
            )
        },
        floatingActionButton = {
            RunKeeperFab(
                icon = RunIcon,
                onClick = {
                    onAction(RunOverviewAction.OnStartClick)
                }
            )
        }
    ) { padding ->

    }
}

@Preview
@Composable
private fun RunOverviewScreenPreview() {
    RunKeeperTheme {
        RunOverviewScreen(
            onAction = {}
        )
    }
}