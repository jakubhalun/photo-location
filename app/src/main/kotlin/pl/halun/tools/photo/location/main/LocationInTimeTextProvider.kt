package pl.halun.tools.photo.location.main

import pl.halun.tools.photo.location.kmls.TravelPoint
import java.time.Instant

class LocationInTimeTextProvider {

    private var travel: List<TravelPoint>? = null
    private var time: Instant? = null

    fun textForTime(time: Instant): String {
        this.time = time
        return "jpeg"
    }

    fun textForLocations(): String {
        // TODO
        return "kml"
    }
}
