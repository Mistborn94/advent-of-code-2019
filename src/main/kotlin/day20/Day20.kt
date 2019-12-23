package day20

import helper.Point

enum class Mode {
    INNER,
    OUTER
}

data class Portal(val name: String, val mode: Mode, val position: Point)

class Pluto(val map: List<String>) {

    private val portalsByPosition: Map<Point, Portal>
    private val portalsByName: Map<String, List<Portal>>

    private val donutWidth: Int = findDonutSize(map[map.size / 2])
    private val donutHeight: Int = run {
        val middle = map[0].length / 2
        val column = map.map { it[middle] }.joinToString(separator = "")

        findDonutSize(column)
    }

    init {
        val (portalsByName, portalsByPosition) = findLabels()
        this.portalsByName = portalsByName
        this.portalsByPosition = portalsByPosition
    }

    private fun findLabels(): Pair<Map<String, MutableList<Portal>>, Map<Point, Portal>> {
        val portalsByLabel = mutableMapOf<String, MutableList<Portal>>()
        val portalsByPosition = mutableMapOf<Point, Portal>()

        val holeWidth = map[0].length - 2 * donutWidth
        val lastInnerX = donutWidth + holeWidth - 1

        val holeHeight = map.size - 2 * donutHeight
        val lastInnerY = donutHeight + holeHeight - 1

        sequenceOf(
            //Top and Bottom row labels
            findLabelsInRow(0, 2, Mode.OUTER),
            findLabelsInRow(map.size - 2, map.size - 3, Mode.OUTER),
            // Inner Row Labels
            findLabelsInRow(donutHeight, donutHeight - 1, Mode.INNER, donutWidth - 1),
            findLabelsInRow(lastInnerY - 1, lastInnerY + 1, Mode.INNER, donutWidth - 1),
            //First and last column labels
            findLabelsInColumn(0, 2, Mode.OUTER),
            findLabelsInColumn(map[0].length - 2, map[0].length - 3, Mode.OUTER),
            // Inner Column labels
            findLabelsInColumn(donutWidth, donutWidth - 1, Mode.INNER, donutHeight - 1),
            findLabelsInColumn(lastInnerX - 1, lastInnerX + 1, Mode.INNER, donutHeight - 1)
        ).flatten().forEach { label ->
            portalsByPosition[label.position] = label
            portalsByLabel.getOrPut(label.name) { mutableListOf() }.add(label)
        }

        return Pair(portalsByLabel, portalsByPosition)
    }


    fun solveA(): Int {
        val startingPoint = portalsByName.getValue("AA")[0].position
        val endingPoint = portalsByName.getValue("ZZ")[0].position

        val nodesToTraverse = linkedSetOf(startingPoint)
        val visitedNodes = mutableMapOf(startingPoint to 0)

        while (visitedNodes[endingPoint] == null) {
            val currentNode = nodesToTraverse.first()
            nodesToTraverse.remove(currentNode)
            val currentDistance = visitedNodes.getValue(currentNode)

            currentNode.neighbours().forEach {
                if (map[it] == '.' && it !in visitedNodes) {
                    visitedNodes[it] = currentDistance + 1
                    nodesToTraverse.add(it)
                }
            }

            val currentPortal = portalsByPosition[currentNode]
            val currentLabel = currentPortal?.name
            if (currentLabel != null && currentLabel !in setOf("AA", "ZZ")) {
                val teleportTo = portalsByName.getValue(currentLabel).first { it.position != currentNode }.position
                if (teleportTo !in visitedNodes) {
                    visitedNodes[teleportTo] = currentDistance + 1
                    nodesToTraverse.add(teleportTo)
                }
            }
        }

        return visitedNodes.getValue(endingPoint)
    }

    private fun findDonutSize(sequence: CharSequence): Int {
        val subSequence = sequence.substring(2, sequence.length - 2)

        return subSequence.indexOfFirst { it !in setOf('.', '#') } + 2
    }

    private fun findLabelsInRow(
        firstLabelRow: Int,
        cellRow: Int,
        mode: Mode,
        offset: Int = 0
    ): Sequence<Portal> {
        return sequence {
            val row = map[firstLabelRow]
            row.substring(offset, row.length - offset).forEachIndexed { index, c ->
                val x = index + offset
                val second = map[firstLabelRow + 1][x]
                if (c in 'A'..'Z' && second in 'A'..'Z') {
                    val label = c.toString() + second
                    val position = Point(x, cellRow)
                    yield(Portal(label, mode, position))
                }
            }
        }
    }

    private fun findLabelsInColumn(
        firstLabelColumn: Int,
        cellColumn: Int,
        mode: Mode,
        offset: Int = 0
    ): Sequence<Portal> {
        return sequence {
            map.subList(offset, map.size - offset).forEachIndexed { rowIndex, row ->
                val y = rowIndex + offset
                val c = row[firstLabelColumn]
                val second = map[y][firstLabelColumn + 1]
                if (c in 'A'..'Z' && second in 'A'..'Z') {
                    val label = c.toString() + second
                    val position = Point(cellColumn, y)
                    yield(Portal(label, mode, position))
                }
            }
        }
    }

}

private operator fun List<String>.get(point: Point) = this[point.y][point.x]