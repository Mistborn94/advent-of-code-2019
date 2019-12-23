package day18

import helper.Point
import helper.get
import helper.indexOf
import helper.set
import java.util.*

fun solveA(map: List<List<Char>>): Int {
    return TritonMap.buildForA(map).solveA()
}

class TritonMap private constructor(val map: List<List<Char>>, val entryPositions: Set<Point>) {
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
        val nodesToVisit = sortedSetOf(pathComparatorA(seenSteps), Path(firstStep, emptySet()))

        while (!nodesToVisit.first().hasAllKeys()) {
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
                        visit(seenSteps, currentPath, nodesToVisit, nextStep, newDistance)
                    } else if (cell.toLowerCase() in currentKeys) {
                        val nextStep = Step(point, currentKeys)
                        visit(seenSteps, currentPath, nodesToVisit, nextStep, newDistance)
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
        nodesToVisit: TreeSet<Path>,
        nextStep: Step,
        newDistance: Int
    ) {
        if (nextStep in seenSteps) {
            val bestDistance = seenSteps.getValue(nextStep)
            seenSteps[nextStep] = minOf(bestDistance, newDistance)
        } else if (nextStep !in currentPath) {
            val newPath = currentPath + nextStep
            seenSteps[nextStep] = newDistance
            nodesToVisit.add(Path(nextStep, newPath))
        }
    }

    fun solveB(): Int {
        //4 Robots
        //Sharing a keyset
        //Separate /Shared seen steps
        val firstSteps = entryPositions.map { Step(it, emptySet()) }.toList()
        val seenStepDistances = firstSteps.map { it to 0 }.toMap().toMutableMap()
        val nodesToVisit = firstSteps.mapIndexed { i, firstStep ->
            i to sortedSetOf(pathComparatorA(seenStepDistances), Path(firstStep, emptySet()))
        }.toMap()

        val nonReadyPaths = (0 until 4).map { it to mutableSetOf<Path>() }

        while (totalNextKeys(nodesToVisit) != allKeys) {

        }

        return totalNextKeys(nodesToVisit)

        TODO("Implement")
    }

    private fun Point.isKey() = map[this] in 'a'..'z'
    private fun Point.isDoor() = map[this] in 'A'..'Z'

    private fun Path.hasAllKeys(): Boolean = this.finalStep.hasAllKeys(allKeys.size)

    private fun pathComparatorA(seenSteps: Map<Step, Int>): Comparator<Path> {
        return compareBy<Path> { it.length(seenSteps) }
            .thenByDescending { it.finalStep.keyCount }
            .thenBy { it.hashCode() }
    }

    companion object {
        fun buildForA(map: List<List<Char>>): TritonMap = TritonMap(map, setOf(map.indexOf('@')))
        fun buildForB(map: List<List<Char>>): TritonMap {
            val originalStart = map.indexOf('@')
            val mutableMap = map.map { it.toMutableList() }.toMutableList()
            val newStarts = setOf(
                originalStart + Point(1, 1),
                originalStart + Point(1, -1),
                originalStart + Point(-1, 1),
                originalStart + Point(-1, -1)
            )
            val newWalls = setOf(
                originalStart,
                originalStart + Point(0, 1),
                originalStart + Point(0, -1),
                originalStart + Point(1, 0),
                originalStart + Point(-1, 0)
            )

            newStarts.forEach { mutableMap[it] = '@' }
            newWalls.forEach { mutableMap[it] = '#' }

            return TritonMap(mutableMap, newWalls)
        }
    }
}

private fun Step.isReady(ownedKeys: Set<Char>): Boolean {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}

data class Path(val finalStep: Step, val path: Set<Step>) {
    override fun toString(): String {
        return "Path(finalStep=${finalStep.point}, keys=${finalStep.keys})"
    }

    fun length(seenSteps: Map<Step, Int>) = seenSteps.getValue(this.finalStep)
}

data class Step(val point: Point, val keys: Set<Char>) {
    val keyCount = keys.size
    fun hasAllKeys(expectedCount: Int) = keyCount == expectedCount
}
