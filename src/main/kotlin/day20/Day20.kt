package day20

import helper.Point

enum class Mode {
    INNER,
    OUTER
}

data class Portal(val name: String, val mode: Mode, val position: Point)

fun solveA(lines: List<String>): Int {

    val (portalsByName, portalsByPosition) = findLabels(lines)

    val startingPoint = portalsByName.getValue("AA")[0].position
    val endingPoint = portalsByName.getValue("ZZ")[0].position

    val nodesToTraverse = linkedSetOf(startingPoint)
    val visitedNodes = mutableMapOf(startingPoint to 0)
    val previousPoints = mutableMapOf<Point, Point>()

    while (visitedNodes[endingPoint] == null) {
        val currentNode = nodesToTraverse.first()
        nodesToTraverse.remove(currentNode)
        val currentDistance = visitedNodes.getValue(currentNode)

        currentNode.neighbours().forEach {
            if (lines[it] == '.' && it !in visitedNodes) {
                visitedNodes[it] = currentDistance + 1
                nodesToTraverse.add(it)
                previousPoints[it] = currentNode
            }
        }

        val currentPortal = portalsByPosition[currentNode]
        val currentLabel = currentPortal?.name
        if (currentLabel != null && currentLabel !in setOf("AA", "ZZ")) {
            val teleportTo = portalsByName.getValue(currentLabel).first { it.position != currentNode }.position
            if (teleportTo !in visitedNodes) {
                visitedNodes[teleportTo] = currentDistance + 1
                nodesToTraverse.add(teleportTo)
                previousPoints[teleportTo] = currentNode
            }
        }
    }

    println(buildPath(endingPoint, previousPoints))

    return visitedNodes.getValue(endingPoint)
}

fun buildPath(endingPoint: Point, previousPoints: Map<Point, Point>): List<Point> {
    val path = mutableListOf<Point>()
    var currentPoint: Point? = endingPoint;
    while (currentPoint != null) {
        path.add(currentPoint)
        currentPoint = previousPoints[currentPoint]
    }
    return path.reversed()
}

private fun findLabels(
    lines: List<String>
): Pair<Map<String, MutableList<Portal>>, Map<Point, Portal>> {
    val labels = mutableMapOf<String, MutableList<Portal>>()
    val labelledPositions = mutableMapOf<Point, Portal>()

    val donutWidth = findDonutWidth(lines) + 2
    val holeWidth = lines[0].length - 2 * donutWidth
    val lastInnerX = donutWidth + holeWidth - 1

    val donutHeight = findDonutHeight(lines) + 2
    val holeHeight = lines.size - 2 * donutHeight
    val lastInnerY = donutHeight + holeHeight - 1

    sequenceOf(
        //Top row labels
        findLabelsInRow(lines, 0, 2, Mode.OUTER),
        //bottom row labels
        findLabelsInRow(lines, lines.size - 2, lines.size - 3, Mode.OUTER),
        // Inner Labels
        findLabelsInRow(lines, donutHeight, donutHeight - 1, Mode.INNER, donutWidth - 1),
        findLabelsInRow(lines, lastInnerY - 1, lastInnerY + 1, Mode.INNER, donutWidth - 1),
        //First column labels
        findLabelsInColumn(lines, 0, 2, Mode.OUTER),
        //Last column labels
        findLabelsInColumn(lines, lines[0].length - 2, lines[0].length - 3, Mode.OUTER),
        // Inner Column labels
        findLabelsInColumn(lines, donutWidth, donutWidth - 1, Mode.INNER, donutHeight - 1),
        findLabelsInColumn(lines, lastInnerX - 1, lastInnerX + 1, Mode.INNER, donutHeight - 1)
    ).flatten().forEach { label ->
        labelledPositions[label.position] = label
        labels.getOrPut(label.name) { mutableListOf() }.add(label)
    }

    return Pair(labels, labelledPositions)
}

fun findDonutHeight(lines: List<String>): Int {
    val middle = lines[0].length / 2
    val column = lines.map { it[middle] }.joinToString(separator = "")

    return findDonutSize(column)
}

private fun findDonutWidth(lines: List<String>): Int {
    return findDonutSize(lines[lines.size / 2])
}

private fun findDonutSize(sequence: CharSequence): Int {
    val subSequence = sequence.subSequence(2, sequence.length - 2)

    return subSequence.indexOfFirst { it !in setOf('.', '#') }
}

private fun findLabelsInRow(
    lines: List<String>,
    firstLabelRow: Int,
    cellRow: Int,
    mode: Mode,
    offset: Int = 0
): Sequence<Portal> {
    return sequence {
        val row = lines[firstLabelRow]
        row.subSequence(offset, row.length - offset).forEachIndexed { index, c ->
            val x = index + offset
            val second = lines[firstLabelRow + 1][x]
            if (c in 'A'..'Z' && second in 'A'..'Z') {
                val label = c.toString() + second
                val position = Point(x, cellRow)
                yield(Portal(label, mode, position))
            }
        }
    }
}

private fun findLabelsInColumn(
    lines: List<String>,
    firstLabelColumn: Int,
    cellColumn: Int,
    mode: Mode,
    offset: Int = 0
): Sequence<Portal> {
    return sequence {
        lines.subList(offset, lines.size - offset).forEachIndexed { rowIndex, row ->
            val y = rowIndex + offset
            val c = row[firstLabelColumn]
            val second = lines[y][firstLabelColumn + 1]
            if (c in 'A'..'Z' && second in 'A'..'Z') {
                val label = c.toString() + second
                val position = Point(cellColumn, y)
                yield(Portal(label, mode, position))
            }
        }
    }
}

private operator fun List<String>.get(point: Point) = this[point.y][point.x]