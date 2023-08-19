package pl.halun.tools.photo.location

import pl.halun.tools.photo.location.jpegs.JpegReader
import pl.halun.tools.photo.location.kmls.KmlReader
import pl.halun.tools.photo.location.main.LocationInTimeTextProvider

class Configuration {

    fun locationInTimeTextProvider(): LocationInTimeTextProvider =
        LocationInTimeTextProvider()

    fun jpegReader(): JpegReader = JpegReader()

    fun kmlReader(): KmlReader = KmlReader()
}