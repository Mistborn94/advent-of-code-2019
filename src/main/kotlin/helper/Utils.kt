package helper

import java.util.ArrayList

fun <T> ArrayList<T>.resize(minimumSize: Int, defaultValue: T) {
    if (minimumSize < 0) {
        throw IllegalArgumentException("Negative sizes not allowed")
    }
    ensureCapacity(minimumSize)
    while (size < minimumSize) {
        add(defaultValue)
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
}