package day24

import helper.Point
import helper.get
import helper.set
import kotlin.math.pow

const val BUG = '#'
const val SPACE = '.'

private typealias CharGrid = List<List<Char>>

class GameOfLife(val initialGrid: CharGrid) {

    val emptyGrid = Array(25) { SPACE }.toList().chunked(5)

    val centerCell = Point(2, 2)

    val topRow = (0 until 5).map { Point(it, 0) }
    val bottomRow = (0 until 5).map { Point(it, 4) }
    val leftColumn = (0 until 5).map { Point(0, it) }
    val rightColumn = (0 until 5).map { Point(4, it) }

    val innerCells = listOf(Point(1, 1), Point(3, 1), Point(3, 3), Point(1, 3))
    val innerRing = mapOf(
        Point(2, 1) to topRow,
        Point(3, 2) to rightColumn,
        Point(2, 3) to bottomRow,
        Point(1, 2) to leftColumn
    )
    val outerRing =
        (topRow + rightColumn + bottomRow + leftColumn).toSet()

    val rescursiveSeenStates = mutableMapOf<Int, MutableMap<Int, CharGrid>>()

    val indexRange = 0 until 5

    fun solveA(): Int {
        val seenStates = mutableSetOf(initialGrid)

        var currentState = simulateBasic(initialGrid)

        while (currentState !in seenStates) {
            seenStates.add(currentState)
            currentState = simulateBasic(currentState)
        }

        return biodiversityRating(currentState)
    }

    fun solveB(repetitions: Int): Int {
        repeat(repetitions + 1) { i ->
            getState(i + 1, 0)
            var lowerLevel = -1
            while (getState(i, lowerLevel).bugCount() > 0) {
                getState(i + 1, lowerLevel)
                lowerLevel -= 1
            }

            var higherLevel = 1
            while (getState(i, higherLevel).bugCount() > 0) {
                getState(i + 1, higherLevel)
                higherLevel += 1
            }
        }
        val maps = rescursiveSeenStates.getValue(repetitions)
        return maps.values.sumBy { it.bugCount() }
    }

    private fun getState(iteration: Int, level: Int): CharGrid {
        return if (iteration == 0) {
            if (level == 0) initialGrid else emptyGrid
        } else {
            val iterationMap = rescursiveSeenStates.getOrPut(iteration) { mutableMapOf() }
            val levelMap = iterationMap[level]
            if (levelMap != null) {
                levelMap
            } else {
                val layer = simulateRecursiveLayer(iteration, level)
                iterationMap[level] = layer
                layer
            }
        }
    }

    private fun simulateRecursiveLayer(iteration: Int, level: Int): CharGrid {
        val newState = Array(25) { '?' }.toList().chunked(5) { it.toMutableList() }.toMutableList()
        val previousLevelState = getState(iteration - 1, level)

        innerCells.forEach { currentPoint ->
            val neighbourBugCount = currentPoint.neighbours().count { previousLevelState[it] == BUG }
            val currentCell = previousLevelState[currentPoint]

            newState[currentPoint] =
                calculateCellContents(currentCell, neighbourBugCount)
        }

        outerRing.forEach { currentPoint ->
            val neighbours = currentPoint.neighbours()
            val currentLevelNeighbours = neighbours.filter { it.x in this.indexRange && it.y in this.indexRange }
            val nextLevelIndex = level - 1
            val nextLevelNeighbours = (neighbours - currentLevelNeighbours).map { (x, y) ->
                val newX = if (x == currentPoint.x) 2 else if (x < 0) 1 else 3
                val newY = if (y == currentPoint.y) 2 else if (y < 0) 1 else 3
                Point(newX, newY)
            }

            newState[currentPoint] =
                calculateRecursiveCellContents(
                    currentPoint,
                    iteration,
                    previousLevelState,
                    currentLevelNeighbours,
                    nextLevelIndex,
                    nextLevelNeighbours
                )
        }

        innerRing.forEach { (currentPoint, nextLevelNeighbours) ->
            val currentLevelNeighbours = currentPoint.neighbours().filter { it != centerCell }
            val nextLevelIndex = level + 1
            newState[currentPoint] = calculateRecursiveCellContents(
                currentPoint,
                iteration,
                previousLevelState,
                currentLevelNeighbours,
                nextLevelIndex,
                nextLevelNeighbours
            )
        }

        return newState
    }

    private fun calculateRecursiveCellContents(
        currentPoint: Point,
        iteration: Int,
        previousLevelState: CharGrid,
        currentLevelNeighbours: List<Point>,
        nextLevelIndex: Int,
        nextLevelNeighbours: List<Point>
    ): Char {
        val currentCell = previousLevelState[currentPoint]
        val currentLevelNeighbourBugs = currentLevelNeighbours.count { previousLevelState[it] == BUG }

        //If we know it will die / stay a space, no need to go deeper
        return if (currentCell == BUG && currentLevelNeighbourBugs > 1 || currentCell == SPACE && currentLevelNeighbourBugs > 2) {
            SPACE
        } else {
            val nextLevel = getState(iteration - 1, nextLevelIndex)
            val nextLevelNeighbourBugs = nextLevelNeighbours.count { nextLevel[it] == BUG }
            calculateCellContents(currentCell, currentLevelNeighbourBugs + nextLevelNeighbourBugs)
        }
    }

    private fun biodiversityRating(currentState: List<List<Char>>): Int {
        return currentState.flatten().foldIndexed(0) { i, acc, char ->
            if (char == BUG) {
                acc + 2.0.pow(i).toInt()
            } else {
                acc
            }
        }
    }

    private fun simulateBasic(currentState: CharGrid): CharGrid {
        val newState = mutableListOf<List<Char>>()
        for (y in this.indexRange) {
            val line = mutableListOf<Char>()
            for (x in this.indexRange) {
                val currentPoint = Point(x, y)
                val neighbours =
                    currentPoint.neighbours().filter { (x, y) -> x in this.indexRange && y in this.indexRange }
                        .map { currentState[it] }

                val bugCount = neighbours.count { it == BUG }
                val currentCell = currentState[currentPoint]
                val newCell = calculateCellContents(currentCell, bugCount)
                line.add(newCell)
            }
            newState.add(line)
        }
        return newState
    }

    private fun calculateCellContents(currentCell: Char, neighbourBugCount: Int) =
        if (currentCell == BUG && neighbourBugCount == 1 || currentCell == SPACE && neighbourBugCount in 1..2) BUG else SPACE

}

private fun Map<Int, CharGrid>.viewMaps(): String =
    entries.sortedBy { it.key }.joinToString(separator = "\n\n") { (key, value) -> "Depth $key:\n${value.viewMap()}" }

private fun CharGrid.viewMap(): String = joinToString(separator = "\n") { it.joinToString(separator = "") }

private fun CharGrid.bugCount(): Int = flatten().count { it == BUG }