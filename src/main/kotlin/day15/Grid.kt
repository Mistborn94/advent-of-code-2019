package day15

import helper.Point
import helper.resize
import kotlin.math.abs
import kotlin.math.max

class Grid<T> {
    var topRight = arrayListOf<ArrayList<T?>>()
        private set
    var bottomRight = arrayListOf<ArrayList<T?>>()
        private set
    var topLeft = arrayListOf<ArrayList<T?>>()
        private set
    var bottomLeft = arrayListOf<ArrayList<T?>>()
        private set

    operator fun get(point: Point) = get(point.x, point.y)

    operator fun get(x: Int, y: Int): T? {
        val resolvedX = resolveIndex(x)
        val resolvedY = resolveIndex(y)
        val targetList = resolveList(x, y)

        if (resolvedY !in targetList.indices || resolvedX !in targetList[resolvedY].indices) {
            return null
        }
        return targetList[resolvedY][resolvedX]
    }

    operator fun set(point: Point, value: T) {
        set(point.x, point.y, value)
    }

    operator fun set(x: Int, y: Int, value: T) {
        val resolvedX = resolveIndex(x)
        val resolvedY = resolveIndex(y)
        val targetList = resolveList(x, y)
        resizeTargetList(targetList, resolvedX, resolvedY)

        targetList[resolvedY][resolvedX] = value
    }

    private fun resizeTargetList(targetList: ArrayList<ArrayList<T?>>, resolvedX: Int, resolvedY: Int) {
        if (resolvedY !in targetList.indices) {
            targetList.resize(resolvedY + 1) { arrayListOf() }
        }

        val row = targetList[resolvedY]
        if (resolvedX !in row.indices) {
            row.resize(resolvedX + 1) { null }
        }
    }

    private fun resolveList(x: Int, y: Int): ArrayList<ArrayList<T?>> {
        return if (x < 0) {
            if (y < 0) bottomLeft else topLeft
        } else {
            if (y < 0) bottomRight else topRight
        }
    }

    private fun resolveIndex(i: Int): Int {
        return if (i < 0) abs(i) - 1 else i
    }

    override fun toString(): String {
        val bottomLeft = bottomLeft.deepCopy()
        val bottomRight = bottomRight.deepCopy()
        val topLeft = topLeft.deepCopy()
        val topRight = topRight.deepCopy()

        equalizeYLists(bottomLeft, bottomRight)
        equalizeYLists(topLeft, topRight)
        equalizeXLists(topLeft, bottomLeft)
        equalizeXLists(topRight, bottomRight)

        return buildString {
            for (i in topLeft.indices.reversed()) {
                append(topLeft[i].reversed().joinToString(separator = "", prefix = "|") { it?.toString() ?: " " })
                append(topRight[i].joinToString(separator = "", postfix = "|") { it?.toString() ?: " " })
                append("\n");
            }

            for (i in bottomLeft.indices) {
                append(bottomLeft[i].reversed().joinToString(separator = "", prefix = "|") { it?.toString() ?: " " })
                append(bottomRight[i].joinToString(separator = "", postfix = "|") { it?.toString() ?: " " })
                append("\n");
            }
        }
    }

    private fun ArrayList<ArrayList<T?>>.deepCopy() = mapTo(ArrayList()) { ArrayList(it) }

    private fun equalizeXLists(
        listA: ArrayList<ArrayList<T?>>,
        listB: ArrayList<ArrayList<T?>>
    ) {
        val size = (listA + listB).map { it.size }.max() ?: 0
        listA.forEach {
            it.resize(size) { null }
        }
        listB.forEach {
            it.resize(size) { null }
        }
    }

    private fun equalizeYLists(
        listA: ArrayList<ArrayList<T?>>,
        listB: ArrayList<ArrayList<T?>>
    ) {
        val size = max(listA.size, listB.size)
        listA.resize(size) { arrayListOf() }
        listB.resize(size) { arrayListOf() }
    }
}