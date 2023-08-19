package pl.halun.tools.photo.location.jpegs

import java.time.Instant

class JpegReader {
    fun readCreationTime(path: String): Instant {
        // TODO
        return Instant.now()
    }
}

class InvalidJpegInputFileException(message: String): RuntimeException(message)
