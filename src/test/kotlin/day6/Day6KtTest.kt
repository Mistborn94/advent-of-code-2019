package day6

import helper.readInput
import org.junit.Test
import kotlin.test.assertEquals

internal class Day6KtTest {

    val sampleA = """
        COM)B
        B)C
        C)D
        D)E
        E)F
        B)G
        G)H
        D)I
        E)J
        J)K
        K)L
    """.trimIndent()

    val sampleB = """
        COM)B
        B)C
        C)D
        D)E
        E)F
        B)G
        G)H
        D)I
        E)J
        J)K
        K)L
        K)YOU
        I)SAN
    """.trimIndent()

    @Test
    fun samplesA() {
        assertEquals(42, solveA(sampleA.lines()))
    }

    @Test
    fun samplesB() {
        assertEquals(4, solveB(sampleB.lines()))
    }

    @Test
    fun solve() {
        val readLines = readInput(6).readLines()
        val solveA = solveA(readLines)
        println("Day 6A $solveA");
        assertEquals(333679, solveA)

        val solveB = solveB(readLines)
        assertEquals(370, solveB)
        println("Day 6B $solveB")
    }
}