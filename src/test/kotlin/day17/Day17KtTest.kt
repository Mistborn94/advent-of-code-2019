package day17

import helper.readInput
import org.junit.Test
import kotlin.test.assertEquals

class Day17KtTest {

    val sampleA = """
        ..#..........
        ..#..........
        #######...###
        #.#...#...#.#
        #############
        ..#...#...#..
        ..#####...^..
    """.trimIndent()

    @Test
    fun samples() {
        val intersections = findIntersections(sampleA.split("\n").map { it.toCharArray().toList() })
        assertEquals(76, sumIntersections(intersections))
    }

    @Test
    fun solveA() {
        val inputs = readInput(17).readText().trim().split(",").map(String::toLong)
        val solveA = solveA(inputs)

        println("Day 17A $solveA")

        assertEquals(12512, solveA)
    }
}