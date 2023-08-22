package pl.halun.tools.photo.location.main

import pl.halun.tools.photo.location.kmls.TravelPoint
import java.time.Instant

sealed class Result

data class InvalidResult(val message: String): Result()

data class LocationResult(
    val lastBefore: TravelPoint,
    val closestInTime: TravelPoint,
    val stopPoints: List<TravelPoint>,
    val photoTimeWithDuration: Instant
): Result()
