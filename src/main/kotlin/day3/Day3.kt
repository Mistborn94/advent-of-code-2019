package day3

import java.lang.Integer.max
import kotlin.math.abs
import kotlin.math.min

enum class Orientation {
    HORIZONTAL,
    VERTICAL
}

class Intersection(val point: Point, val steps: Int)

data class Point(val x: Int, val y: Int) : Comparable<Point> {

    override fun compareTo(other: Point): Int {
        return if (this.x != other.x) {
            x.compareTo(other.x)
        } else {
            y.compareTo(other.y)
        }
    }

    fun abs(): Int {
        return abs(x) + abs(y)
    }

}

data class Line constructor(val start: Point, val end: Point, val startingSteps: Int) {

    private val orientation: Orientation = if (start.y == end.y) Orientation.HORIZONTAL else Orientation.VERTICAL

    fun intersect(other: Line): Intersection? {
        if (other.orientation == this.orientation) {
            return null
        }
        val vertical = if (this.orientation == Orientation.VERTICAL) this else other
        val horizontal = if (this.orientation == Orientation.HORIZONTAL) this else other

        return if (vertical.start.x in horizontal.xRange && horizontal.start.y in vertical.yRange) {
            val point = Point(vertical.start.x, horizontal.start.y)
            val steps =
                vertical.startingSteps + horizontal.startingSteps + abs(point.x - horizontal.start.x) + abs(point.y - vertical.start.y)
            return Intersection(point, steps)
        } else null
    }

    val xRange = min(start.x, end.x)..max(start.x, end.x)
    val yRange = min(start.y, end.y)..max(start.y, end.y)

}

private fun List<Line>.intersectWith(other: List<Line>): List<Intersection> {
    return flatMap { a ->
        other.mapNotNull { b -> b.intersect(a) }
    }
}

fun buildWire(instructions: List<String>): Pair<MutableList<Line>, MutableList<Line>> {
    val hlines = mutableListOf<Line>()
    val vlines = mutableListOf<Line>()
    var currentX = 0
    var currentY = 0
    var currentSteps = 0;

    for (instruction in instructions) {
        val start = Point(currentX, currentY)
        val direction = instruction[0]
        val length = instruction.substring(1).toInt()

        when (direction) {
            'U' -> currentY -= length
            'D' -> currentY += length
            'L' -> currentX -= length
            'R' -> currentX += length
        }
        val end = Point(currentX, currentY)
        val line = Line(start, end, currentSteps)
        when (direction) {
            'U', 'D' -> vlines.add(line)
            'L', 'R' -> hlines.add(line)
        }

        currentSteps += length
    }
    return Pair(hlines, vlines)
}

fun solveA(wire1: List<String>, wire2: List<String>): Int {
    val (wire1H, wire1V) = buildWire(wire1)
    val (wire2H, wire2V) = buildWire(wire2)

    return (wire1H.intersectWith(wire2V) + wire1V.intersectWith(wire2H)).filter {
        it.point != Point(0, 0)
    }.minBy { it.point.abs() }!!.point.abs()
}

fun solveB(wire1: List<String>, wire2: List<String>): Int {
    val (wire1H, wire1V) = buildWire(wire1)
    val (wire2H, wire2V) = buildWire(wire2)

    return (wire1H.intersectWith(wire2V) + wire1V.intersectWith(wire2H)).filter {
        it.point != Point(0, 0)
    }.minBy { it.steps }!!.steps
}
