package day18

import helper.Point

fun solveA(map: List<List<Char>>): Int {
    return TritonMap(map).traverse()
}

class TritonMap(val map: List<List<Char>>) {
    val allKeys = map.flatMap { line -> line.filter { it in 'a'..'z' } }.toSet()
    val allDoors = map.flatMap { line -> line.filter { it in 'A'..'Z' } }.toSet()

    val entryPosition: Point = indexOf('@')
    val keysAndDoors = map.indices.flatMap { y ->
        map[y].indices.map { x -> Point(x, y) }.filter { it.isDoor() || it.isKey() }
    }

    val distanceGraph = buildDistanceGraph()

    private fun buildDistanceGraph(): Map<Point, List<Pair<Point, Int>>> {
        //Map of node to neighbouring nodes + distance
        val distancesMap = mutableMapOf<Point, List<Pair<Point, Int>>>()
        val startingPositions = keysAndDoors + entryPosition

        startingPositions.forEach { point ->
            distancesMap[point] = findDistancesToOthers(point)
        }

        return distancesMap
    }

    private fun findDistancesToOthers(startingPoint: Point): List<Pair<Point, Int>> {
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

    private fun indexOf(char: Char): Point {
        val startingY = map.indexOfFirst { it.contains(char) }
        return Point(map[startingY].indexOf(char), startingY)
    }

    fun traverse(): Int {
        val mappedDistanceGraph = distanceGraph.mapKeys { (key, _) -> map[key] }
            .mapValues { (_, values) -> values.map { (point, distance) -> map[point] } }
        val firstStep = Step(entryPosition, emptySet())
        val seenSteps = mutableSetOf(firstStep)
        val nodesToVisit = mutableSetOf(Path(firstStep, emptySet(), 0))

        var pathNode = nodesToVisit.minBy { it.length }!!
        while (!pathNode.finalStep.hasAllKeys(allKeys.size)) {
            nodesToVisit.remove(pathNode)
            val currentStep = pathNode.finalStep
            val currentKeys = currentStep.keys
            val currentPath = pathNode.path

            distanceGraph.getValue(currentStep.point).forEach { (point, distance) ->
                val newDistance = pathNode.length + distance
                if (point.y in map.indices && point.x in map[0].indices) {
                    val cell = map[point]

                    if (point.isKey()) {
                        val nextKeys = currentKeys + cell
                        val nextStep = Step(point, nextKeys)
                        if (nextStep !in currentPath && nextStep !in seenSteps) {
                            val newPath = currentPath + nextStep
                            seenSteps.add(nextStep)
                            nodesToVisit.add(Path(nextStep, newPath, newDistance))
                        }
                    } else if (cell.toLowerCase() in currentKeys) {
                        val nextStep = Step(point, currentKeys)
                        if (nextStep !in currentPath && nextStep !in seenSteps) {
                            val newPath = currentPath + nextStep
                            seenSteps.add(nextStep)
                            nodesToVisit.add(Path(nextStep, newPath, newDistance))
                        }
                    }
                }
            }
            pathNode = nodesToVisit.minBy { it.length }!!
        }
        return pathNode.length
    }

    data class Step(val point: Point, val keys: Set<Char>) {
        val keyCount = keys.size
        fun hasAllKeys(expectedCount: Int) = keyCount == expectedCount
    }

    data class Path(val finalStep: Step, val path: Set<Step>, val length: Int)

    private fun Point.isKey() = map[this] in 'a'..'z'
    private fun Point.isDoor() = map[this] in 'A'..'Z'
}

private operator fun <E> List<List<E>>.get(point: Point) = this[point.y][point.x]
