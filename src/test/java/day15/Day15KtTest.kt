package day15

import helper.readInput
import org.junit.Test

internal class Day15KtTest {

    @Test
    fun solveA() {
        val program = readInput(15).readText().trim().split(",").map { it.toLong() }

        solveA(program)
    }
}