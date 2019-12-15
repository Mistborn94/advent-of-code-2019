package day15

import day5.IntCode
import helper.Direction
import helper.Point
import helper.log
import java.util.*
import java.util.concurrent.BlockingQueue
import java.util.concurrent.CompletableFuture
import java.util.concurrent.LinkedBlockingQueue

enum class Cell(val s: String) {
    SPACE("."),
    WALL("#"),
    OXYGEN_SYSTEM("*"),
    START("O"),
    DROID("D");

    override fun toString(): String {
        return s
    }
}

val directionCommands = mapOf(
    Direction.UP to 1,
    Direction.DOWN to 2,
    Direction.LEFT to 3,
    Direction.RIGHT to 4
)

val resultCodes = mapOf(
    0 to Cell.WALL,
    1 to Cell.SPACE,
    2 to Cell.OXYGEN_SYSTEM
)

class Droid(private val commands: BlockingQueue<Long>, private val results: BlockingQueue<Long>) {

    @Volatile
    var oxygenFound: Boolean = false
        private set

    val grid = Grid<Cell>()

    init {
        grid[0, 0] = Cell.START
    }

    var currentPosition = Point(0, 0)
    var currentDirection = Direction.RIGHT

    val currentPath = Stack<Point>()
    var oxygenPosition: Point? = null

    fun tryWalk() {
        val nextPositions: List<Pair<Point, Direction>> = findUnknownNextPositions()
        if (nextPositions.isEmpty()) {
            backtrack()
        } else {
            val (nextPosition, nextDirection) = nextPositions[0]
            val cellType = move(nextDirection)
            println("Droid: CellType $cellType at $nextPosition")
            grid[nextPosition] = cellType

            if (cellType != Cell.WALL) {
                currentPath.push(currentPosition)
                currentPosition = nextPosition
                currentDirection = nextDirection
            }

            if (cellType == Cell.OXYGEN_SYSTEM) {
                oxygenFound = true
                oxygenPosition = currentPosition
            }
        }
        val actualType = grid[currentPosition]
        grid[currentPosition] = Cell.DROID
        println(grid)
        grid[currentPosition] = actualType!!
        println("----------------------------")
    }

    private fun backtrack() {
        val previousPosition = currentPath.pop()
        val direction = Direction.fromPoint(currentPosition - previousPosition)
        move(direction)
        println("Droid: Backtraced to $previousPosition")
        currentPosition = previousPosition
        currentDirection = direction
    }

    private fun move(nextDirection: Direction): Cell {
        println("Droid: Starting move $nextDirection")
        val command = directionCommands.getValue(nextDirection)
        commands.add(command.toLong())
        println("Droid: Sent command $command, waiting for result")
        return resultCodes.getValue(results.take().toInt())
    }

    private fun findUnknownNextPositions(): List<Pair<Point, Direction>> {
        return listOf(
            directionFromCurrent(currentDirection.left),
            directionFromCurrent(currentDirection.left.left),
            directionFromCurrent(currentDirection.right),
            directionFromCurrent(currentDirection)
        ).filter { (point, _) ->
            grid[point] == null
        }
    }

    private fun directionFromCurrent(direction: Direction) = Pair(currentPosition + direction.point, direction)
}


fun solveA(program: List<Long>) {

    val commands = LinkedBlockingQueue<Long>(listOf<Long>(0, 0, 0, 0, 0, 0))
    val results = LinkedBlockingQueue<Long>()

    val intCode = IntCode(program, commands, results)
    val droid = Droid(commands, results)

    CompletableFuture.runAsync {
        while (!droid.oxygenFound) {
            intCode.runProgram()
        }
    }

    CompletableFuture.runAsync {
        while (!droid.oxygenFound) {
            droid.tryWalk()
        }
    }.get()



    println(" Found oxygen system at ${droid.oxygenPosition}")
}
