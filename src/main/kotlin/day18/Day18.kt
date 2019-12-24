package day18

import helper.Point
import helper.get
import helper.indexOf
import helper.set
import java.util.*

fun solveA(map: List<List<Char>>): Int {
    return TritonMap.buildForA(map).solveA()
}

fun solveB(map: List<List<Char>>, shortcut: Boolean = false): Int {
    return TritonMap.buildForB(map).solveB(shortcut)
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
        val firstStep = SingleRobotStep(entryPositions.first(), emptySet())
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
                        val nextStep = SingleRobotStep(point, currentKeys + cell)
                        visitA(seenSteps, currentPath, nextStep, newDistance)?.let(nodesToVisit::add)
                    } else if (cell.toLowerCase() in currentKeys) {
                        val nextStep = SingleRobotStep(point, currentKeys)
                        visitA(seenSteps, currentPath, nextStep, newDistance)?.let(nodesToVisit::add)
                    }
                }
            }
        }

        val target = nodesToVisit.first()
        return seenSteps.getValue(target.finalStep)
    }

    private fun visitA(
        seenSteps: MutableMap<SingleRobotStep, Int>,
        currentPath: Set<SingleRobotStep>,
        nextStep: SingleRobotStep,
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

    private fun visitB(
        seenSteps: MutableMap<MultiRobotStep, Int>,
        currentPath: Set<SingleRobotStep>,
        nextSingleStep: SingleRobotStep,
        nextMultiStep: MultiRobotStep,
        newDistance: Int
    ): SingleRobotPath? {
        return when {
            nextMultiStep in seenSteps -> {
                val bestDistance = seenSteps.getValue(nextMultiStep)
                seenSteps[nextMultiStep] = minOf(bestDistance, newDistance)
                null
            }
            nextSingleStep !in currentPath -> {
                seenSteps[nextMultiStep] = newDistance
                val newPath = currentPath + nextSingleStep
                SingleRobotPath(nextSingleStep, newPath)
            }
            else -> null
        }
    }

    /**
     * The shorcut can be used if a robot's path strictly does not go outside its quarter
     */
    fun solveB(completeShortcut: Boolean): Int {
        //4 Robots
        //Sharing a keyset
        //Separate / Shared seen steps
        val robots = entryPositions
        val keysByRobot = findRobotKeys()

        val individualFirstSteps = robots.map { SingleRobotStep(it, emptySet()) }
        val firstMultiRobotStep = MultiRobotStep(individualFirstSteps.map { step -> step.point to step }.toMap())
        val seenStepDistances = mutableMapOf(firstMultiRobotStep to 0).toMutableMap()

        val nodesToVisit = sortedSetOf(
            pathComparator(seenStepDistances),
            MultiRobotPath(
                firstMultiRobotStep,
                individualFirstSteps.map { firstStep ->
                    firstStep.point to SingleRobotPath(firstStep, emptySet())
                }.toMap()
            )
        )

        while (nodesToVisit.first().finalKeyCount != allKeys.size) {
            val multiRobotPathNode = nodesToVisit.pollFirst()!!
            val multiRobotStep = multiRobotPathNode.finalStep

            val totalKeys = multiRobotPathNode.collectedKeys
            val currentDistance = seenStepDistances.getValue(multiRobotStep)

            val incompletePaths =
                if (completeShortcut) multiRobotPathNode.incompletePaths(keysByRobot) else multiRobotPathNode.paths
            incompletePaths.forEach { (robot, pathNode) ->
                val currentStep = pathNode.finalStep
                val currentPath = pathNode.path
                val stepKeys = pathNode.collectedKeys

                val neighbours = distanceGraph.getValue(currentStep.point)
                neighbours.forEach { (point, distance) ->
                    val newDistance = currentDistance + distance
                    if (point.y in map.indices && point.x in map[0].indices) {
                        val cell = map[point]
                        if (point.isKey()) {
                            val nextSingleStep = SingleRobotStep(point, stepKeys + cell)
                            val nextMultiStep = multiRobotStep.copyWith(point, nextSingleStep)
                            visitB(seenStepDistances, currentPath, nextSingleStep, nextMultiStep, newDistance)?.let {
                                nodesToVisit.add(multiRobotPathNode.copyWith(robot, it, nextMultiStep))
                            }
                        } else if (cell.toLowerCase() in totalKeys) {
                            val nextSingleStep = SingleRobotStep(point, stepKeys)
                            val nextMultiStep = multiRobotStep.copyWith(point, nextSingleStep)
                            visitB(seenStepDistances, currentPath, nextSingleStep, nextMultiStep, newDistance)?.let {
                                nodesToVisit.add(multiRobotPathNode.copyWith(robot, it, nextMultiStep))
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

    private fun <S : Step> pathComparator(seenSteps: Map<S, Int>): Comparator<Path<S>> {
        return compareBy<Path<S>> { it.length(seenSteps) }
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

interface Path<S : Step> {
    fun length(seenSteps: Map<S, Int>): Int
    val finalKeyCount: Int
        get() = collectedKeys.size
    val collectedKeys: Set<Char>
}

data class SingleRobotPath(val finalStep: SingleRobotStep, val path: Set<SingleRobotStep>) : Path<SingleRobotStep> {
    override fun toString(): String {
        return "Path(finalStep=${finalStep.point}, keys=${finalStep.keys})"
    }

    override fun length(seenSteps: Map<SingleRobotStep, Int>) = seenSteps.getValue(this.finalStep)

    override val collectedKeys: Set<Char>
        get() = finalStep.keys
}

interface Step {
    val keys: Set<Char>
    val keyCount: Int
        get() = keys.size
}

/**
 * A step in the path: A point + a collection of keys
 */
data class SingleRobotStep(val point: Point, override val keys: Set<Char>) : Step {
    override val keyCount = keys.size
}

data class MultiRobotStep(val steps: Map<Point, SingleRobotStep>) : Step {
    override val keys = steps.values.flatMap { it.keys }.toSet()

    fun copyWith(robot: Point, step: SingleRobotStep): MultiRobotStep {
        val newSteps = steps.toMutableMap()
        newSteps[robot] = step
        return MultiRobotStep(steps = newSteps)
    }
}

data class MultiRobotPath(val finalStep: MultiRobotStep, val paths: Map<Point, SingleRobotPath>) :
    Path<MultiRobotStep> {
    operator fun get(point: Point) = paths[point]

    override fun length(seenSteps: Map<MultiRobotStep, Int>): Int {
        return seenSteps.getValue(this.finalStep)
    }

    fun copyWith(point: Point, path: SingleRobotPath, step: MultiRobotStep): MultiRobotPath {
        val newPaths = paths.toMutableMap()
        newPaths[point] = path
        return MultiRobotPath(paths = newPaths, finalStep = step)
    }

    override val collectedKeys: Set<Char>
        get() = paths.values.flatMap { it.collectedKeys }.toSet()

    fun incompletePaths(totalKeys: Map<Point, Set<Char>>): Map<Point, SingleRobotPath> {
        return paths.filter { (key, value) -> totalKeys.getValue(key).any { it !in value.collectedKeys } }
    }
}

