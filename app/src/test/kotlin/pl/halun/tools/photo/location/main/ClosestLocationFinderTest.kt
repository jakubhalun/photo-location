package pl.halun.tools.photo.location.main

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import pl.halun.tools.photo.location.kmls.Location
import pl.halun.tools.photo.location.kmls.TravelPoint
import java.time.Instant

class ClosestLocationFinderTest {

    private lateinit var finder: ClosestLocationFinder

    private val travelPoint1 = TravelPoint(Location(10.0, 10.0), Instant.parse("2023-08-20T10:00:00Z"))
    private val travelPoint2 = TravelPoint(Location(20.0, 20.0), Instant.parse("2023-08-20T10:00:30Z"))
    private val travelPoint3 = TravelPoint(Location(30.0, 30.0), Instant.parse("2023-08-20T10:02:00Z"))

    private val track = listOf(travelPoint1, travelPoint2, travelPoint3)

    @BeforeEach
    fun setup() {
        finder = ClosestLocationFinder()
    }

    @Test
    fun `finds last travel point before given time`() {
        val result = finder.findClosest(track, Instant.parse("2023-08-20T10:01:00Z"))
        assertEquals(travelPoint2, result.lastBefore)
    }

    @Test
    fun `finds closest travel point to given time`() {
        val result = finder.findClosest(track, Instant.parse("2023-08-20T10:00:40Z"))
        assertEquals(travelPoint2, result.closestInTime)
    }

    @Test
    fun `finds all stops close in time`() {
        val stopPoint1 = TravelPoint(Location(40.0, 40.0), Instant.parse("2023-08-20T10:01:10Z"))
        val stopPoint2 = TravelPoint(Location(50.0, 50.0), Instant.parse("2023-08-20T10:02:30Z"))
        val extendedTrack = track + listOf(stopPoint1, stopPoint2)

        val result = finder.findClosest(extendedTrack, Instant.parse("2023-08-20T10:02:00Z"))
        assertTrue(result.stopPoints.contains(stopPoint1))
    }
}
