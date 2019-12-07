package day7

import helper.readInput
import org.junit.Assert.assertEquals
import org.junit.Test

class Day7KtTest {

    @Test
    fun samples() {
        testProgram(listOf(3, 15, 3, 16, 1002, 16, 10, 16, 1, 16, 15, 15, 4, 15, 99, 0, 0), 43210)
        testProgram(
            listOf(
                3, 23, 3, 24, 1002, 24, 10, 24, 1002, 23, -1, 23, 101,
                5, 23, 23, 1, 24, 23, 23, 4, 23, 99, 0, 0
            ), 54321
        )
        testProgram(
            listOf(
                3, 31, 3, 32, 1002, 32, 10, 32, 1001, 31, -2, 31, 1007, 31, 0, 33,
                1002, 33, 7, 33, 1, 33, 31, 31, 1, 32, 31, 31, 4, 31, 99, 0, 0, 0
            ), 65210
        )

    }

    private fun testProgram(program: List<Int>, expectedResult: Int) {
        assertEquals(expectedResult, solveA(program))
    }

    @Test
    fun solveA() {
        val program = readInput(7).readText().split(",").map { it.trim() }.map { it.toInt() }
        println("Day 7A ${day7.solveA(program)}")
    }
}