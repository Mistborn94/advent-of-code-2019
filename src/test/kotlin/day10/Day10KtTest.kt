package day10

import helper.readInput
import org.junit.Test
import java.lang.Math.PI
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class Day10KtTest {

    val sample8 = """
        .#..#
        .....
        #####
        ....#
        ...##
    """.trimIndent()

    val sample33 = """
        ......#.#.
        #..#.#....
        ..#######.
        .#.#.###..
        .#..#.....
        ..#....#.#
        #..#....#.
        .##.#..###
        ##...#..#.
        .#....####
    """.trimIndent()

    val sample35 = """
        #.#...#.#.
        .###....#.
        .#....#...
        ##.#.#.#.#
        ....#.#.#.
        .##..###.#
        ..#...##..
        ..##....##
        ......#...
        .####.###.
    """.trimIndent()

    val sample41 = """
        .#..#..###
        ####.###.#
        ....###.#.
        ..###.##.#
        ##.##.#.#.
        ....###..#
        ..#.#..#.#
        #..#.#.###
        .##...##.#
        .....#.#..
    """.trimIndent()

    val sample210 = """
        .#..##.###...#######
        ##.############..##.
        .#.######.########.#
        .###.#######.####.#.
        #####.##.#.##.###.##
        ..#####..#.#########
        ####################
        #.####....###.#.#.##
        ##.#################
        #####.##.###..####..
        ..######..##.#######
        ####.##.####...##..#
        .#####..#.######.###
        ##...#.##########...
        #.##########.#######
        .####.#.###.###.#.##
        ....##.##.###..#####
        .#.#.###########.###
        #.#.#.#####.####.###
        ###.##.####.##.#..##
    """.trimIndent()

    val sampleB = """
        .#....#####...#..
        ##...##.#####..##
        ##...#...#.#####.
        ..#.....#...###..
        ..#.#.....#....##
    """.trimIndent()

    @Test
    fun samplesA() {
        assertEquals(8, day10.solveA(sample8.lines()).second)
        assertEquals(33, day10.solveA(sample33.lines()).second)
        assertEquals(35, day10.solveA(sample35.lines()).second)
        assertEquals(41, day10.solveA(sample41.lines()).second)
        assertEquals(210, day10.solveA(sample210.lines()).second)
    }

    @Test
    fun solveA() {
        val lines = readInput(10).readLines()
        val solveA = solveA(lines).second
        assertEquals(260, solveA)
        println("Day 10 A $solveA")
    }

    @Test
    fun samplesB() {
        assertEquals(1112, solveB(sample210.lines(), 1))
        assertEquals(1201, solveB(sample210.lines(), 2))
        assertEquals(1202, solveB(sample210.lines(), 3))
        assertEquals(1208, solveB(sample210.lines(), 10))
        assertEquals(1600, solveB(sample210.lines(), 20))
        assertEquals(1609, solveB(sample210.lines(), 50))
        assertEquals(1016, solveB(sample210.lines(), 100))
        assertEquals(906, solveB(sample210.lines(), 199))
        assertEquals(802, solveB(sample210.lines(), 200))
        assertEquals(1009, solveB(sample210.lines(), 201))
//        assertEquals(1101, solveB(sample210.lines(), 299))
    }

    @Test
    fun solveB() {
        val lines = readInput(10).readLines()
        val solveB = solveB(lines, 200)
        assertNotEquals(112, solveB)
        assertNotEquals(900, solveB)
        println("Day 10 B $solveB")
    }
}

private fun Int.toRadians(): Double {
    return this * PI / 180
}
