package day15

import day5.IntCode
import helper.Direction
import helper.Point
import java.util.concurrent.BlockingQueue

enum class Cell(val char: String) {
    SPACE("."),
    WALL("#"),
    OXYGEN_SYSTEM("*"),
    START("O");

    override fun toString(): String {
        return char
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

private val startingPosition = Point(0, 0)

class Droid(val initialIntCodeProgram: List<Long>) {

    private val grid = Grid<Cell>()

    val pointsToTraverse = LinkedHashSet<Point>()

    val seenPoints = mutableMapOf(
        startingPosition to listOf<Long>()
    )

    init {
        grid[0, 0] = Cell.START
        pointsToTraverse.add(startingPosition)
    }

    var oxygenPosition: Point? = null

    fun explore() {
        val currentPosition = pointsToTraverse.takeFirst()
        val nextPositions: List<Pair<Point, Direction>> = currentPosition.unknownNeighbours()
        val currentPath = seenPoints.getValue(currentPosition)

        nextPositions.forEach { (nextPosition, nextDirection) ->
            val command = directionCommands.getValue(nextDirection)
            val newPath = currentPath + listOf(command.toLong())
            val intCode = IntCode.ofLongs(initialIntCodeProgram, newPath)
            intCode.runUtilInput()
            val last = intCode.outputs.drainToList().last()
            val cellType = resultCodes.getValue(last.toInt())

            grid[nextPosition] = cellType

            if (cellType != Cell.WALL) {
                pointsToTraverse.add(nextPosition)
                seenPoints[nextPosition] = newPath
            }

            if (cellType == Cell.OXYGEN_SYSTEM) {
                oxygenPosition = nextPosition
            }
        }
    }

    fun searchForOxygen() {
        while (oxygenPosition == null) {
            explore()
        }
    }

    fun finishExploring() {
        while (pointsToTraverse.isNotEmpty()) {
            explore()
        }
    }

    fun fillWithOxygen(): Int {
        var oxygenCells = mutableSetOf(oxygenPosition!!)
        var nextIteration = oxygenCells.flatMap { it.openNeighbours() }.filter { it !in oxygenCells }
        var time = 0

        while (nextIteration.isNotEmpty()) {
            time += 1
            oxygenCells.addAll(nextIteration)
            nextIteration = oxygenCells.flatMap { it.openNeighbours() }.filter { it !in oxygenCells }
        }
        return time
    }

    private fun Point.unknownNeighbours(): List<Pair<Point, Direction>> {
        return this.allNeighbours().filter { (point, _) ->
            grid[point] == null && point !in this@Droid.seenPoints
        }
    }

    private fun Point.openNeighbours(): List<Point> {
        return allNeighbours().map { it.first }
            .filter { point ->
                grid[point] == Cell.SPACE
            }
    }

    private fun Point.allNeighbours(): List<Pair<Point, Direction>> {
        return listOf(
            directionFromCurrent(this, Direction.UP),
            directionFromCurrent(this, Direction.DOWN),
            directionFromCurrent(this, Direction.LEFT),
            directionFromCurrent(this, Direction.RIGHT)
        )
    }

    private fun directionFromCurrent(currentPosition: Point, direction: Direction) =
        Pair(currentPosition + direction.point, direction)
}

private fun <E> BlockingQueue<E>.drainToList(): List<E> {
    val list = mutableListOf<E>()
    this.drainTo(list)
    return list
}

private fun <E> MutableSet<E>.takeFirst(): E {
    val first = this.first()
    this.remove(first)
    return first
}

fun solveA(program: List<Long>): Int {
    val droid = Droid(program)

    droid.searchForOxygen()

    return droid.seenPoints.getValue(droid.oxygenPosition!!).size
}

fun solveB(program: List<Long>): Int {
    val droid = Droid(program)

    droid.finishExploring()

    return droid.fillWithOxygen()
}
