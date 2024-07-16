package cz.dcervenka.wear.run.domain

import cz.dcervenka.core.domain.util.Error

enum class ExerciseError: Error {
    TRACKING_NOT_SUPPORTED,
    ONGOING_OWN_EXERCISE,
    ONGOING_OTHER_EXERCISE,
    EXERCISE_ALREADY_ENDED,
    UNKNOWN
}