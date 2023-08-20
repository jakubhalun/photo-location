package pl.halun.tools.photo.location.main

import java.time.Instant

class LocationResultTextFormatter {
    fun prepareText(locationResult: LocationResult, timeWithDuration: Instant): String {


        return locationResult.toString()
    }
}
