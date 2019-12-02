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
        val program = readInput(2).readText().split(",").map(String::trim).map(String::toInt).toTypedArray()
        program[1] = 12
        program[2] = 2
        runProgram(program)

        println("Day 2A: ${program.joinToString()}")
    }

    private fun testProgram(initial: Array<Int>, expected: Array<Int>) {
        val program = initial.copyOf()
        runProgram(program)
        assertEquals(expected.joinToString(), program.joinToString())
    }
}