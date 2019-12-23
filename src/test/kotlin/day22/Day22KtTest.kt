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
    fun samplesB() {
//        var stack = CardStack(10)
//        println(stack.cards)
//        repeat(10) {
//            stack = stack.runIteration(sample2.lines())
//            println(stack.cards)
//        }

        assertEquals(6, solveB(sample1.lines(), 10, 8, 1))
        assertEquals(2, solveB(sample1.lines(), 10, 8, 2))
        assertEquals(4, solveB(sample1.lines(), 10, 8, 3))
        assertEquals(8, solveB(sample1.lines(), 10, 8, 4))

        assertEquals(5, solveB(sample2.lines(), 10, 8, 1))
        assertEquals(3, solveB(sample3.lines(), 10, 7, 1))
        assertEquals(6, solveB(sample4.lines(), 10, 7, 1))
    }

    @Test
    fun solveA() {
        val solveA = solveA(readInput(22).readLines()).cards.indexOf(2019)
        println("Day 22A $solveA")
        assertTrue(solveA > 2889)
    }

    @Test
    fun solveB() {
        val solveB = solveB(readInput(22).readLines(), 119_315_717_514_047, 2020, 101_741_582_076_661)
        println("Day 22B $solveB")
        assertTrue(solveB > 25_035_251_100_630)
    }
}
