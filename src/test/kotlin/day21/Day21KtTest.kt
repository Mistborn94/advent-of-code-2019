package day21

import helper.readInput
import org.junit.Assert.assertEquals
import org.junit.Test

class Day21KtTest {

    @Test
    fun solveA() {
        val inputs = readInput(21).readText().trim().split(",").map { it.toLong() }
        val solveA = solveA(inputs)
        println("Day 21A $solveA")
        assertEquals(19353692, solveA)
    }

    @Test
    fun solveB() {
        val inputs = readInput(21).readText().trim().split(",").map { it.toLong() }
        val solveB = solveB(inputs)
        println("Day 21B $solveB")

    }
}