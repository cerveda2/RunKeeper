package cz.dcervenka.analytics.domain

interface AnalyticsRepository {
    suspend fun getAnalyticsValues(): AnalyticsValues
}