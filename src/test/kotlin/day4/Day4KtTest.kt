package day4

import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class Day4KtTest {

    @Test
    fun solve() {
        println("Day 4A " + solveA(357253, 892942))
        println("Day 4B " + solveB(357253, 892942))
    }

    @Test
    fun samples() {
        assertTrue(hasLonelyAdjacentNumbers("112233"))
        assertTrue(hasLonelyAdjacentNumbers("111122"))
        assertFalse(hasLonelyAdjacentNumbers("123444"))
    }
}