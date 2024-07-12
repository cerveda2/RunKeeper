@file:OptIn(ExperimentalMaterial3Api::class)

package cz.dcervenka.run.presentation.active_run

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cz.dcervenka.core.presentation.designsystem.RunKeeperTheme
import cz.dcervenka.core.presentation.designsystem.StartIcon
import cz.dcervenka.core.presentation.designsystem.StopIcon
import cz.dcervenka.core.presentation.designsystem.components.RunKeeperFab
import cz.dcervenka.core.presentation.designsystem.components.RunKeeperScaffold
import cz.dcervenka.core.presentation.designsystem.components.RunKeeperToolbar
import cz.dcervenka.run.presentation.R
import cz.dcervenka.run.presentation.active_run.components.RunDataCard
import org.koin.androidx.compose.koinViewModel

@Composable
fun ActiveRunScreenRoot(
    viewModel: ActiveRunViewModel = koinViewModel(),
) {
    ActiveRunScreen(
        state = viewModel.state,
        onAction = viewModel::onAction
    )
}

@Composable
private fun ActiveRunScreen(
    state: ActiveRunState,
    onAction: (ActiveRunAction) -> Unit
) {
    RunKeeperScaffold(
        withGradient = false,
        topAppBar = {
            RunKeeperToolbar(
                showBackButton = true,
                title = stringResource(id = R.string.active_run),
                onBackClick = { onAction(ActiveRunAction.OnBackClick) },
            )
        },
        floatingActionButton = {
            RunKeeperFab(
                icon = if (state.shouldTrack) StopIcon else StartIcon,
                onClick = { onAction(ActiveRunAction.OnToggleRunClick) },
                iconSize = 20.dp,
                contentDescription = if (state.shouldTrack) {
                    stringResource(id = R.string.pause_run)
                } else {
                    stringResource(id = R.string.start_run)
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
        ) {
            RunDataCard(
                elapsedTime = state.elapsedTime,
                runData = state.runData,
                modifier = Modifier
                    .padding(16.dp)
                    .padding(padding)
                    .fillMaxWidth()
            )
        }
    }
}

@Preview
@Composable
private fun ActiveRunScreenPreview() {
    RunKeeperTheme {
        ActiveRunScreen(
            state = ActiveRunState(),
            onAction = {}
        )
    }
}