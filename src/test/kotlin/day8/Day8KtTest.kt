package day8

import helper.readInput
import org.junit.Test

class Day8KtTest {

    @Test
    fun solve() {
        val input = readInput(8).readText().trim()
        val solveA = solveA(25, 6, input)
        println("Day 8A: $solveA")
        val solveB = solveB(25, 6, input)

        println("Day 8B:\n$solveB")
    }
}