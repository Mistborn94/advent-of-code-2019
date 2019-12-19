package day19

import helper.readInput
import org.junit.Test

class Day19KtTest {

    @Test
    fun solveA() {
        val program = readInput(19).readText().trim().split(",").map(String::toLong)
        val solveA = solveA(program)
        println("Day 19A $solveA")
    }
}