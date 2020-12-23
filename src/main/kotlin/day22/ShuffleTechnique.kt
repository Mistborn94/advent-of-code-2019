package day22

import helper.solveRemainder
import java.math.BigInteger

/**
 * Solution for combining operators from https://www.reddit.com/r/adventofcode/comments/ee56wh/2019_day_22_part_2_so_whats_the_purpose_of_this/fbr0vjb/
 */
sealed class ShuffleTechnique {
    abstract fun nextIndex(currentIndex: Long): Long
    abstract fun previousIndex(currentIndex: Long): Long
    abstract fun combineWith(other: ShuffleTechnique): List<ShuffleTechnique>
}

data class Reverse(val count: Long) : ShuffleTechnique() {
    override fun nextIndex(currentIndex: Long) = count - currentIndex - 1
    override fun previousIndex(currentIndex: Long) = count - currentIndex - 1

    /**
     * reverse, reverse = nothing
     * reverse, cut x = cut count-x , reverse
     * reverse, deal with increment x = deal with increment x, cut count+1-x, reverse
     */
    override fun combineWith(other: ShuffleTechnique): List<ShuffleTechnique> {
        return when (other) {
            is Reverse -> emptyList()
            is Cut -> listOf(Cut(count, count - other.cut), this)
            is DealWithIncrement -> listOf(other, Cut(count, count + 1 - other.increment), this)
        }
    }
}

data class Cut(val count: Long, val cut: Long) : ShuffleTechnique() {
    override fun nextIndex(currentIndex: Long): Long = (currentIndex - cut + count) % count
    override fun previousIndex(currentIndex: Long): Long = (currentIndex + cut) % count

    /**
     * cut x, cut y = cut (x+y) % count
     * cut x, deal with increment y = deal with increment y, cut (x*y) % count
     */
    override fun combineWith(other: ShuffleTechnique): List<ShuffleTechnique> {
        return when (other) {
            is Cut -> listOf(Cut(count, (cut + other.cut) % count))
            is DealWithIncrement -> listOf(other, Cut(count, multiplyMod(cut, other.increment, count)))
            else -> listOf(this, other)
        }
    }
}

data class DealWithIncrement(val count: Long, val increment: Long) : ShuffleTechnique() {
    private val bigCount = count.toBigInteger()
    private val bigIncrement = increment.toBigInteger()

    override fun nextIndex(currentIndex: Long): Long = (currentIndex * increment) % count
    override fun previousIndex(currentIndex: Long): Long {
        val moduli = listOf(bigCount, bigIncrement)
        val remainders = listOf(currentIndex.toBigInteger(), BigInteger.ZERO)
        return (solveRemainder(moduli, remainders) / bigIncrement).longValueExact()
    }

    /**
     * deal with increment x, deal with increment y = deal with increment (x*y) % count
     */
    override fun combineWith(other: ShuffleTechnique): List<ShuffleTechnique> {
        return when (other) {
            is DealWithIncrement -> listOf(DealWithIncrement(count, multiplyMod(increment, other.increment, count)))
            else -> listOf(this, other)
        }
    }
}

private fun multiplyMod(x: Long, y: Long, m: Long): Long =
    ((x.toBigInteger() * y.toBigInteger()) % m.toBigInteger()).longValueExact()
