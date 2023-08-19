package pl.halun.tools.photo.location.jpegs

import com.drew.imaging.ImageMetadataReader
import com.drew.metadata.Metadata
import com.drew.metadata.exif.ExifIFD0Directory
import java.io.File
import java.time.Instant

class JpegReader {
    fun readCreationTime(path: String): Instant {
        return getOriginalCreationDate(path)
    }

    private fun getOriginalCreationDate(path: String): Instant {
        val file = File(path)
        val metadata: Metadata = ImageMetadataReader.readMetadata(file)

        val directory = metadata.getFirstDirectoryOfType(ExifIFD0Directory::class.java)
        return directory?.getDate(ExifIFD0Directory.TAG_DATETIME_ORIGINAL)
            ?.toInstant() ?: throw InvalidJpegInputFileException("Cannot find creation date")
    }
}

class InvalidJpegInputFileException(message: String): RuntimeException(message)
