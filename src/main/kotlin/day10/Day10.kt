package day10

import kotlin.math.sqrt

data class Vector(val x: Int, val y: Int) {

    fun magnitude(): Double {
        return sqrt((x * x + y * y).toDouble())
    }
}

data class Point(val x: Int, val y: Int) {

    operator fun minus(other: Point): Vector {
        return Vector(x - other.x, y - other.y)
    }

    operator fun plus(other: Point): Point {
        return Point(x + other.x, y + other.y)
    }
}


fun solveA(lines: List<String>): Pair<Point, Int> {
    val asteroids = buildAsteroidList(lines)

    return determineStationPosition(asteroids)
}

private fun determineStationPosition(asteroids: List<Point>) =
    asteroids.map { it to seenAsteroids(it, asteroids) }.maxBy { it.second }!!

data class Ray(val half: Int, val slope: Double) : Comparable<Ray> {

    override fun compareTo(other: Ray): Int {
        val quadrantCompare = half.compareTo(other.half)
        return if (quadrantCompare != 0) quadrantCompare else other.slope.compareTo(slope)
    }
}

fun solveB(lines: List<String>, asteroidIndex: Int): Int {
    val asteroids = buildAsteroidList(lines)
    val stationPosition = determineStationPosition(asteroids).first

    val rays = getRays(stationPosition, asteroids)

    val sortedLines = rays.keys.sorted()

    if (sortedLines.size < asteroidIndex) {
        throw IllegalStateException("Cannot handle an asteroidIndex $asteroidIndex greater than ${sortedLines.size} lines")
    }

    val destroyingRay = sortedLines[asteroidIndex - 1]
    val ray200 = rays.getValue(destroyingRay)
    val destroyedPlanet = ray200.minBy { (it - stationPosition).magnitude() }!!
    return destroyedPlanet.x * 100 + destroyedPlanet.y
}

private fun getRays(stationPosition: Point, asteroids: List<Point>): Map<Ray, List<Point>> {
    return asteroids.filter { it != stationPosition }
        .groupByTo(sortedMapOf<Ray, MutableList<Point>>()) {
            val slope = slope(stationPosition, it)
            val half = half(stationPosition, it)
            Ray(half, slope)
        }
}

//The Slope y is swapped due to the inverted coordinate system
private fun slope(first: Point, second: Point) =
    (first.y - second.y).toDouble() / (second.x - first.x).toDouble()

fun seenAsteroids(currentAsteroid: Point, asteroids: List<Point>): Int {
    return getRays(currentAsteroid, asteroids).size
}

/**
 *  1 | 0
 *  1 | 0
 */
private fun half(a: Point, b: Point): Int {
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
                asteroids.add(Point(x, y))
            }
        }
    }

    return asteroids;
}

