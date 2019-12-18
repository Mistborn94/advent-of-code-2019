package day18

import helper.Point

fun solveA(map: List<List<Char>>): Int {
    val map = Map(map)
    return map.traverse()
}

class Map(val map: List<List<Char>>) {
    val allKeys = map.flatMap { line -> line.filter { it in 'a'..'z' } }.toSet()
    val startingPosition: Point

    init {
        val startingY = map.indexOfFirst { it.contains('@') }
        startingPosition = Point(map[startingY].indexOf('@'), startingY)
    }

    fun traverse(): Int {
        val firstStep = Step(startingPosition, emptySet())
        val seenSteps = mutableSetOf<Step>(firstStep)
//        val paths = mutableMapOf(firstStep to emptySet<Step>())
        val nodesToVisit = linkedSetOf(Path(firstStep, emptySet()))

        var node = nodesToVisit.first()
        while (!node.finalStep.hasAllKeys(allKeys.size)) {
            nodesToVisit.remove(node)
            val currentStep = node.finalStep
            val currentKeys = currentStep.keys
            val currentPath = node.path

            currentStep.point.neighbours().forEach { point ->
                if (point.y in map.indices && point.x in map[0].indices) {
                    val cell = map[point]
                    val isKey = cell in 'a'..'z'
                    val isLockedDoor = cell in 'A'..'Z' && cell.toLowerCase() !in currentKeys
                    if (cell != '#' && !isLockedDoor) {
                        if (isKey && cell !in currentKeys) {
                            val nextKeys = currentKeys + cell
                            val nextStep = Step(point, nextKeys)
                            if (nextStep !in currentPath && nextStep !in seenSteps) {
                                val newPath = currentPath + nextStep
                                seenSteps.add(nextStep)
                                nodesToVisit.add(Path(nextStep, newPath))
                            }
                        } else {
                            val nextStep = Step(point, currentKeys)
                            if (nextStep !in currentPath && nextStep !in seenSteps) {
                                val newPath = currentPath + nextStep
                                seenSteps.add(nextStep)
                                nodesToVisit.add(Path(nextStep, newPath))
                            }
                        }
                    }
                }
            }
            node = nodesToVisit.first()
        }
        return node.length
    }

    data class Step(val point: Point, val keys: Set<Char>) {
        val keyCount = keys.size
        fun hasAllKeys(expectedCount: Int) = keyCount == expectedCount
    }

    data class Path(val finalStep: Step, val path: Set<Step>) {
        val length = path.size
    }
}

private operator fun <E> List<List<E>>.get(point: Point) = this[point.y][point.x]
