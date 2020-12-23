package day22

import helper.readInput
import org.junit.Assert.assertEquals
import org.junit.Test

class Day22KtTest {

    val sample1 = """
        deal with increment 7
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
//        testA(listOf(0, 3, 6, 9, 2, 5, 8, 1, 4, 7), sample1.lines())
        testA(listOf(3, 0, 7, 4, 1, 8, 5, 2, 9, 6), sample2.lines())
//        testA(listOf(6, 3, 0, 7, 4, 1, 8, 5, 2, 9), sample3.lines())
//        testA(listOf(9, 2, 5, 8, 1, 4, 7, 0, 3, 6), sample4.lines())
    }

    private fun testA(expectedOutput: List<Int>, input: List<String>) {
        expectedOutput.indices.forEach { initial ->
            val nextIndex = solveA(input, 10, initial.toLong()).toInt()
            assertEquals("Incorrect output index for $initial", expectedOutput.indexOf(initial), nextIndex)
        }
    }

    @Test
    fun newStackOperator() {
        val operator = Reverse(10)

        val data = listOf(
            0 to 9,
            1 to 8,
            2 to 7,
            3 to 6,
            4 to 5,
            5 to 4,
            6 to 3,
            7 to 2,
            8 to 1,
            9 to 0
        )

        data.forEach { (prev, next) ->
            assertEquals("Next index correct", next.toLong(), operator.nextIndex(prev.toLong()))
            assertEquals("Previous index correct", prev.toLong(), operator.previousIndex(next.toLong()))
        }
    }

    @Test
    fun cut3Operator() {
        val operator = Cut(10, 3)

        val data = listOf(
            0 to 7,
            1 to 8,
            2 to 9,
            3 to 0,
            4 to 1,
            5 to 2,
            6 to 3,
            7 to 4,
            8 to 5,
            9 to 6
        )

        data.forEach { (prev, next) ->
            assertEquals("Next index incorrect", next.toLong(), operator.nextIndex(prev.toLong()))
            assertEquals("Previous index incorrect", prev.toLong(), operator.previousIndex(next.toLong()))
        }
    }

    @Test
    fun cut6Operator() {
        val operator = Cut(10, 6)

        val data = listOf(
            0 to 4,
            1 to 5,
            2 to 6,
            3 to 7,
            4 to 8,
            5 to 9,
            6 to 0,
            7 to 1,
            8 to 2,
            9 to 3
        )

        data.forEach { (prev, next) ->
            assertEquals("Next index incorrect", next.toLong(), operator.nextIndex(prev.toLong()))
            assertEquals("Previous index incorrect", prev.toLong(), operator.previousIndex(next.toLong()))
        }
    }

    @Test
    fun increment3DealOperator() {
        val operator = DealWithIncrement(10, 3)

        val data = listOf(
            0 to 0,
            1 to 3,
            2 to 6,
            3 to 9,
            4 to 2,
            5 to 5,
            6 to 8,
            7 to 1,
            8 to 4,
            9 to 7
        )

        data.forEach { (prev, next) ->
            assertEquals("Next index incorrect", next.toLong(), operator.nextIndex(prev.toLong()))
            assertEquals("Previous index incorrect", prev.toLong(), operator.previousIndex(next.toLong()))
        }
    }

    @Test
    fun increment7DealOperator() {
        val operator = DealWithIncrement(10, 7)

        val data = listOf(
            0 to 0,
            1 to 7,
            2 to 4,
            3 to 1,
            4 to 8,
            5 to 5,
            6 to 2,
            7 to 9,
            8 to 6,
            9 to 3
        )

        data.forEach { (prev, next) ->
            assertEquals("Next index incorrect", next.toLong(), operator.nextIndex(prev.toLong()))
            assertEquals("Previous index incorrect", prev.toLong(), operator.previousIndex(next.toLong()))
        }
    }

    @Test
    fun samplesB() {
        assertEquals(0, solveB(sample1.lines(), 0, 10, 1))
        assertEquals(3, solveB(sample1.lines(), 1, 10, 1))
        assertEquals(6, solveB(sample1.lines(), 2, 10, 1))
        assertEquals(9, solveB(sample1.lines(), 3, 10, 1))
        assertEquals(2, solveB(sample1.lines(), 4, 10, 1))
        assertEquals(5, solveB(sample1.lines(), 5, 10, 1))
        assertEquals(8, solveB(sample1.lines(), 6, 10, 1))
        assertEquals(1, solveB(sample1.lines(), 7, 10, 1))
        assertEquals(4, solveB(sample1.lines(), 8, 10, 1))
        assertEquals(7, solveB(sample1.lines(), 9, 10, 1))
    }

    @Test
    fun solveA() {
        val solveA = solveA(readInput(22).readLines())
        println("Day 22A $solveA")
        assertEquals(3377, solveA)
    }

    @Test
    fun solveB() {
        val solveB = solveB(
            readInput(22).readLines(), 2020,
            119_315_717_514_047,
            101_741_582_076_661
        )
        println("Day 22B $solveB")
        assertEquals(29988879027217, solveB)
    }
}
