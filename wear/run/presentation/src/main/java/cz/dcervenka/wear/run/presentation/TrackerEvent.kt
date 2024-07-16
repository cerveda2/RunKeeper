package cz.dcervenka.wear.run.presentation

sealed interface TrackerEvent {
    data object RunFinished: TrackerEvent
}