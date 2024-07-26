package cz.dcervenka.run.domain

data class AccumulatedExerciseMetrics(
    val heartRates: List<Int> = emptyList(),
    val steps: List<Int> = emptyList(),
)
