@file:OptIn(ExperimentalMaterial3Api::class)

package cz.dcervenka.run.presentation.active_run

import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cz.dcervenka.core.presentation.designsystem.RunKeeperTheme
import cz.dcervenka.core.presentation.designsystem.StartIcon
import cz.dcervenka.core.presentation.designsystem.StopIcon
import cz.dcervenka.core.presentation.designsystem.components.ActionButton
import cz.dcervenka.core.presentation.designsystem.components.OutlinedActionButton
import cz.dcervenka.core.presentation.designsystem.components.RunKeeperDialog
import cz.dcervenka.core.presentation.designsystem.components.RunKeeperFab
import cz.dcervenka.core.presentation.designsystem.components.RunKeeperScaffold
import cz.dcervenka.core.presentation.designsystem.components.RunKeeperToolbar
import cz.dcervenka.core.presentation.ui.ObserveAsEvents
import cz.dcervenka.run.presentation.R
import cz.dcervenka.run.presentation.active_run.components.RunDataCard
import cz.dcervenka.run.presentation.active_run.maps.TrackerMap
import cz.dcervenka.core.notification.ActiveRunService
import cz.dcervenka.run.presentation.util.hasLocationPermission
import cz.dcervenka.run.presentation.util.hasNotificationPermission
import cz.dcervenka.run.presentation.util.shouldShowLocationPermissionRationale
import cz.dcervenka.run.presentation.util.shouldShowNotificationPermissionRationale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.compose.koinViewModel
import java.io.ByteArrayOutputStream

@Composable
fun ActiveRunScreenRoot(
    onFinish: () -> Unit,
    onBackClick: () -> Unit,
    onServiceToggle: (isServiceRunning: Boolean) -> Unit,
    viewModel: ActiveRunViewModel = koinViewModel(),
) {
    val context = LocalContext.current
    ObserveAsEvents(flow = viewModel.events) { event ->
        when (event) {
            is ActiveRunEvent.Error -> {
                Toast.makeText(
                    context,
                    event.error.asString(context),
                    Toast.LENGTH_LONG
                ).show()
            }

            ActiveRunEvent.RunSaved -> onFinish()
        }
    }
    ActiveRunScreen(
        state = viewModel.state,
        onServiceToggle = onServiceToggle,
        onAction = { action ->
            when (action) {
                ActiveRunAction.OnBackClick -> {
                    if (!viewModel.state.hasStartedRunning) {
                        onBackClick()
                    }
                }

                else -> Unit
            }
            viewModel.onAction(action)
        }
    )
}

@Composable
private fun ActiveRunScreen(
    state: ActiveRunState,
    onServiceToggle: (isServiceRunning: Boolean) -> Unit,
    onAction: (ActiveRunAction) -> Unit
) {
    val context = LocalContext.current
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val hasCoarsePermission =
            permissions[android.Manifest.permission.ACCESS_COARSE_LOCATION] == true
        val hasFinePermission =
            permissions[android.Manifest.permission.ACCESS_FINE_LOCATION] == true
        val hasNotificationPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions[android.Manifest.permission.POST_NOTIFICATIONS] == true
        } else true

        val activity = context as ComponentActivity
        val showLocationRationale = activity.shouldShowLocationPermissionRationale()
        val showNotificationRationale = activity.shouldShowNotificationPermissionRationale()

        onAction(
            ActiveRunAction.SubmitLocationPermissionInfo(
                acceptedLocationPermission = hasCoarsePermission && hasFinePermission,
                showLocationRationale = showLocationRationale
            )
        )
        onAction(
            ActiveRunAction.SubmitNotificationPermissionInfo(
                acceptedNotificationPermission = hasNotificationPermission,
                showNotificationRationale = showNotificationRationale
            )
        )
    }

    LaunchedEffect(key1 = true) {
        val activity = context as ComponentActivity
        val showLocationRationale = activity.shouldShowLocationPermissionRationale()
        val showNotificationRationale = activity.shouldShowNotificationPermissionRationale()

        onAction(
            ActiveRunAction.SubmitLocationPermissionInfo(
                acceptedLocationPermission = context.hasLocationPermission(),
                showLocationRationale = showLocationRationale
            )
        )
        onAction(
            ActiveRunAction.SubmitNotificationPermissionInfo(
                acceptedNotificationPermission = context.hasNotificationPermission(),
                showNotificationRationale = showNotificationRationale
            )
        )

        if (!showLocationRationale && !showNotificationRationale) {
            permissionLauncher.requestRunKeeperPermissions(context)
        }
    }

    val isServiceActive by ActiveRunService.isServiceActive.collectAsStateWithLifecycle()
    LaunchedEffect(state.shouldTrack, isServiceActive) {
        if (context.hasLocationPermission() && state.shouldTrack && !isServiceActive) {
            onServiceToggle(true)
        }
    }

    LaunchedEffect(state.isRunFinished) {
        if (state.isRunFinished) {
            onServiceToggle(false)
        }
    }

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
            val coroutineScope = rememberCoroutineScope()
            TrackerMap(
                isRunFinished = state.isRunFinished,
                currentLocation = state.currentLocation,
                locations = state.runData.locations,
                onSnapshot = { bmp ->
                    coroutineScope.launch {
                        val byteArray = withContext(Dispatchers.IO) {
                            val stream = ByteArrayOutputStream()
                            stream.use {
                                bmp.compress(
                                    Bitmap.CompressFormat.JPEG,
                                    80,
                                    it
                                )
                            }
                            stream.toByteArray()
                        }
                        onAction(ActiveRunAction.OnRunProcessed(byteArray))
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
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

    if (!state.shouldTrack && state.hasStartedRunning) {
        RunKeeperDialog(
            title = stringResource(id = R.string.run_paused),
            onDismiss = { onAction(ActiveRunAction.OnResumeRunClick) },
            description = stringResource(id = R.string.resume_or_finish_run),
            primaryButton = {
                ActionButton(
                    text = stringResource(id = R.string.resume),
                    isLoading = false,
                    onClick = { onAction(ActiveRunAction.OnResumeRunClick) },
                    modifier = Modifier.weight(1f),
                )
            },
            secondaryButton = {
                OutlinedActionButton(
                    text = stringResource(id = R.string.finish),
                    isLoading = state.isSavingRun,
                    onClick = {
                        onAction(ActiveRunAction.OnFinishRunClick)
                    },
                    modifier = Modifier.weight(1f),
                )
            }
        )
    }

    if (state.showLocationRationale || state.showNotificationRationale) {
        RunKeeperDialog(
            title = stringResource(id = R.string.permission_required),
            onDismiss = { /* Normal dismissing not allowed for permissions */ },
            description = when {
                state.showLocationRationale && state.showNotificationRationale -> {
                    stringResource(id = R.string.location_notification_rationale)
                }

                state.showLocationRationale -> {
                    stringResource(id = R.string.location_rationale)
                }

                else -> {
                    stringResource(id = R.string.notification_rationale)
                }
            },
            primaryButton = {
                OutlinedActionButton(
                    text = stringResource(id = R.string.okay),
                    isLoading = false,
                    onClick = {
                        onAction(ActiveRunAction.DismissRationaleDialog)
                        permissionLauncher.requestRunKeeperPermissions(context)
                    }
                )
            })
    }
}

private fun ActivityResultLauncher<Array<String>>.requestRunKeeperPermissions(
    context: Context
) {
    val hasLocationPermission = context.hasLocationPermission()
    val hasNotificationPermission = context.hasNotificationPermission()

    val locationPermissions = arrayOf(
        android.Manifest.permission.ACCESS_COARSE_LOCATION,
        android.Manifest.permission.ACCESS_FINE_LOCATION
    )
    val notificationPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(android.Manifest.permission.POST_NOTIFICATIONS)
    } else {
        emptyArray()
    }

    when {
        !hasLocationPermission && !hasNotificationPermission -> {
            launch(locationPermissions + notificationPermissions)
        }

        !hasLocationPermission -> launch(locationPermissions)
        !hasNotificationPermission -> launch(notificationPermissions)
    }
}

@Preview
@Composable
private fun ActiveRunScreenPreview() {
    RunKeeperTheme {
        ActiveRunScreen(
            state = ActiveRunState(),
            onServiceToggle = {},
            onAction = {}
        )
    }
}