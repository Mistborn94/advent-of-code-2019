package day12

import java.lang.Integer.max
import java.lang.Integer.min
import kotlin.math.abs

interface XYZ {
    val x: Int
    val y: Int
    val z: Int
}

data class Point(override val x: Int, override val y: Int, override val z: Int) : XYZ {
    operator fun plus(vector: Vector) = Point(x + vector.x, y + vector.y, z + vector.z)
    fun magnitude(): Int = abs(x) + abs(y) + abs(z)

    override fun toString(): String {
        return "(%3d,%3d,%3d)".format(x, y, z)
    }
}

data class Vector(override val x: Int, override val y: Int, override val z: Int) : XYZ {
    operator fun plus(other: Vector) = Vector(x + other.x, y + other.y, z + other.z)
    fun magnitude(): Int = abs(x) + abs(y) + abs(z)

    override fun toString(): String {
        return "<%3d,%3d,%3d>".format(x, y, z)
    }

    companion object {
        val ZERO = Vector(0, 0, 0)
    }
}

operator fun StringBuilder.plusAssign(chars: CharSequence) {
    this.append(chars)
}

class Pattern {

    var size = 0
        private set
    var searchIndex = 0

    var solved = false
        private set
    private var currentStart = StringBuilder()
    private var currentPattern = StringBuilder()

    private var finalPattern: String? = null

    fun add(item: Any) {
        if (!solved) {
            size += 1
            val itemPattern = "$item,"
            currentPattern += itemPattern

            if (!matchContinues(itemPattern)) {
                while (!currentStart.startsWith(currentPattern)) {
                    val index = currentPattern.indexOf(",")
                    currentStart += currentPattern.substring(0, index + 1)

                    if (currentPattern.length > index + 1) {
                        currentPattern = StringBuilder(currentPattern.substring(index + 1))
                    } else {
                        currentPattern.clear()
                    }
                }
                searchIndex = currentPattern.length
            } else {
                searchIndex += itemPattern.length
                if (currentPattern.isNotEmpty() && currentStart.toString() == currentPattern.toString()) {
                    solved = true
                    finalPattern = currentPattern.toString()
                    size /= 2
                    println("Found Pattern with size $size")
                }
            }
        }
    }

    private fun matchContinues(itemPattern: String): Boolean {
        val end = searchIndex + itemPattern.length
        return currentStart.length > end && currentStart.substring(searchIndex, end) == itemPattern
    }
}

class PatternSet {
    val x = Pattern()
    val y = Pattern()
    val z = Pattern()

    val solved: Boolean
        get() = x.solved && y.solved && z.solved

    fun add(values: XYZ) {
        x.add(values.x)
        y.add(values.y)
        z.add(values.z)
    }
}

class Moon(val id: Int, var position: Point, var velocity: Vector = Vector.ZERO) {

    private val potentialEnergy: Int
        get() = position.magnitude()
    private val kineticEnergy: Int
        get() = velocity.magnitude()

    val positionPatterns = PatternSet()
    private val velocityPatterns = PatternSet()

    val energy: Int
        get() = potentialEnergy * kineticEnergy

    fun updatePatterns() {
        positionPatterns.add(position)
    }

    fun applyGravity(other: Moon) {
        val x = calculateChange(position.x, other.position.x)
        val y = calculateChange(position.y, other.position.y)
        val z = calculateChange(position.z, other.position.z)

        this.velocity += Vector(x, y, z)
    }

    fun move() {
        position += velocity
    }

    private fun calculateChange(currentPosition: Int, otherPosition: Int) =
        min(1, max(-1, otherPosition - currentPosition))

    override fun toString(): String {
        return "$position\t.\t$velocity"
    }

    val hasAllPatterns: Boolean
        get() = positionPatterns.solved // && velocityPatterns.solved
}

val pattern = "<x=([0-9-]+), y=([0-9-]+), z=([0-9-]+)>".toRegex()

fun solveA(lines: List<String>, steps: Int): Int {
    val moons = buildMoons(lines)

    val moonPairs = getMoonPairs(moons)

    repeat(steps) {
        runStep(moonPairs, moons)
    }

    return moons.sumBy { it.energy }
}

fun solveB(lines: List<String>): Long {
    val moons = buildMoons(lines)
    val moonPairs = getMoonPairs(moons)

    while (!moons.all { it.hasAllPatterns }) {
        runStep(moonPairs, moons)
    }

    val maxX = moons.map { it.positionPatterns.x.size }.max()!!
    val maxY = moons.map { it.positionPatterns.y.size }.max()!!
    val maxZ = moons.map { it.positionPatterns.z.size }.max()!!

    return lowestCommonMultiple(maxX, maxY, maxZ)
}

fun lowestCommonMultiple(x: Int, y: Int, z: Int): Long {
    val xy = lowestCommonMultiple(x.toLong(), y.toLong())
    val yz = lowestCommonMultiple(y.toLong(), z.toLong())
    return lowestCommonMultiple(xy, yz)
}

fun lowestCommonMultiple(a: Long, b: Long): Long {
    return a * b / greatestCommonDenominator(a, b)
}

tailrec fun greatestCommonDenominator(a: Long, b: Long): Long {
    return when {
        a == 0L -> b
        b == 0L -> a
        else -> {
            val first = maxOf(a, b)
            val second = minOf(a, b)
            greatestCommonDenominator(second, first % second)
        }
    }
}

private fun runStep(
    moonPairs: List<Pair<Moon, Moon>>, moons: List<Moon>
) {
    moons.forEach {
        it.updatePatterns()
    }
    moonPairs.forEach { (a, b) ->
        a.applyGravity(b)
        b.applyGravity(a)
    }
    moons.forEach {
        it.move()
    }
}

private fun getMoonPairs(moons: List<Moon>): List<Pair<Moon, Moon>> {
    return moons.mapIndexed { index, moonA ->
        moons.subList(index + 1, moons.size)
            .map { moonB -> moonA to moonB }
    }.flatten()
}

private fun buildMoons(lines: List<String>): List<Moon> {
    return lines.map(pattern::matchEntire)
        .map { result -> result!!.destructured }
        .mapIndexed { i, (x, y, z) -> Moon(i, Point(x.toInt(), y.toInt(), z.toInt())) }
}