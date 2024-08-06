package cz.dcervenka.run.network

data class RunDto(
    val id: String = "",
    val dateTimeUtc: String = "",
    val durationMillis: Long = 0L,
    val distanceMeters: Int = 0,
    val lat: Double = 0.0,
    val long: Double = 0.0,
    val avgSpeedKmh: Double = 0.0,
    val maxSpeedKmh: Double = 0.0,
    val totalElevationMeters: Int = 0,
    val mapPictureUrl: String? = null
)
