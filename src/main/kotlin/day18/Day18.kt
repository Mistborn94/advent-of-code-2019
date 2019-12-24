package day18

import helper.Point
import helper.get
import helper.indexOf
import helper.set
import java.util.*

fun solveA(map: List<List<Char>>): Int {
    return TritonMap.buildForA(map).solveA()
}

fun solveB(map: List<List<Char>>): Int {
    return TritonMap.buildForB(map).solveB()
}

class TritonMap private constructor(
    val map: List<List<Char>>,
    val entryPositions: List<Point>,
    val originalEntry: Point
) {
    val allKeys = map.flatMap { line -> line.filter { it in 'a'..'z' } }.toSet()

    val keysAndDoors = map.indices.flatMap { y ->
        map[y].indices.map { x -> Point(x, y) }.filter { it.isDoor() || it.isKey() }
    }

    val distanceGraph = buildDistanceGraph()

    private fun buildDistanceGraph(): Map<Point, List<Pair<Point, Int>>> {
        //Map of node to neighbouring nodes + distance
        val distancesMap = mutableMapOf<Point, List<Pair<Point, Int>>>()
        val startingPositions = keysAndDoors + entryPositions

        startingPositions.forEach { point ->
            distancesMap[point] = findDistancesToOtherDoorsAndKeys(point)
        }

        return distancesMap
    }

    private fun findDistancesToOtherDoorsAndKeys(startingPoint: Point): List<Pair<Point, Int>> {
        val nodesToTraverse = mutableListOf(startingPoint)
        val seenPoints = mutableMapOf(startingPoint to 0)
        val relevantNodes = mutableListOf<Pair<Point, Int>>()

        while (nodesToTraverse.isNotEmpty()) {
            val currentPoint = nodesToTraverse.removeAt(0)
            val currentDistance = seenPoints.getValue(currentPoint)
            if (currentPoint != startingPoint && (currentPoint.isDoor() || currentPoint.isKey())) {
                relevantNodes.add(currentPoint to currentDistance)
            } else {
                currentPoint.neighbours().forEach { neighbourPoint ->
                    val cell = map[neighbourPoint]
                    if (cell != '#' && neighbourPoint !in seenPoints) {
                        seenPoints[neighbourPoint] = currentDistance + 1
                        nodesToTraverse.add(neighbourPoint)
                    }
                }
            }
        }

        return relevantNodes
    }

    fun solveA(): Int {
        val firstStep = Step(entryPositions.first(), emptySet())
        val seenSteps = mutableMapOf(firstStep to 0)
        val nodesToVisit = sortedSetOf(pathComparator(seenSteps), SingleRobotPath(firstStep, emptySet()))

        while (nodesToVisit.first().finalKeyCount != allKeys.size) {
            val pathNode = nodesToVisit.pollFirst()!!
            val currentStep = pathNode.finalStep
            val currentKeys = currentStep.keys
            val currentPath = pathNode.path
            val currentDistance = seenSteps.getValue(currentStep)

            distanceGraph.getValue(currentStep.point).forEach { (point, distance) ->
                val newDistance = currentDistance + distance
                if (point.y in map.indices && point.x in map[0].indices) {
                    val cell = map[point]
                    if (point.isKey()) {
                        val nextStep = Step(point, currentKeys + cell)
                        visit(seenSteps, currentPath, nextStep, newDistance)?.let(nodesToVisit::add)
                    } else if (cell.toLowerCase() in currentKeys) {
                        val nextStep = Step(point, currentKeys)
                        visit(seenSteps, currentPath, nextStep, newDistance)?.let(nodesToVisit::add)
                    }
                }
            }
        }

        val target = nodesToVisit.first()
        return seenSteps.getValue(target.finalStep)
    }

    private fun visit(
        seenSteps: MutableMap<Step, Int>,
        currentPath: Set<Step>,
        nextStep: Step,
        newDistance: Int
    ): SingleRobotPath? {
        return when (nextStep) {
            in seenSteps -> {
                val bestDistance = seenSteps.getValue(nextStep)
                seenSteps[nextStep] = minOf(bestDistance, newDistance)
                val newPath = currentPath + nextStep
                SingleRobotPath(nextStep, newPath)
                null
            }
            !in currentPath -> {
                seenSteps[nextStep] = newDistance
                val newPath = currentPath + nextStep
                SingleRobotPath(nextStep, newPath)
            }
            else -> null
        }
    }

    fun solveB(): Int {
        //4 Robots
        //Sharing a keyset
        //Separate / Shared seen steps
        val robots = entryPositions
        val keysByRobot = findRobotKeys()

        val individualFirstSteps = robots.map { Step(it, emptySet()) }
        val seenStepDistances = individualFirstSteps.map { step -> step to 0 }.toMap().toMutableMap()

        //Should be a list of multirobot paths
        val nodesToVisit = sortedSetOf(
            pathComparator(seenStepDistances),
            MultiRobotPath(
                individualFirstSteps.map { firstStep ->
                    firstStep.point to SingleRobotPath(firstStep, emptySet())
                }.toMap()
            )
        )

        while (nodesToVisit.first().finalKeyCount != allKeys.size) {
            val multiRobotPathNode = nodesToVisit.pollFirst()!!
            val totalKeys = multiRobotPathNode.collectedKeys

            val incompletePaths = multiRobotPathNode.incompletePaths(keysByRobot)
            incompletePaths.forEach { (robot, pathNode) ->
                val currentStep = pathNode.finalStep
                val currentPath = pathNode.path
                val currentDistance = seenStepDistances.getValue(currentStep)

                val neighbours = distanceGraph.getValue(currentStep.point)
                neighbours.forEach { (point, distance) ->
                    val newDistance = currentDistance + distance
                    if (point.y in map.indices && point.x in map[0].indices) {
                        val cell = map[point]
                        if (point.isKey()) {
                            val nextStep = Step(point, totalKeys + cell)
                            visit(seenStepDistances, currentPath, nextStep, newDistance)?.let {
                                nodesToVisit.add(multiRobotPathNode.copyWith(robot, it))
                            }
                        } else if (cell.toLowerCase() in totalKeys) {
                            val nextStep = Step(point, totalKeys)
                            visit(seenStepDistances, currentPath, nextStep, newDistance)?.let {
                                nodesToVisit.add(multiRobotPathNode.copyWith(robot, it))
                            }
                        }
                    }
                }
            }
        }

        return nodesToVisit.first().length(seenStepDistances)
    }

    private fun findRobotKeys(): Map<Point, Set<Char>> {

        return map.indices.flatMap { y -> map[0].indices.map { x -> Point(x, y) } }
            .filter { it.isKey() }
            .groupBy { (x, y) -> getRobotKey(x, y) }
            .mapValues { (_, value) -> value.map { map[it] }.toSet() }
    }

    private fun getRobotKey(x: Int, y: Int): Point {
        val xComponent = if (x < originalEntry.x) -1 else 1
        val yComponent = if (y < originalEntry.y) -1 else 1
        return originalEntry + Point(xComponent, yComponent)
    }

    private fun Point.isKey() = map[this] in 'a'..'z'
    private fun Point.isDoor() = map[this] in 'A'..'Z'

    private fun pathComparator(seenSteps: Map<Step, Int>): Comparator<Path> {
        return compareBy<Path> { it.length(seenSteps) }
            .thenByDescending { it.finalKeyCount }
            .thenBy { it.hashCode() }
    }

    companion object {
        fun buildForA(map: List<List<Char>>): TritonMap {
            val entryPosition = map.indexOf('@')
            return TritonMap(map, listOf(entryPosition), entryPosition)
        }

        fun buildForB(map: List<List<Char>>): TritonMap {
            val originalStart = map.indexOf('@')
            val mutableMap = map.map { it.toMutableList() }.toMutableList()
            val newStarts = listOf(
                originalStart + Point(1, 1),
                originalStart + Point(1, -1),
                originalStart + Point(-1, -1),
                originalStart + Point(-1, 1)
            )
            val newWalls = listOf(
                originalStart,
                originalStart + Point(0, 1),
                originalStart + Point(0, -1),
                originalStart + Point(1, 0),
                originalStart + Point(-1, 0)
            )

            newStarts.forEach { mutableMap[it] = '@' }
            newWalls.forEach { mutableMap[it] = '#' }

            return TritonMap(mutableMap, newStarts, originalStart)
        }
    }
}

interface Path {
    fun length(seenSteps: Map<Step, Int>): Int
    val finalKeyCount: Int
    val collectedKeys: Set<Char>
}

data class SingleRobotPath(val finalStep: Step, val path: Set<Step>) : Path {
    override fun toString(): String {
        return "Path(finalStep=${finalStep.point}, keys=${finalStep.keys})"
    }

    override fun length(seenSteps: Map<Step, Int>) = seenSteps.getValue(this.finalStep)

    override val finalKeyCount: Int
        get() = finalStep.keyCount
    override val collectedKeys: Set<Char>
        get() = finalStep.keys
}

/**
 * A step in the path: A point + a collection of keys
 */
data class Step(val point: Point, val keys: Set<Char>) {
    val keyCount = keys.size
}

data class MultiRobotPath(val paths: Map<Point, SingleRobotPath>) : Path {
    operator fun get(point: Point) = paths[point]

    override fun length(seenSteps: Map<Step, Int>): Int {
        return paths.values.sumBy { it.length(seenSteps) }
    }

    fun copyWith(point: Point, path: SingleRobotPath): MultiRobotPath {
        val newPaths = paths.toMutableMap()
        newPaths[point] = path
        return copy(paths = newPaths)
    }

    override val finalKeyCount: Int
        get() = paths.values.flatMap { it.collectedKeys }.toSet().size
    override val collectedKeys: Set<Char>
        get() = paths.values.flatMap { it.collectedKeys }.toSet()

    fun incompletePaths(totalKeys: Map<Point, Set<Char>>): Map<Point, SingleRobotPath> {
        return paths.filter { (key, value) -> totalKeys.getValue(key).any { it !in value.collectedKeys } }
    }
}

