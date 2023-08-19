package pl.halun.tools.photo.location

import pl.halun.tools.photo.location.jpegs.JpegReader
import pl.halun.tools.photo.location.kmls.KmlReader
import pl.halun.tools.photo.location.main.LocationInTimeTextProvider

class Configuration {
    fun mainWindowController(
        locationInTimeTextProvider: LocationInTimeTextProvider,
        jpegReader: JpegReader,
        kmlReader: KmlReader
    ): MainWindowController =
        MainWindowController(locationInTimeTextProvider, jpegReader, kmlReader)

}