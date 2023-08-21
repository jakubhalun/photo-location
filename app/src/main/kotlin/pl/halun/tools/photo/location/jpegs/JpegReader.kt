package pl.halun.tools.photo.location.jpegs

import com.drew.imaging.ImageMetadataReader
import com.drew.metadata.Metadata
import com.drew.metadata.exif.ExifDirectoryBase
import com.drew.metadata.exif.ExifIFD0Directory
import com.drew.metadata.exif.ExifSubIFDDirectory
import java.io.File
import java.time.Instant

class JpegReader {
    fun getOriginalCreationDate(path: String): Instant {
        val file = getAndValidateFile(path)
        val metadata = getAndValidateMetadata(file)
        val directories = getAndValidateDirectories(metadata)

        return directories.getOriginalCreationDate()
    }

    private fun getAndValidateFile(path: String): File {
        val file = File(path)
        if (!file.exists()) throw InvalidJpegInputFileException("File does not exist!")
        if (file.isDirectory) throw InvalidJpegInputFileException("Single JPEG file is required, not a directory")
        return file
    }

    private fun getAndValidateMetadata(file: File): Metadata =
        try {
            ImageMetadataReader.readMetadata(file)
        } catch (e: Exception) {
            println(e.message)
            throw InvalidJpegInputFileException("Invalid metadata. Not an image?")
        }

    private fun getAndValidateDirectories(metadata: Metadata): ExifDirectories {
        val directories = ExifDirectories(
            metadata.getFirstDirectoryOfType(ExifSubIFDDirectory::class.java),
            metadata.getFirstDirectoryOfType(ExifIFD0Directory::class.java)
        )
        if (!directories.isValid()) throw InvalidJpegInputFileException("This file does not contain valid EXIF")
        return directories
    }
}

data class ExifDirectories(
    private val subIFDDirectory: ExifSubIFDDirectory?,
    private val exifIFD0Directory: ExifIFD0Directory?
) {
    fun isValid(): Boolean = subIFDDirectory != null || exifIFD0Directory != null

    fun getOriginalCreationDate(): Instant =
        subIFDDirectory?.getDate(ExifDirectoryBase.TAG_DATETIME_ORIGINAL)?.toInstant()
            ?: exifIFD0Directory?.getDate(ExifIFD0Directory.TAG_DATETIME)?.toInstant()
            ?: throw InvalidJpegInputFileException("Cannot find creation date")

}

class InvalidJpegInputFileException(message: String): RuntimeException(message)
