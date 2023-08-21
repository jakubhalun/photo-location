package pl.halun.tools.photo.location.main

import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import pl.halun.tools.photo.location.kmls.Location
import pl.halun.tools.photo.location.kmls.TravelPoint
import java.time.Instant
import java.time.temporal.ChronoUnit

class LocationInTimeTextProviderTest {

    private lateinit var closestLocationFinder: ClosestLocationFinder
    private lateinit var textFormatter: LocationResultTextFormatter
    private lateinit var provider: LocationInTimeTextProvider

    private val startOfTrackTime = Instant.parse("2023-01-01T00:00:00Z")
    private val endOfTrackTime = Instant.parse("2023-01-01T12:00:00Z")

    @BeforeEach
    fun setup() {
        closestLocationFinder = mockk()
        textFormatter = mockk()
        provider = LocationInTimeTextProvider(closestLocationFinder, textFormatter)

        // Set up a default scenario
        every { closestLocationFinder.findClosest(any(), any()) } returns mockk()
        every { textFormatter.prepareText(any(), any()) } returns "Some text"
    }

    @Test
    fun `test long mismatch before start of track`() {
        val points = listOf(
            TravelPoint(Location(50.0, 20.0), startOfTrackTime),
            TravelPoint(Location(50.0, 20.0), endOfTrackTime)
        )
        provider.textForChangedLocations(points)

        val result = provider.textForChangedTime(startOfTrackTime.minus(2, ChronoUnit.DAYS))

        assertEquals("The JPEG creation time is well outside of tracked travel points (wrong selection of KML file?). Tracked time between $startOfTrackTime and $endOfTrackTime", result)
    }

    @Test
    fun `test long mismatch after end of track`() {
        val points = listOf(
            TravelPoint(Location(50.0, 20.0), startOfTrackTime),
            TravelPoint(Location(50.0, 20.0), endOfTrackTime)
        )
        provider.textForChangedLocations(points)

        val result = provider.textForChangedTime(endOfTrackTime.plus(2, ChronoUnit.DAYS))

        assertEquals("The JPEG creation time is well outside of tracked travel points (wrong selection of KML file?). Tracked time between $startOfTrackTime and $endOfTrackTime", result)
    }

    @Test
    fun `test short mismatch before start of track`() {
        val points = listOf(
            TravelPoint(Location(50.0, 20.0), startOfTrackTime),
            TravelPoint(Location(50.0, 20.0), endOfTrackTime)
        )
        provider.textForChangedLocations(points)

        val result = provider.textForChangedTime(startOfTrackTime.minus(2, ChronoUnit.MINUTES))

        assertEquals("The JPEG creation time is outside of tracked travel points (wrong selection of time zone?). Tracked time between $startOfTrackTime and $endOfTrackTime", result)
    }

    @Test
    fun `test short mismatch after end of track`() {
        val points = listOf(
            TravelPoint(Location(50.0, 20.0), startOfTrackTime),
            TravelPoint(Location(50.0, 20.0), endOfTrackTime)
        )
        provider.textForChangedLocations(points)

        val result = provider.textForChangedTime(endOfTrackTime.plus(16, ChronoUnit.MINUTES))

        assertEquals("The JPEG creation time is outside of tracked travel points (wrong selection of time zone?). Tracked time between $startOfTrackTime and $endOfTrackTime", result)
    }
}
