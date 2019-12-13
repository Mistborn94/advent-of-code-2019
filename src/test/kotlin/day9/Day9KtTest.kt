package day9

import day5.IntCode
import helper.readInput
import org.junit.Test
import java.util.concurrent.LinkedBlockingQueue
import kotlin.test.assertEquals

class Day9KtTest {

    @Test
    fun samples() {
        val sampleA = listOf<Long>(109, 1, 204, -1, 1001, 100, 1, 100, 1008, 100, 16, 101, 1006, 101, 0, 99)

        val outputA = IntCode(sampleA, LinkedBlockingQueue()).let {
            it.runProgram()
            it.outputs
        }

        assertEquals(outputA.toList(), sampleA)

        val sampleB = listOf(1102, 34915192, 34915192, 7, 4, 7, 99, 0)
        val outputB = IntCode(sampleB, emptyList()).let {
            it.runProgram()
            it.outputs
        }

        assertEquals(16, outputB.last().toString().length)

        val sampleC = listOf(104, 1125899906842624, 99)
        val outputC = IntCode(sampleC, LinkedBlockingQueue()).let {
            it.runProgram()
            it.outputs
        }

        assertEquals(1125899906842624, outputC.last())
    }

    @Test
    fun solveA() {
        val program = readInput(9).readText().trim().split(",").map { it.toLong() }
        val output = IntCode(program, LinkedBlockingQueue(listOf(1L))).let {
            it.runProgram()
            it.outputs
        }
        println("Day 9A ${output.last()}: $output")
    }

    @Test
    fun solveB() {
        val program = readInput(9).readText().trim().split(",").map { it.toLong() }
        val output = IntCode(program, LinkedBlockingQueue(listOf(2L))).let {
            it.runProgram()
            it.outputs
        }
        println("Day 9B ${output.last()}: $output")
    }

}