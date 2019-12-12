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

    private var currentStart = ArrayList<Int>()
    private var currentPattern = ArrayList<Int>()

    private var finalPattern: String? = null

    //TODO: Alternate approach
    //If position = initial, check if symmetric
    //Probably waaaay more efficient
    fun add(item: Int) {
        if (!solved) {
            size += 1
            currentPattern.add(item)

            if (!matchContinues(item)) {
                while (!currentStart.startsWith(currentPattern)) {
                    currentStart.add(currentPattern[0])
                    currentPattern.removeAt(0)
                }
                searchIndex = currentPattern.size
            } else {
                searchIndex += 1
                if (currentPattern.isNotEmpty() && currentStart == currentPattern) {
                    solved = true
                    finalPattern = currentPattern.toString()
                    size /= 2
                    println("Found Pattern with size $size")
                }
            }
        }
    }

    private fun matchContinues(itemPattern: Int): Boolean {
        return currentStart.size > searchIndex && currentStart[searchIndex] == itemPattern
    }
}

private fun <E> java.util.ArrayList<E>.startsWith(prefix: java.util.ArrayList<E>): Boolean {
    return if (prefix.size > this.size) false
    else prefix.indices.all { this[it] == prefix[it] }
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

    val energy: Int
        get() = potentialEnergy * kineticEnergy

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
    val pattern = PatternSet()
    val moons = buildMoons(lines)
    val moonPairs = getMoonPairs(moons)

    while (!moons[0].hasAllPatterns ) {
        pattern.add(moons[0].position)
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
    return a / greatestCommonDenominator(a, b) * b
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