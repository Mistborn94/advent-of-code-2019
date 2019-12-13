package day13

import helper.readInput
import org.junit.Ignore
import org.junit.Test

class Day13KtTest {

    @Test
    @Ignore
    fun solveA() {
        val program = readInput(13).readText().trim().split(",").map(String::toLong)
        val solveA = solveA(program)
        println("Day 13 A: $solveA")
    }
}