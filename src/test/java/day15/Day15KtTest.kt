package day15

import helper.readInput
import org.junit.Ignore
import org.junit.Test
import java.util.concurrent.LinkedBlockingQueue

@Ignore("Ite is broken")
internal class Day15KtTest {

    @Test
    fun sampleA() {
        val outputs = listOf<Long>(0, 1, 0, 0, 0, 1, 0, 1, 0, 2)
        val droid = Droid(LinkedBlockingQueue(), LinkedBlockingQueue(outputs))

        droid.run()
        println(droid.grid)
    }

    @Test
    fun solveA() {
        val program = readInput(15).readText().trim().split(",").map { it.toLong() }

        solveA(program)
    }
}