package cz.dcervenka.analytics.presentation

sealed interface AnalyticsAction {
    data object OnBackClick : AnalyticsAction
}