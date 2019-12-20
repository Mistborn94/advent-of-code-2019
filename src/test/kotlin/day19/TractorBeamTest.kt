package day19

import helper.readInput
import org.junit.Ignore
import org.junit.Test

class TractorBeamTest {

    @Test
    @Ignore
    fun solveA() {
        val program = readInput(19).readText().trim().split(",").map(String::toLong)
        val solveA = TractorBeam(program).solveA()
        println("Day 19A $solveA")
    }

    @Test
    fun solveB() {
        val program = readInput(19).readText().trim().split(",").map(String::toLong)
//        TractorBeam(program).printCells(100)
        val solveB = TractorBeam(program).solveB()
        println("Day 19B $solveB")
    }
}