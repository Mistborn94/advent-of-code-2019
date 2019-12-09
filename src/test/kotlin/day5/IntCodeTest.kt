package day5

import helper.readInput
import org.junit.Test
import kotlin.test.assertEquals

class IntCodeTest {

    @Test
    fun testSampleInputs_Day2() {
        testProgram(
            listOf(1, 9, 10, 3, 2, 3, 11, 0, 99, 30, 40, 50),
            listOf(3500, 9, 10, 70, 2, 3, 11, 0, 99, 30, 40, 50)
        )
        testProgram(listOf(1, 0, 0, 0, 99), listOf(2, 0, 0, 0, 99))
        testProgram(listOf(2, 3, 0, 3, 99), listOf(2, 3, 0, 6, 99))
        testProgram(listOf(2, 4, 4, 5, 99, 0), listOf(2, 4, 4, 5, 99, 9801))
        testProgram(listOf(1, 1, 1, 4, 99, 5, 6, 0, 99), listOf(30, 1, 1, 4, 2, 5, 6, 0, 99))
    }

    @Test
    fun testSampleInputs_Day5() {
        val program1 = listOf(3, 9, 8, 9, 10, 9, 4, 9, 99, -1, 8)
        testOutput(program1, 8, 1)
        testOutput(program1, 7, 0)

        val program2 = listOf(3, 9, 7, 9, 10, 9, 4, 9, 99, -1, 8)
        testOutput(program2, 6, 1)
        testOutput(program2, 8, 0)

        val program3 = listOf(3, 3, 1108, -1, 8, 3, 4, 3, 99)
        testOutput(program3, 8, 1)
        testOutput(program3, 7, 0)

        val program4 = listOf(3, 3, 1107, -1, 8, 3, 4, 3, 99)
        testOutput(program4, 6, 1)
        testOutput(program4, 8, 0)

        val program5 = listOf(3, 12, 6, 12, 15, 1, 13, 14, 13, 4, 13, 99, -1, 0, 1, 9)
        testOutput(program5, 0, 0)
        testOutput(program5, 5, 1)

        val program6 = listOf(3, 3, 1105, -1, 9, 1101, 0, 0, 12, 4, 12, 99, 1)
        testOutput(program6, 0, 0)
        testOutput(program6, 5, 1)

        val program7 = listOf(
            3, 21, 1008, 21, 8, 20, 1005, 20, 22, 107, 8, 21, 20, 1006, 20, 31,
            1106, 0, 36, 98, 0, 0, 1002, 21, 125, 20, 4, 20, 1105, 1, 46, 104,
            999, 1105, 1, 46, 1101, 1000, 1, 20, 4, 20, 1105, 1, 46, 98, 99
        )
        testOutput(program7, 7, 999)
        testOutput(program7, 8, 1000)
        testOutput(program7, 9, 1001)
    }

    private fun testOutput(program: List<Int>, input: Int, output: Long) {
        val intCode = IntCode(program, listOf(input))
        intCode.runProgram()
        assertEquals(output, intCode.outputs.last())
    }

    @Test
    fun solve() {
        val originalProgram = readInput(5).readText().trim().split(",").map(String::toInt)

        val intCodeA = IntCode(originalProgram, listOf(1))
        intCodeA.runProgram()

        println("Day 5A: ${intCodeA.outputs}")

        val intCodeB = IntCode(originalProgram, listOf(5))
        intCodeB.runProgram()

        println("Day 5B: ${intCodeB.outputs}")
    }

    private fun testProgram(program: List<Int>, expected: List<Int>) {
        val intCode = IntCode(program, listOf(1))
        intCode.runProgram()
        assertEquals(expected.joinToString(), intCode.memory.joinToString())
    }
}