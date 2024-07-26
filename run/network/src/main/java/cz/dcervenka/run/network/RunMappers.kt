package cz.dcervenka.run.network

import cz.dcervenka.core.domain.location.Location
import cz.dcervenka.core.domain.run.Run
import java.time.Instant
import java.time.ZoneId
import kotlin.time.Duration.Companion.milliseconds

fun RunDto.toRun(): Run {
    return Run(
        id = id,
        duration = durationMillis.milliseconds,
        dateTimeUtc = Instant.parse(dateTimeUtc)
            .atZone(ZoneId.of("UTC")),
        distanceMeters = distanceMeters,
        location = Location(lat, long),
        maxSpeedKmh = maxSpeedKmh,
        avgHeartRate = avgHeartRate,
        maxHeartRate = maxHeartRate,
        totalElevationMeters = totalElevationMeters,
        mapPictureUrl = mapPictureUrl,
        steps = 0,
    )
}

fun Run.toRunRequest(): RunRequest {
    return RunRequest(
        id = id!!,
        durationMillis = duration.inWholeMilliseconds,
        distanceMeters = distanceMeters,
        lat = location.lat,
        long = location.long,
        avgSpeedKmh = avgSpeedKmh,
        maxSpeedKmh = maxSpeedKmh,
        avgHeartRate = avgHeartRate,
        maxHeartRate = maxHeartRate,
        totalElevationMeters = totalElevationMeters,
        epochMillis = dateTimeUtc.toEpochSecond() * 1000L
    )
}