package day2

import helper.readInput
import org.junit.Test
import kotlin.test.assertEquals

class Day2KtTest {

    @Test
    fun testSampleInputs() {
        testProgram(arrayOf(1, 0, 0, 0, 99), arrayOf(2, 0, 0, 0, 99))
        testProgram(arrayOf(2, 3, 0, 3, 99), arrayOf(2, 3, 0, 6, 99))
        testProgram(arrayOf(2, 4, 4, 5, 99, 0), arrayOf(2, 4, 4, 5, 99, 9801))
        testProgram(arrayOf(1, 1, 1, 4, 99, 5, 6, 0, 99), arrayOf(30, 1, 1, 4, 2, 5, 6, 0, 99))
    }

    @Test
    fun solve() {
        val originalProgram = readInput(2).readText().split(",").map(String::trim).map(String::toInt).toTypedArray()
        val program = runProgram(originalProgram, 12, 2)

        println("Day 2A: ${program[0]}")

        val answerB = solveB(originalProgram, 19690720)
        println("Day 2B: $answerB")
    }

    private fun testProgram(program: Array<Int>, expected: Array<Int>) {
        assertEquals(expected.joinToString(), runProgram(program).joinToString())
    }
}