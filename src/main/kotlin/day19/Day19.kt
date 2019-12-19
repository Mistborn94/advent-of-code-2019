package day19

import day5.IntCode
import helper.drainToList
import java.util.concurrent.LinkedBlockingQueue

fun solveA(program: List<Long>): Int {

    val inputSequence = sequence {
        for (x in 0 until 50) {
            for (y in 0 until 50) {
                yield(x)
                yield(y)
            }
        }
    }

    val inputQueue = LinkedBlockingQueue<Long>()
    val outputQueue = LinkedBlockingQueue<Long>()
    for (x in 0 until 50) {
        for (y in 0 until 50) {
            val intCode = IntCode(program, inputQueue, outputQueue)
            inputQueue.add(x.toLong())
            inputQueue.add(y.toLong())
            intCode.runProgram()
        }
    }

    val drainToList = outputQueue.drainToList()
    println(drainToList.chunked(50).joinToString(separator = "\n") { it.joinToString(separator = "") + " " + it.count { it == 1L } })
    return drainToList.count { it == 1L }
}