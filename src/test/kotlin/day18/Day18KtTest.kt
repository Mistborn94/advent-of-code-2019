package day18

import helper.readInput
import org.junit.Assert.assertEquals
import org.junit.Ignore
import org.junit.Test
import kotlin.test.assertTrue

class Day18KtTest {

    val sample8 = """
        #########
        #b.A.@.a#
        #########
    """.trimIndent()

    val sample10 = """
        #########
        #.....c.#
        #.#####.#
        #.a.@.b.#
        #########
    """.trimIndent()

    val sample86 = """
        ########################
        #f.D.E.e.C.b.A.@.a.B.c.#
        ######################.#
        #d.....................#
        ########################
    """.trimIndent()

    val sample132 = """
        ########################
        #...............b.C.D.f#
        #.######################
        #.....@.a.B.c.d.A.e.F.g#
        ########################
    """.trimIndent()

    val sample81 = """
        ########################
        #@..............ac.GI.b#
        ###d#e#f################
        ###A#B#C################
        ###g#h#i################
        ########################
    """.trimIndent()

    val sample136 = """
        #################
        #i.G..c...e..H.p#
        ########.########
        #j.A..b...f..D.o#
        ########@########
        #k.E..a...g..B.n#
        ########.########
        #l.F..d...h..C.m#
        #################
    """.trimIndent()

    @Test
    fun testSamples() {
        assertEquals(8, solveA(sample8.toMap()))
        assertEquals(10, solveA(sample10.toMap()))
        assertEquals(86, solveA(sample86.toMap()))
        assertEquals(132, solveA(sample132.toMap()))
        assertEquals(81, solveA(sample81.toMap()))
        assertEquals(136, solveA(sample136.toMap()))
    }

    @Test
    fun solveA() {
        val solveA = solveA(readInput(18).readText().trim().toMap())
        println("Day 18 A $solveA")
        assertEquals(3048, solveA)
    }

    private fun String.toMap() = split("\n").map { it.toCharArray().toList() }
}