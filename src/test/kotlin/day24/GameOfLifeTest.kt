package day24

import helper.readInput
import org.junit.Test
import kotlin.test.assertEquals

internal class GameOfLifeTest {

    val sampleA = """
        ....#
        #..#.
        #..##
        ..#..
        #....
    """.trimIndent()

    @Test
    fun samplesA() {
        val gameOfLife = GameOfLife(sampleA.lines().toNestedCharList())
        assertEquals(2129920, gameOfLife.solveA())
    }

    @Test
    fun samplesB() {
        val gameOfLife = GameOfLife(sampleA.lines().toNestedCharList())
        assertEquals(99, gameOfLife.solveB(10))
    }

    @Test
    fun solveB() {
        val input = readInput(24).readLines().toNestedCharList()
        val solve = GameOfLife(input).solveB(200)
        println("Day 24B $solve")
    }

    private fun List<String>.toNestedCharList(): List<List<Char>> = map { it.toCollection(mutableListOf()) }

    @Test
    fun solveA() {
        val input = readInput(24).readLines().toNestedCharList()
        val solveA = GameOfLife(input).solveA()
        println("Day 24A $solveA")
        assertEquals(28781019, solveA)
    }
}