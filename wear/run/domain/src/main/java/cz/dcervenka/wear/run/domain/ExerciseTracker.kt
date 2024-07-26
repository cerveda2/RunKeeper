package cz.dcervenka.wear.run.domain

import cz.dcervenka.core.domain.util.EmptyResult
import kotlinx.coroutines.flow.Flow

interface ExerciseTracker {
    val exerciseMetrics: Flow<ExerciseMetrics>
    suspend fun isRunningTrackingSupported(): Boolean
    suspend fun prepareExercise(): EmptyResult<ExerciseError>
    suspend fun startExercise(): EmptyResult<ExerciseError>
    suspend fun resumeExercise(): EmptyResult<ExerciseError>
    suspend fun pauseExercise(): EmptyResult<ExerciseError>
    suspend fun stopExercise(): EmptyResult<ExerciseError>
}