package day16

import helper.readInput
import org.junit.Ignore
import org.junit.Test
import kotlin.test.assertEquals

internal class Day16KtTest {

    val sampleA1 = "12345678"
    val sampleA2 = "80871224585914546619083218645595"
    val sampleA3 = "19617804207202209144916044189917"
    val sampleA4 = "69317163492948606335995924319873"

    val sampleB1 = "03036732577212944063491565474664"
    val sampleB2 = "02935109699940807407585447034323"
    val sampleB3 = "03081770884921959731165446850517"

    @Test
    fun samplesA() {
        assertEquals("48226158", solveA(sampleA1, 1))
        assertEquals("34040438", solveA(sampleA1, 2))
        assertEquals("03415518", solveA(sampleA1, 3))
        assertEquals("01029498", solveA(sampleA1, 4))

        assertEquals("24176176", solveA(sampleA2))
        assertEquals("73745418", solveA(sampleA3))
        assertEquals("52432133", solveA(sampleA4))
    }

    @Test
    fun experimentB() {
        solveB(sampleB1, 10, repeat = 5)
    }

    @Test
//    @Ignore("This is waaaaaay too slow. Needs some work")
    fun sampleB1() {
        assertEquals("84462026", solveB(sampleB1, repeat = 10_000))
    }

    @Test
    @Ignore("This is waaaaaay too slow. Needs some work")
    fun sampleB2() {
        assertEquals("78725270", solveB(sampleB2, repeat = 10_000))
    }

    @Test
    @Ignore("This is waaaaaay too slow. Needs some work")
    fun sampleB3() {
        assertEquals("53553731", solveB(sampleB3, repeat = 10_000))
    }

    @Test
    @Ignore("This is waaaaaay too slow. Needs some work")
    fun solveB() {
        val input = readInput(16).readText().trim()
        val solveB = solveB(input, repeat = 10_000)
        println("Day 16B $solveB")
    }

    @Test
    fun solveA() {
        val input = readInput(16).readText().trim()
        val solveA = solveA(input)
        println("Day 16A $solveA")
        assertEquals("15841929", solveA)
    }
}