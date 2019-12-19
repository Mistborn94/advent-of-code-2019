package day15

import helper.readInput
import org.junit.Test
import kotlin.test.assertEquals

internal class Day15KtTest {

    @Test
    fun solveA() {
        val program = readInput(15).readText().trim().split(",").map { it.toLong() }
        val solveA = solveA(program)

        assertEquals(270, solveA)
        println("Day 15A: $solveA")
    }

    @Test
    fun solveB() {
        val program = readInput(15).readText().trim().split(",").map { it.toLong() }
        val solveB = solveB(program)

        assertEquals(364, solveB)
        println("Day 15B: $solveB")
    }
}