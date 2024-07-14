package cz.dcervenka.run.presentation.run_overview.mapper

import cz.dcervenka.core.domain.run.Run
import cz.dcervenka.core.presentation.ui.formatted
import cz.dcervenka.core.presentation.ui.toFormattedKm
import cz.dcervenka.core.presentation.ui.toFormattedKmh
import cz.dcervenka.core.presentation.ui.toFormattedMeters
import cz.dcervenka.core.presentation.ui.toFormattedPace
import cz.dcervenka.run.presentation.run_overview.model.RunUi
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun Run.toRunUi(): RunUi {
    val dateTimeInLocalTime = dateTimeUtc
        .withZoneSameInstant(ZoneId.systemDefault())
    val formattedDateTime = DateTimeFormatter
        .ofPattern("MMM dd, yyyy - hh:mma")
        .format(dateTimeInLocalTime)

    val distanceKm = distanceMeters / 1000.0

    return RunUi(
        id = id!!,
        duration = duration.formatted(),
        dateTime = formattedDateTime,
        distance = distanceKm.toFormattedKm(),
        avgSpeed = avgSpeedKmh.toFormattedKmh(),
        maxSpeed = maxSpeedKmh.toFormattedKmh(),
        pace = duration.toFormattedPace(distanceKm),
        totalElevation = totalElevationMeters.toFormattedMeters(),
        mapPictureUrl = mapPictureUrl
    )
}