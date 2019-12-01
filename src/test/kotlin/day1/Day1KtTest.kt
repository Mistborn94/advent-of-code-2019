package day1

import helper.readInput
import org.junit.Test
import kotlin.test.assertEquals

internal class Day1KtTest {

    @Test
    fun solve() {
        val lines = readInput(1).readLines()
        val partA = day1.solvePartA(lines)

        println("Day 1A: $partA")

        val partB = solvePartB(lines)
        println("Day 1B: $partB")

    }

    @Test
    fun testCalcFuel() {
        assertEquals(2, calculateFuel(12))
        assertEquals(2, calculateFuel(14))
        assertEquals(654, calculateFuel(1969))
        assertEquals(33583, calculateFuel(100756))
    }

    @Test
    fun testTotalFuel() {
        assertEquals(2, calculateExtraFuel(14))
        assertEquals(966, calculateExtraFuel(1969))
        assertEquals(50346, calculateExtraFuel(100756))

    }
}