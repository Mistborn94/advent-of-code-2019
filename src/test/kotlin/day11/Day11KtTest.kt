package day11

import helper.readInput
import org.junit.Assert.assertEquals
import org.junit.Test

class Day11KtTest {

    @Test
    fun solveA() {
        val program = readInput(11).readText().trim().split(",").map(String::toLong)
        val solveA = solveA(program)
        assertEquals(1909, solveA)
        println("Day 11 A $solveA")
    }

    @Test
    fun solveB() {
        val program = readInput(11).readText().trim().split(",").map(String::toLong)
        val solveB = solveB(program).replace('0', ' ')
        println("Day 11 B \n$solveB")
    }
}