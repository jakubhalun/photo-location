package pl.halun.tools.photo.location.main

import pl.halun.tools.photo.location.kmls.TravelPoint

data class LocationResult(val lastBefore: TravelPoint, val closestInTime: TravelPoint, var otherClose: List<TravelPoint>)
