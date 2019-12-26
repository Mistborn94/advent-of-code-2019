package day25

import day5.IntCode
import helper.drainToList
import helper.readInput
import java.util.*
import java.util.concurrent.CancellationException
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutionException
import java.util.concurrent.LinkedBlockingQueue

enum class Direction {
    SOUTH {
        override val opposite: Direction
            get() = NORTH
    },
    WEST {
        override val opposite: Direction
            get() = EAST
    },
    EAST {
        override val opposite: Direction
            get() = WEST
    },
    NORTH {
        override val opposite: Direction
            get() = SOUTH
    };

    abstract val opposite: Direction
}

class Room(val name: String, val doors: Set<Direction>, val initialItems: Set<String> = setOf<String>()) {
    val neighbours = mutableMapOf<Direction, Room>()

    fun hasUnknownNeighbours() = doors.any { neighbours[it] == null }
    fun unknownNeighbours() = doors.filter { neighbours[it] == null }
}

class PathState {
    var lastItem: String? = null
    var currentRoom: Room? = null
    val currentPath = Stack<Direction>()
    val commandsHistory = mutableListOf<String>()
}

private const val securityCheckpointName = "== Security Checkpoint =="

class ShipExplorer(val program: List<Long>) {
    val untakeableItems = mutableSetOf("infinite loop", "giant electromagnet")
    val allItems = mutableListOf<String>()
    val rooms = mutableMapOf<String, Room>()
    var pathToSecurityCheckpoint: List<Direction>? = null

    fun handleOutput(output: String, pathState: PathState): String {
        pathState.lastItem = null
        val trimmedOutput = output.trim()
        when {
            trimmedOutput.contains("A loud, robotic voice says") -> {
                throw IllegalStateException("Passed Security Checkpoint")
            }
            trimmedOutput.contains("==") -> {
                val intialOutputLines = trimmedOutput.lines()
                val outputLines =
                    intialOutputLines.run { subList(indexOfLast { it.startsWith("==") }, size) }

                val roomName = outputLines[0]
                val doors = outputLines.sectionFor("Doors here lead:").map { it.trim(' ', '-').toUpperCase() }
                    .map { Direction.valueOf(it) }.toSet()
                val items = outputLines.sectionFor("Items here:").map { it.trim(' ', '-') }.toSet()

                val currentRoom = rooms.getOrPut(roomName) {
                    Room(roomName, doors, items)
                }

                val previousRoom = pathState.currentRoom

                if (previousRoom != null && pathState.currentPath.isNotEmpty()) {
                    val lastStep = pathState.currentPath.peek()
                    previousRoom.neighbours[lastStep] = currentRoom
                    currentRoom.neighbours[lastStep.opposite] = previousRoom
                }

                if (roomName == securityCheckpointName) {
                    pathToSecurityCheckpoint = pathState.currentPath.toList()
                    return backtrack(pathState)
                }
                pathState.currentRoom = currentRoom

                val takableItems = items.filter { it !in untakeableItems }
                if (takableItems.size > 1) {
                    throw IllegalStateException("Cannot handle more than one item yet")
                } else if (takableItems.isNotEmpty()) {
                    allItems.addAll(takableItems)
                    pathState.lastItem = takableItems[0]
                    return "take ${takableItems[0]}"
                }

                return issueMoveCommand(pathState)

            }
            trimmedOutput.startsWith("You take the") -> {
                return issueMoveCommand(pathState)
            }
            else -> {
                println("Unable to handle output: \n******\n$output\n******\n")
                throw IllegalArgumentException("Unable to handle output.")
            }
        }
    }

    private fun backtrack(pathState: PathState): String {
        val last = pathState.currentPath.pop()
        return last.opposite.name.toLowerCase()
    }

    private fun issueMoveCommand(pathState: PathState): String {
        val unknownNeighbours = pathState.currentRoom!!.unknownNeighbours()
        return if (unknownNeighbours.isNotEmpty()) {
            val direction = unknownNeighbours.first()
            pathState.currentPath.push(direction)
            direction.name.toLowerCase()
        } else {
            backtrack(pathState)
        }
    }

    private val unexploredRooms: Boolean
        get() = rooms.isEmpty() || rooms.values.any { it.hasUnknownNeighbours() && it.name != securityCheckpointName }

    fun solveA() {
        var previousIteration: PathState? = null
        while (unexploredRooms) {
            val pathState = PathState()
            exploreRooms(pathState, previousIteration)

            previousIteration = pathState
//            println("Finished Iteration\n++++++++++++++++++++")
        }

        solveSecurityCheck(previousIteration!!)


    }

    private fun solveSecurityCheck(previousIteration: PathState) {
        val backtrack = previousIteration.currentPath.reversed().map { it.opposite.name.toLowerCase() }
        val securityPath = pathToSecurityCheckpoint!!.map { it.name.toLowerCase() }
        val initialCommands = previousIteration.commandsHistory  + backtrack + securityPath
        val inputNotifier = LinkedBlockingQueue<Boolean>()
        val intCode = buildIntCode(inputNotifier, initialCommands)

        val securityRoom = rooms.getValue(securityCheckpointName)

        val inventory = checkInventory(intCode)
        val dropAll = inventory.map { "drop $it" }
        val takeAll = inventory.map { "take $it" }

        val sentCombinations = mutableSetOf<Set<String>>()
        val combinationsToExpand = mutableSetOf(emptySet<String>())
        val directionCommand = securityRoom.unknownNeighbours().first().name.toLowerCase()

        do {
            val combination = combinationsToExpand.first()
            combinationsToExpand.remove(combination)

            if (sentCombinations.add(combination)) {
                intCode.sendAscii(dropAll)
                intCode.sendAscii(combination)
                intCode.sendAscii("inv")
                intCode.sendAscii(directionCommand)

                val add = takeAll - combination
                val newCombinations = add.map { combination + it }
                combinationsToExpand.addAll(newCombinations)
            } else {
                println("Already Sent $combination")
            }
        } while (combinationsToExpand.isNotEmpty())

        intCode.runUtilInput()
        println(intCode.readOutputString())
    }

    //Heavier: 48
    //Lighter: 80

    private fun checkInventory(intCode: IntCode): Set<String> {
        intCode.sendAscii("inv")
        intCode.runUtilInput()
        val outputs = intCode.readOutputString().lines()
        return outputs.sectionFor("Items in your inventory:").map { it.trim(' ', '-') }.toSet()
    }

    private fun List<String>.sectionFor(line: String): List<String> {
        val startIndex = indexOfFirst { it == line }

        if (startIndex == -1) {
            return emptyList()
        }

        val endIndex = startIndex + subList(startIndex, size).indexOfFirst { it.isBlank() }
        return subList(startIndex + 1, endIndex)
    }

    private fun exploreRooms(pathState: PathState, previousIteration: PathState?) {
        val initialCommands = commandHistoryWithoutLast(previousIteration)

        pathState.commandsHistory.addAll(initialCommands)
        pathState.currentPath.addAll(previousIteration?.currentPath ?: emptyList())

        val notifierQueue = LinkedBlockingQueue<Boolean>()

        val intCode = buildIntCode(notifierQueue, initialCommands)

        val intcodeFuture = CompletableFuture.runAsync {
            try {
                intCode.runProgram()
            } finally {
                notifierQueue.add(false)
            }
        }
        val exploringFuture = CompletableFuture.runAsync {
            while (unexploredRooms && notifierQueue.take()) {
                val outputString = intCode.readOutputString()
                val command = handleOutput(outputString, pathState)
                pathState.commandsHistory.add(command)
                intCode.sendAscii(command)
            }
            if (intCode.running) {
                pathState.commandsHistory.add("Cancel")
                intcodeFuture.cancel(true)
            } else {
                val lastItem = pathState.lastItem
                if (lastItem != null) {
                    untakeableItems.add(lastItem)
                }
            }
        }

        try {
            CompletableFuture.allOf(exploringFuture, intcodeFuture).get()
        } catch (e: ExecutionException) {
            println("Execution Exception: $e")
            if (e.cause == null) {
                throw  IllegalStateException(e)
            } else if (e.cause !is CancellationException) {
                throw IllegalStateException(e.cause!!)
            }
        }
    }

    private fun IntCode.readOutputString(): String {
        val outputs = outputs.drainToList()
        return outputs.joinToString(separator = "") { it.toChar().toString() }
    }

    private fun buildIntCode(
        notifierQueue: LinkedBlockingQueue<Boolean>,
        initialCommands: List<String>
    ): IntCode {
        val intCode = IntCode(program) {
            notifierQueue.put(true)
        }
        intCode.sendAscii(initialCommands)
        return intCode
    }

    private fun commandHistoryWithoutLast(previousIteration: PathState?): List<String> {
        return previousIteration?.commandsHistory?.run {
            subList(0, lastIndex)
        } ?: emptyList()
    }
}

fun main() {
    val program = readInput(25).readText().trim().split(",").map(String::toLong)
    ShipExplorer(program).solveA()
}

private fun readCommand(): String {
    var instruction = readLine()
    while (instruction == null) {
        instruction = readLine()
    }
    return instruction
}