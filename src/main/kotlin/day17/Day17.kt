package day17

import day5.IntCode
import helper.Point
import java.util.concurrent.BlockingQueue

fun solveA(program: List<Long>): Int {

    val intCode = IntCode(program) { println("Waiting for input") }
    intCode.runProgram()

    val outputList = intCode.outputs.drainToList().map { it.toChar() }

    val lines = buildMap(outputList)

    lines.forEach {
        println(it.joinToString(separator = ""))
    }

    val intersections = findIntersections(lines)

    return sumIntersections(intersections)
}

private fun buildMap(outputList: List<Char>): List<List<Char>> {
    val firstLineEnd = outputList.indexOf('\n')
    val lines = outputList.filter { it != '\n' }.chunked(firstLineEnd)
    return lines
}


fun sumIntersections(intersections: MutableList<Point>) =
    intersections.sumBy { it.x * it.y }

fun findIntersections(lines: List<List<Char>>): MutableList<Point> {
    val intersections = mutableListOf<Point>()
    for (y in 1 until lines.lastIndex) {
        for (x in 1 until lines[0].lastIndex) {
            if (lines[y][x] == '#' &&
                lines[y - 1][x] == '#' &&
                lines[y + 1][x] == '#' &&
                lines[y][x - 1] == '#' &&
                lines[y][x + 1] == '#'
            ) {
                intersections.add(Point(x, y))
            }
        }
    }
    return intersections
}

private fun BlockingQueue<Long>.drainToList(): List<Long> {
    val outputList = mutableListOf<Long>()
    drainTo(outputList)
    return outputList
}