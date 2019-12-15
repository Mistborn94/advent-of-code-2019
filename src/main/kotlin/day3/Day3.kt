package day3

import helper.Point
import java.lang.Integer.max
import kotlin.math.min

enum class Orientation {
    HORIZONTAL {
        override fun maskConstant(point: Point): Point = Point(point.x, 0)
        override fun maskChanging(point: Point): Point = Point(0, point.y)
    },
    VERTICAL {
        override fun maskConstant(point: Point): Point = Point(0, point.y)
        override fun maskChanging(point: Point): Point = Point(point.x, 0)
    };

    abstract fun maskConstant(point: Point): Point
    abstract fun maskChanging(point: Point): Point
}

class Intersection(val point: Point, val steps: Int)

class Line(
    val start: Point,
    val orientation: Orientation,
    val length: Int,
    val startingSteps: Int
) {
    val end: Point = start + orientation.maskConstant(Point(length, length))

    fun intersect(other: Line): Intersection? {
        if (other.orientation == this.orientation) {
            return null
        }

        val point = this.constantPart + other.constantPart

        return if (point in this && point in other) {
            val steps = this.startingSteps + (point - this.start).abs() +
                    other.startingSteps + (point - other.start).abs()
            return Intersection(point, steps)
        } else null
    }

    private operator fun contains(point: Point): Boolean = point.x in xRange && point.y in yRange

    private val constantPart = this.orientation.maskChanging(start)
    private val xRange = min(start.x, end.x)..max(start.x, end.x)
    private val yRange = min(start.y, end.y)..max(start.y, end.y)
}

private fun intersectWires(listA: List<Line>, listB: List<Line>): List<Intersection> {
    return listA.flatMap { a ->
        listB.mapNotNull { b -> b.intersect(a) }
    }
}

fun buildWire(instructions: List<String>): Map<Orientation, List<Line>> {
    val wire = mapOf(
        Orientation.HORIZONTAL to mutableListOf<Line>(),
        Orientation.VERTICAL to mutableListOf<Line>()
    )

    var currentPosition = Point(0, 0)
    var currentSteps = 0

    for (instruction in instructions) {
        val direction = instruction[0]
        val length = instruction.substring(1).toInt()

        val line = when (direction) {
            'U' -> Line(currentPosition, Orientation.VERTICAL, -length, currentSteps)
            'D' -> Line(currentPosition, Orientation.VERTICAL, length, currentSteps)
            'L' -> Line(currentPosition, Orientation.HORIZONTAL, -length, currentSteps)
            'R' -> Line(currentPosition, Orientation.HORIZONTAL, length, currentSteps)
            else -> throw IllegalArgumentException("Unknown Direction")
        }

        currentPosition = line.end
        currentSteps += length

        wire[line.orientation]?.add(line)
    }
    return wire
}

fun solveA(wire1: List<String>, wire2: List<String>): Int {
    val intersections = getIntersections(wire1, wire2)
    return intersections.minBy { it.point.abs() }!!.point.abs()
}

private fun getIntersections(wire1Instr: List<String>, wire2Instr: List<String>): List<Intersection> {
    val wire1 = buildWire(wire1Instr)
    val wire2 = buildWire(wire2Instr)

    return (intersectWires(wire1.getValue(Orientation.HORIZONTAL), wire2.getValue(Orientation.VERTICAL)) +
            intersectWires(wire1.getValue(Orientation.VERTICAL), wire2.getValue(Orientation.HORIZONTAL)))
        .filter { it.point != Point(0, 0) }
}

fun solveB(wire1: List<String>, wire2: List<String>): Int {
    return getIntersections(wire1, wire2).minBy { it.steps }!!.steps
}
