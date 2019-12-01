package day1

import helper.readInput
import org.junit.Test
import kotlin.test.assertEquals

internal class Day1KtTest {

    @Test
    fun solve() {
        val lines = readInput(1).readLines()
        val partA = solvePartA(lines)

        println("Day 1A: $partA")
        assertEquals(3219099, partA)

        val partB = solvePartB(lines)
        println("Day 1B: $partB")

        assertEquals(4825810, partB)

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
        assertEquals(2, calculateTotalFuel(14))
        assertEquals(966, calculateTotalFuel(1969))
        assertEquals(50346, calculateTotalFuel(100756))
    }
}