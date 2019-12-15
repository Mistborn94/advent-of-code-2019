package day13

import helper.Point
import day5.IntCode
import helper.readInput
import helper.resize
import java.io.File
import java.util.*
import java.util.concurrent.BlockingQueue
import java.util.concurrent.CompletableFuture
import java.util.concurrent.LinkedBlockingQueue

class ArcadeGame(
    val instructionQueue: BlockingQueue<Long>,
    val renderQueue: BlockingQueue<Long>,
    val notifyQueue: BlockingQueue<Boolean>,
    val replayCommands: Queue<Int>
) {

    val commandHistory = mutableListOf<Int>()
    private val mapChars = mapOf(
        0 to ' ',
        1 to '|',
        2 to 'X',
        3 to '▀',
        4 to 'O'
    )

    private val inputs = mapOf(
        'a' to -1,
        's' to 0,
        'd' to 1
    )

    var currentScore = 0
    var grid = arrayListOf<ArrayList<Char>>()

    private fun renderCell(x: Int, y: Int, value: Int) {
        if (x == -1 && y == 0) {
            currentScore = value
        } else {
            val char = mapChars.getValue(value)
            grid.resize(y + 1, arrayListOf())
            val row = grid[y]
            row.resize(x + 1, ' ')
            row[x] = char
        }
    }

    fun runProgram() {
        while (notifyQueue.take()) {
            updateCells()
            printMap()
            promptInput()
        }
    }

    private fun updateCells() {
        val renderList = LinkedList<Long>()
        renderQueue.drainTo(renderList)
        while (renderList.isNotEmpty()) {
            val x = renderList.remove().toInt()
            val y = renderList.remove().toInt()
            val type = renderList.remove().toInt()
            renderCell(x, y, type)
        }
    }

    private fun printMap() {
        println("Current Score: $currentScore")
        println(grid.joinToString(separator = "\n") { it.joinToString(separator = "") })
    }

    private fun promptInput() {
        println("Move your joystick (a/s/d): ")
        val input = if (!replayCommands.isEmpty()) {
            replayCommands.remove()
        } else {
            var line = readLine()
            while (line.isNullOrEmpty()) {
                line = readLine()
            }

            inputs.getValue(line[0])
        }
        println("Registered input $input")
        commandHistory.add(input)
        instructionQueue.put(input.toLong())
    }
}

fun solveA(program: List<Long>): Int {
    val map = mutableMapOf<Point, Int>()
    val intCode = IntCode(program, LinkedBlockingQueue())
    intCode.runProgram()

    val outputs = intCode.outputs
    while (!outputs.isEmpty()) {
        val x = outputs.take().toInt()
        val y = outputs.take().toInt()
        val type = outputs.take().toInt()
        map[Point(x, y)] = type
    }

    println(map)
    return map.values.count { it == 2 }
}

fun runB(program: List<Long>, initialCommands: List<Int>): ArcadeGame {
    val renderQueue = LinkedBlockingQueue<Long>()
    val instructionQueue = LinkedBlockingQueue<Long>()
    val notifyQueue = LinkedBlockingQueue<Boolean>()

    val freeProgram = program.toMutableList()
    freeProgram[0] = 2

    val intCode = IntCode(freeProgram, instructionQueue, renderQueue) { notifyQueue.add(true) }
    val game = ArcadeGame(instructionQueue, renderQueue, notifyQueue, LinkedList(initialCommands))

    val intCodeFuture = CompletableFuture.runAsync { intCode.runProgram() }
    val arcadeFuture = CompletableFuture.runAsync { game.runProgram() }

    intCodeFuture.get()
    notifyQueue.add(false)
    arcadeFuture.get()

    return game
}

fun main() {
    val replayFile = File("src/main/resources/day13/replay.txt")

    if (!replayFile.exists()) {
        replayFile.createNewFile()
    }

    val readText = replayFile.readText()
    val replaySteps = if (readText.isNotEmpty())
        readText.split(",").map(String::toInt)
    else emptyList()

    val program = readInput(13).readText().trim().split(",").map(String::toLong)
    val runB = runB(program, replaySteps)
    println("Final Score: ${runB.currentScore}")

    replayFile.writeText(runB.commandHistory.subList(0, runB.commandHistory.size).joinToString(separator = ","))
}