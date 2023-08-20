package pl.halun.tools.photo.location.jpegs

import com.drew.imaging.ImageMetadataReader
import com.drew.metadata.Metadata
import com.drew.metadata.exif.ExifDirectoryBase
import com.drew.metadata.exif.ExifIFD0Directory
import com.drew.metadata.exif.ExifSubIFDDirectory
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

        val subIFDDirectory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory::class.java)
        val exifIFD0Directory = metadata.getFirstDirectoryOfType(ExifIFD0Directory::class.java)
        if (subIFDDirectory == null && exifIFD0Directory == null) throw InvalidJpegInputFileException("This file does not contain valid EXIF")

        return subIFDDirectory?.getDate(ExifDirectoryBase.TAG_DATETIME_ORIGINAL)?.toInstant()
            ?: exifIFD0Directory.getDate(ExifIFD0Directory.TAG_DATETIME)?.toInstant()
            ?: throw InvalidJpegInputFileException("Cannot find creation date")
    }
}

class InvalidJpegInputFileException(message: String): RuntimeException(message)
