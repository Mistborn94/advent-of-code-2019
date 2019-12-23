package day22

import helper.readInput
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class Day22KtTest {

    val sample1 = """
        deal with increment 7
        deal into new stack
        deal into new stack
    """.trimIndent()

    val sample2 = """
        cut 6
        deal with increment 7
        deal into new stack
    """.trimIndent()

    val sample3 = """
        deal with increment 7
        deal with increment 9
        cut -2
    """.trimIndent()

    val sample4 = """
        deal into new stack
        cut -2
        deal with increment 7
        cut 8
        cut -4
        deal with increment 7
        cut 3
        deal with increment 9
        deal with increment 3
        cut -1
    """.trimIndent()

    @Test
    fun samplesA() {
        assertEquals(listOf(0, 3, 6, 9, 2, 5, 8, 1, 4, 7), solveA(sample1.lines(), 10).cards)
        assertEquals(listOf(3, 0, 7, 4, 1, 8, 5, 2, 9, 6), solveA(sample2.lines(), 10).cards)
        assertEquals(listOf(6, 3, 0, 7, 4, 1, 8, 5, 2, 9), solveA(sample3.lines(), 10).cards)
        assertEquals(listOf(9, 2, 5, 8, 1, 4, 7, 0, 3, 6), solveA(sample4.lines(), 10).cards)
    }

    @Test
    fun solveA() {
        val solveA = solveA(readInput(22).readLines()).cards.indexOf(2019)
        print("Day 22A $solveA")
        assertTrue(solveA > 2889)
    }

    @Test
    fun solveB() {
        val solveA = solveB(readInput(22).readLines(), 101741582076661L).cards.indexOf(2019)
        print("Day 22A $solveA")
        assertTrue(solveA > 2889)
    }
}
