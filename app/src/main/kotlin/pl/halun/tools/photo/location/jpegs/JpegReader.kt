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
        if (!file.exists()) throw InvalidJpegInputFileException("File does not exist!")
        if (file.isDirectory) throw InvalidJpegInputFileException("Single JPEG file is required, not a directory")

        val metadata: Metadata = try {
            ImageMetadataReader.readMetadata(file)
        } catch (e: Exception) {
            println(e.message)
            throw InvalidJpegInputFileException("Invalid metadata. Not an image?")
        }
        if (metadata.hasErrors()) throw InvalidJpegInputFileException("Invalid metadata. Not an image?")

        val directory = metadata.getFirstDirectoryOfType(ExifIFD0Directory::class.java)
            ?: throw InvalidJpegInputFileException("This file does not contain valid EXIF")

        return directory.getDate(ExifIFD0Directory.TAG_DATETIME)
            ?.toInstant() ?: throw InvalidJpegInputFileException("Cannot find creation date")
    }
}

class InvalidJpegInputFileException(message: String): RuntimeException(message)
