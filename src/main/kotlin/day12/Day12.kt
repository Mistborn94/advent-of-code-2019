package day12

import java.lang.Integer.max
import java.lang.Integer.min
import kotlin.math.abs

data class Point(val x: Int, val y: Int, val z: Int) {
    operator fun plus(vector: Vector) = Point(x + vector.x, y + vector.y, z + vector.z)
    fun magnitude(): Int = abs(x) + abs(y) + abs(z)
}

data class Vector(val x: Int, val y: Int, val z: Int) {
    operator fun plus(other: Vector) = Vector(x + other.x, y + other.y, z + other.z)

    fun magnitude(): Int = abs(x) + abs(y) + abs(z)
}

class Moon(val id: Int, var position: Point, var velocity: Vector = Vector(0, 0, 0)) {

    private val potentialEnergy: Int
        get() = position.magnitude()
    private val kineticEnergy: Int
        get() = velocity.magnitude()

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
        return "Moon(id=$id,position=$position, velocity=$velocity)"
    }
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

fun solveB(lines: List<String>) {

}

private fun runStep(
    moonPairs: List<Pair<Moon, Moon>>, moons: List<Moon>
) {
    moonPairs.forEach { (a, b) ->
        a.applyGravity(b)
        b.applyGravity(a)
    }
    moons.forEach { it.move() }
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