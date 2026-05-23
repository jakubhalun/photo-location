package pl.halun.tools.photo.location

import pl.halun.tools.photo.location.jpegs.JpegReader
import pl.halun.tools.photo.location.kmls.KmlReader
import pl.halun.tools.photo.location.main.ClosestLocationFinder
import pl.halun.tools.photo.location.main.LocationInTimeProvider

class Configuration {

    fun locationInTimeTextProvider(): LocationInTimeProvider =
        LocationInTimeProvider(
            ClosestLocationFinder()
        )

    fun jpegReader(): JpegReader = JpegReader()

    fun kmlReader(): KmlReader = KmlReader()
}
