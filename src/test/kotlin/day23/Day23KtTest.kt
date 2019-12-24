package day23

import helper.readInput
import org.junit.Test
import kotlin.test.assertEquals

class Day23KtTest {

    @Test
    fun solveA() {
        val program = readInput(23).readText().trim().split(",").map(String::toLong)
        val answer = IntCodeNetwork(program).solveA()
        println("Day 23 A $answer")
        assertEquals(23954, answer)
    }

    @Test
    fun solveB() {
        val program = readInput(23).readText().trim().split(",").map(String::toLong)
        val answer = IntCodeNetwork(program).solveB()
        println("Day 23 B $answer")
        assertEquals(17265, answer)
    }
}