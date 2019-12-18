package helper

import java.util.*

fun <T> ArrayList<T>.resize(minimumSize: Int, supplier: () -> T) {
    if (minimumSize < 0) {
        throw IllegalArgumentException("Negative sizes not allowed")
    }
    ensureCapacity(minimumSize)
    while (size < minimumSize) {
        add(supplier())
    }
}

data class Point(val x: Int, val y: Int) {

    fun abs(): Int {
        return kotlin.math.abs(x) + kotlin.math.abs(y)
    }

    operator fun minus(other: Point): Point =
        Point(x - other.x, y - other.y)

    operator fun plus(other: Point): Point =
        Point(x + other.x, y + other.y)

    fun neighbours() = listOf(
        Point(x + 1, y),
        Point(x - 1, y),
        Point(x, y + 1),
        Point(x, y - 1)
    )
}