package day12

import org.junit.Assert.assertEquals
import org.junit.Ignore
import org.junit.Test

class Day12KtTest {

    val sample1 = """
        <x=-1, y=0, z=2>
        <x=2, y=-10, z=-7>
        <x=4, y=-8, z=8>
        <x=3, y=5, z=-1>
    """.trimIndent()

    val sample2 = """
        <x=-8, y=-10, z=0>
        <x=5, y=5, z=10>
        <x=2, y=-7, z=3>
        <x=9, y=-8, z=-3>
    """.trimIndent()

    val puzzleInput = """
        <x=1, y=4, z=4>
        <x=-4, y=-1, z=19>
        <x=-15, y=-14, z=12>
        <x=-17, y=1, z=10>
    """.trimIndent()

    @Test
    fun sampleA1() {
        assertEquals(179, solveA(sample1.lines(), 10))
    }

    @Test
    fun sampleB2() {
        assertEquals(4686774924, solveB(sample2.lines()))
    }

    @Test
    fun sampleA2() {
        assertEquals(1940, solveA(sample2.lines(), 100))
    }

    @Test
    fun solveA() {
        val solveA = solveA(puzzleInput.lines(), 1000)
        assertEquals(10635, solveA)
        println("Day 12A  $solveA")
    }
    
    @Test
    fun solveB() {
        println("Starting Solve B")
        val solveB = solveB(puzzleInput.lines())
        assertEquals(583523031727256, solveB)
        println("Day 12B  $solveB")
    }
}