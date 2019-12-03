package day3

import helper.readInput
import org.junit.Test
import kotlin.test.assertEquals

internal class Day3KtTest {

    @Test
    fun sample1() {
        val s = "R75,D30,R83,U83,L12,D49,R71,U7,L72\nU62,R66,U55,R34,D71,R55,D58,R83".lines()

        assertEquals(159, solveA(s[0].split(","), s[1].split(",")))
        assertEquals(610, solveB(s[0].split(","), s[1].split(",")))
    }

    @Test
    fun sample2() {
        val s = "R98,U47,R26,D63,R33,U87,L62,D20,R33,U53,R51\nU98,R91,D20,R16,D67,R40,U7,R15,U6,R7".lines()

        assertEquals(135, solveA(s[0].split(","), s[1].split(",")))
        assertEquals(410, solveB(s[0].split(","), s[1].split(",")))
    }

    @Test
    fun sample3() {
        val s = "R8,U5,L5,D3\nU7,R6,D4,L4".lines()

        assertEquals(6, solveA(s[0].split(","), s[1].split(",")))
        assertEquals(30, solveB(s[0].split(","), s[1].split(",")))
    }

    @Test
    fun solve() {
        val lines = readInput(3).readLines();

        assertEquals(2427, solveA(lines[0].split(","), lines[1].split(",")))
        assertEquals(27890, solveB(lines[0].split(","), lines[1].split(",")))
    }

}