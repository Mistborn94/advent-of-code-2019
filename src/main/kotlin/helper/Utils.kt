package helper

import java.math.BigInteger
import java.util.*
import java.util.concurrent.BlockingQueue

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

fun <T> BlockingQueue<T>.drainToList(): List<T> {
    val outputList = mutableListOf<T>()
    drainTo(outputList)
    return outputList
}

fun <T> List<List<T>>.indexOf(char: T): Point {
    val startingY = this.indexOfFirst { it.contains(char) }
    return Point(this[startingY].indexOf(char), startingY)
}

operator fun <E> List<List<E>>.get(point: Point) = this[point.y][point.x]
operator fun <E> MutableList<MutableList<E>>.set(point: Point, value: E) {
    this[point.y][point.x] = value
}

/**
 * Calculate the Modular Multiplicative Inverse of [a] under [m]
 * ax mod m = 1 where x in {0,1,2....m-1}
 */
fun mmi(a: BigInteger, m: BigInteger): BigInteger {
    val (g, x, y) = extendedGcd(a, m)
    //Make it positive. Just in case.
    val positiveX = makePositive(m, x)

    assert(g == BigInteger.ONE) { "$a and $m is not coprime" }
    assert(a * positiveX % m == BigInteger.ONE) { "$a * $positiveX % $m != 1" }
    assert(positiveX.longValueExact() in 0 until m.toLong()) { "x not in range 0 until m" }

    return positiveX
}

private fun makePositive(m: BigInteger, x: BigInteger) = (m + x) % m


/**
 * Apply the Chinese Remainder Theorem to solve a set of modulo equations
 *
 * x % moduli[0]    =  remainders[0],
 * x % moduli[1]    =  remainders[1],
 * .......................
 * x % moduli[k-1]  =  remainders[k-1]
 *
 * https://www.geeksforgeeks.org/chinese-remainder-theorem-set-1-introduction/
 * https://www.geeksforgeeks.org/chinese-remainder-theorem-set-2-implementation/
 */
fun solveRemainder(
    moduli: List<BigInteger>,
    remainders: List<BigInteger>
): BigInteger {
    val moduliBigInt = moduli
    val indices = moduliBigInt.indices

    val moduloProduct = moduliBigInt.reduce { acc, l -> acc * l }
    val partialProducts = moduliBigInt.map { moduloProduct / it }
    val inverse = indices.map { mmi(partialProducts[it], moduliBigInt[it]) }

    return indices.map { remainders[it] * partialProducts[it] * inverse[it] }.sum() % moduloProduct
}

private fun Iterable<BigInteger>.sum() = reduce { acc, value -> acc + value }

/**
 * https://cp-algorithms.com/algebra/extended-euclid-algorithm.html
 * Calculate the gcd and x and y where
 *  ax + by = gcd(a,b)
 */
fun extendedGcd(a: BigInteger, b: BigInteger): ExtendedGcd {
    return when {
        //a = 0, so by = b
        a == BigInteger.ZERO -> ExtendedGcd(b, BigInteger.ZERO, BigInteger.ONE)
        //b = 0 so ax = a
        b == BigInteger.ZERO -> ExtendedGcd(a, BigInteger.ONE, BigInteger.ZERO)
        else -> {
            val (g, x1, y1) = extendedGcd(b, a % b)
            //This result means: b⋅x1 + (a % b)⋅y1 = g
            //And: a % b = a − ⌊a/b⌋⋅b

            //With substitution:
            //g = b⋅x1 + (a − ⌊a/b⌋⋅b)⋅y1

            //And expanded:
            //g = b⋅x1 + y1⋅a − ⌊a/b⌋⋅b⋅y1

            //Now rearrange to the format g = ax + by
            //g = a⋅y1 + b(x1 - y1⋅⌊a/b⌋)
            //x = y1
            val x = y1
            //y = x1 - y1⋅⌊a/b⌋
            val y = x1 - y1 * (a / b)
            ExtendedGcd(g, x, y)
        }
    }
}

//ax + by = gcd(a,b)
data class ExtendedGcd(val g: BigInteger, val x: BigInteger, val y: BigInteger)