package day20

import helper.Point

fun solveA(lines: List<String>): Int {

    val labels = mutableMapOf<String, MutableList<Point>>()
    val labelledPositions = mutableMapOf<Point, String>()

    populateAllLabels(lines, labelledPositions, labels)

    val startingPoint = labels.getValue("AA")[0]
    val endingPoint = labels.getValue("ZZ")[0]

    val nodesToTraverse = linkedSetOf(startingPoint)
    val visitedNodes = mutableMapOf(startingPoint to 0)

    while (visitedNodes[endingPoint] == null) {
        val currentNode = nodesToTraverse.first()
        nodesToTraverse.remove(currentNode)
        val currentDistance = visitedNodes.getValue(currentNode)

        currentNode.neighbours().forEach {
            if (lines[it] == '.' && it !in visitedNodes) {
                visitedNodes[it] = currentDistance + 1
                nodesToTraverse.add(it)
            }
        }

        val currentLabel = labelledPositions[currentNode]
        if (currentLabel != null && currentLabel !in setOf("AA", "ZZ")) {
            val teleportTo = labels.getValue(currentLabel).first { it != currentNode }
            if (teleportTo !in visitedNodes) {
                visitedNodes[teleportTo] = currentDistance + 1
                nodesToTraverse.add(teleportTo)
            }
        }
    }

    return visitedNodes.getValue(endingPoint)
}

private fun populateAllLabels(
    lines: List<String>,
    labelledPositions: MutableMap<Point, String>,
    labels: MutableMap<String, MutableList<Point>>
) {
    val donutWidth = findDonutWidth(lines)
    val holeWidth = lines[0].length - 2 * donutWidth
    val lastInnerX = donutWidth + holeWidth - 1

    val donutHeight = findDonutHeight(lines)
    val holeHeight = lines.size - 2 * donutHeight
    val lastInnerY = donutHeight + holeHeight - 1

    sequenceOf(
        //Top row labels
        findLabelsInRow(lines, 0, 2),
        //bottom row labels
        findLabelsInRow(lines, lines.size - 2, lines.size - 3),
        // Inner Labels
        findLabelsInRow(lines, donutHeight, donutHeight - 1, donutWidth - 1),
        findLabelsInRow(lines, lastInnerY - 1, lastInnerY + 1, donutWidth - 1),
        //First column labels
        findLabelsInColumn(lines, 0, 2),
        //Last column labels
        findLabelsInColumn(lines, lines[0].length - 2, lines[0].length - 3),
        // Inner Column labels
        findLabelsInColumn(lines, donutWidth, donutWidth - 1, donutHeight - 1),
        findLabelsInColumn(lines, lastInnerX - 1, lines[0].length - 3, donutHeight - 1)
    ).flatten().forEach { (label, position) ->
        labelledPositions[position] = label
        labels.getOrPut(label) { mutableListOf() }.add(position)
    }
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

    return subSequence.indexOfFirst { it !in setOf('.', '#') } + 2
}

private fun findLabelsInRow(
    lines: List<String>,
    firstLabelRow: Int,
    cellRow: Int,
    offset: Int = 0
): Sequence<Pair<String, Point>> {
    return sequence {
        val row = lines[firstLabelRow]
        row.subSequence(offset, row.length - offset).forEachIndexed { index, c ->
            val x = index + offset
            val second = lines[firstLabelRow + 1][x]
            if (c in 'A'..'Z' && second in 'A'..'Z') {
                val label = c.toString() + second
                val position = Point(x, cellRow)
                yield(label to position)
            }
        }
    }
}

private fun findLabelsInColumn(
    lines: List<String>,
    firstLabelColumn: Int,
    cellColumn: Int,
    offset: Int = 0
): Sequence<Pair<String, Point>> {
    return sequence {
        lines.subList(offset, lines.size - offset).forEachIndexed { rowIndex, row ->
            val c = row[firstLabelColumn]
            val y = rowIndex + offset
            if (c in 'A'..'Z') {
                val label = c.toString() + lines[y][firstLabelColumn + 1]
                val position = Point(cellColumn, y)
                yield(label to position)
            }
        }
    }
}

private operator fun List<String>.get(point: Point) = this[point.y][point.x]