package pl.halun.tools.photo.location

import pl.halun.tools.photo.location.jpegs.JpegReader
import pl.halun.tools.photo.location.kmls.KmlReader
import pl.halun.tools.photo.location.main.ClosestLocationFinder
import pl.halun.tools.photo.location.main.LocationInTimeTextProvider
import pl.halun.tools.photo.location.main.LocationResultTextFormatter

class Configuration {

    fun locationInTimeTextProvider(): LocationInTimeTextProvider =
        LocationInTimeTextProvider(
            ClosestLocationFinder(),
            LocationResultTextFormatter()
        )

    fun jpegReader(): JpegReader = JpegReader()

    fun kmlReader(): KmlReader = KmlReader()
}
