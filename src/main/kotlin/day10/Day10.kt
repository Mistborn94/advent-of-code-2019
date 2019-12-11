package day10

import kotlin.math.abs
import kotlin.math.sqrt

data class Vector(val x: Int, val y: Int) {
    fun magnitude(): Double = sqrt((x * x + y * y).toDouble())
}

data class Point(val x: Int, val y: Int) {
    operator fun minus(other: Point): Vector = Vector(x - other.x, y - other.y)
    fun rayTo(other: Point): Ray = Ray.of(this, other)
}

data class Ray(val side: Int, val slope: Double) : Comparable<Ray> {

    override fun compareTo(other: Ray): Int {
        val sideCompare = side.compareTo(other.side)
        return if (sideCompare != 0) sideCompare else other.slope.compareTo(slope)
    }

    companion object {
        fun of(stationPosition: Point, asteroid: Point): Ray {
            val slope = slope(stationPosition, asteroid)
            val half = side(stationPosition, asteroid)
            return Ray(half, slope)
        }
    }
}

private fun determineStationPosition(asteroids: List<Point>) =
    asteroids.map { it to getRays(it, asteroids).size }.maxBy { (_, count) -> count }!!

fun solveA(lines: List<String>): Pair<Point, Int> {
    val asteroids = buildAsteroidList(lines)
    return determineStationPosition(asteroids)
}

fun solveB(lines: List<String>, asteroidIndex: Int): Int {
    val asteroids = buildAsteroidList(lines)
    val stationPosition = determineStationPosition(asteroids).first

    val rays = getRays(stationPosition, asteroids)
    val sortedRays = rays.keys.sorted()

    if (asteroidIndex > sortedRays.size) {
        throw IllegalStateException("Cannot handle an asteroidIndex $asteroidIndex greater than ${sortedRays.size} lines")
    }

    val nthRay = sortedRays[asteroidIndex - 1]
    val rayAsteroids = rays.getValue(nthRay)
    val nthAsteroid = rayAsteroids.minBy { (it - stationPosition).magnitude() }!!
    // Undo the initial y-negation to get the correct answer
    return nthAsteroid.x * 100 + abs(nthAsteroid.y)
}

private fun getRays(stationPosition: Point, asteroids: List<Point>): Map<Ray, List<Point>> {
    return asteroids.filter { it != stationPosition }
        .groupByTo(sortedMapOf<Ray, MutableList<Point>>(), stationPosition::rayTo)
}

private fun slope(first: Point, second: Point) = (second.y - first.y).toDouble() / (second.x - first.x).toDouble()

/**
 *  1 | 0
 *  1 | 0
 */
private fun side(a: Point, b: Point): Int {
    val vec = b - a
    return if (vec.x >= 0) {
        0
    } else {
        1
    }
}

private fun buildAsteroidList(lines: List<String>): List<Point> {
    val asteroids = mutableListOf<Point>()

    lines.forEachIndexed { y, line ->
        line.forEachIndexed { x, char ->
            if (char == '#') {
                //Negative y to cater for the inverted y-axis
                asteroids.add(Point(x, -y))
            }
        }
    }

    return asteroids
}

