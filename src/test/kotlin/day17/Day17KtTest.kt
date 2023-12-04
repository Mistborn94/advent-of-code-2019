package day17

import helper.readInput
import org.junit.Ignore
import org.junit.Test
import kotlin.test.assertEquals

class Day17KtTest {

    private val sampleA = """
        ..#..........
        ..#..........
        #######...###
        #.#...#...#.#
        #############
        ..#...#...#..
        ..#####...^..
    """.trimIndent()

    private val sampleB = """
        #######...#####
        #.....#...#...#
        #.....#...#...#
        ......#...#...#
        ......#...###.#
        ......#.....#.#
        ^########...#.#
        ......#.#...#.#
        ......#########
        ........#...#..
        ....#########..
        ....#...#......
        ....#...#......
        ....#...#......
        ....#####......
    """.trimIndent()

    private val sampleB_FullPath = "R,8,R,8,R,4,R,4,R,8,L,6,L,2,R,4,R,4,R,8,R,8,R,8,L,6,L,2"

    @Test
    fun samples() {
        val intersections = findIntersections(sampleA.toMap())
        assertEquals(76, sumIntersections(intersections))
    }

    private fun String.toMap() = split("\n").map { it.toCharArray().toList() }

    @Test
    fun solveA() {
        val inputs = readInput(17).readText().trim().split(",").map(String::toLong)
        val solveA = solveA(inputs)

        println("Day 17A $solveA")

        assertEquals(12512, solveA)
    }

    @Test
    fun samplesB() {
//        val path = commandList(sampleB.toMap())
//        val commandString = compressCommands(path)
//        assertEquals(sampleB_FullPath, commandString)
//
//        val functions = findFunctions(path)
//        println("Sample 17B:\n$functions")
//
//        println(verify(path, functions.a, functions.b, functions.c)?.joinToString(separator = ","))
    }

    @Test
//    @Ignore("Too slow :(")
    fun solveB() {
        val inputs = readInput(17).readText().trim().split(",").map(String::toLong)
        val solveB = solveB(inputs)
        assertEquals(1409507, solveB)

        println("Day 17B $solveB")
    }
}