package day13

import day3.Point
import day5.IntCode

fun solveA(program: List<Int>): Int {
    val map = mutableMapOf<Point, Int>()
    val intCode = IntCode(program, emptyList())
    intCode.runProgram()

    val outputs = intCode.outputs
    while (!outputs.isEmpty()) {
        val x = outputs.take().toInt()
        val y = outputs.take().toInt()
        val type = outputs.take().toInt()
        map[Point(x, y)] = type
    }

    return map.values.count { it == 2 }

}