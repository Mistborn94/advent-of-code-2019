package day19

import day5.IntCode
import helper.drainToList
import java.util.concurrent.LinkedBlockingQueue
import kotlin.math.abs

class TractorBeam(val program: List<Long>) {
    val inputQueue = LinkedBlockingQueue<Long>()
    val outputQueue = LinkedBlockingQueue<Long>()

    fun solveA(): Int {
        for (x in 0 until 50) {
            for (y in 0 until 50) {
                runIntCode(x, y)
            }
        }

        return outputQueue.drainToList().count { it == 1L }
    }

    private fun runIntCode(x: Int, y: Int) {
        inputQueue.add(x.toLong())
        inputQueue.add(y.toLong())
        IntCode(program, inputQueue, outputQueue).runProgram()
    }

    fun solveB() {
        println("Starting...")
        var solved = false

        var x = 50
        var topY = findTopEdge(x, 0)
        var bottomY = findBottomEdge(x, topY)

        while (!solved) {
            topY = findTopEdge(x, topY)
            bottomY = findBottomEdge(x, bottomY)

            if (abs(topY - bottomY) >= 100) {
                println("Found 100 height beam at $x $topY down to $x $bottomY")
                solved = true
            }

            if (x % 50 == 0) {
                println("Tested x $x, diff is ${topY - bottomY}")
            }

            x++
        }
    }

    private fun findBottomEdge(x: Int, y: Int): Int {
        var currentY = y
        runIntCode(x, currentY)
        var currentOutput = outputQueue.take()

        return if (currentOutput == 1L) {
            //Search Downwards
            while (currentOutput == 1L) {
                currentY += 1
                runIntCode(x, currentY)
                currentOutput = outputQueue.take()
            }
            currentY - 1
        } else {
            //Search Upwards
            while (currentOutput == 0L) {
                currentY -= 1
                runIntCode(x, currentY)
                currentOutput = outputQueue.take()
            }
            currentY
        }
    }

    private fun findTopEdge(x: Int, y: Int): Int {
        var currentY = y
        runIntCode(x, currentY)
        var currentOutput = outputQueue.take()

        return if (currentOutput == 0L) {
            //Search Downwards
            while (currentOutput == 0L) {
                currentY += 1
                runIntCode(x, currentY)
                currentOutput = outputQueue.take()
            }
            currentY
        } else {
            //Search Upwards
            while (currentOutput == 1L) {
                currentY -= 1
                runIntCode(x, currentY)
                currentOutput = outputQueue.take()
            }
            currentY + 1
        }
    }

    fun printCells(size: Int) {
        for (x in 0 until size) {
            for (y in 0 until size) {
                runIntCode(x, y)
            }
        }

        val drainToList = outputQueue.drainToList()
        println(drainToList.chunked(size).joinToString(separator = "\n") { row -> row.joinToString(separator = "") + " " + row.count { it == 1L } })
    }
}