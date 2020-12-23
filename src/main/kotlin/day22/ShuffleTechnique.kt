package day22

import helper.solveRemainder

/**
 * Solution from
 * https://www.reddit.com/r/adventofcode/comments/ee56wh/2019_day_22_part_2_so_whats_the_purpose_of_this/fbr0vjb/
 *
 * Operators can be combined:
 *
 *
 * cut x
 * cut y
 * ---
 * cut (x+y) % count
 *
 * deal with increment x
 * deal with increment y
 * ---
 * deal with increment (x*y) % count
 *
 *
 * Or reordered:
 *
 * reverse
 * cut x
 * -------------------
 * cut count-x
 * reverse
 *
 * cut x
 * deal with increment y
 * ---
 * deal with increment y
 * cut (x*y) % count
 *
 * reverse
 * deal with increment x
 * ---
 * deal with increment x
 * cut -(x-1) = count+1-x
 * reverse
 *
 * Also:
 *  the deck becomes sorted again after (count - 1) iterations
 */

sealed class ShuffleTechnique(private val order: Int) : Comparable<ShuffleTechnique> {

    /**
    private fun reverse(cardIndex: Long) = count - cardIndex - 1
    private fun cut(n: Int, cardIndex: Long): Long = (cardIndex - n + count) % count
    private fun dealWithIncrement(increment: Int, cardIndex: Long) = (increment * cardIndex) % count
     */
    abstract fun nextIndex(currentIndex: Long): Long
    abstract fun previousIndex(currentIndex: Long): Long

    //    abstract fun combineWith(other: ShuffleTechnique): ShuffleTechnique
//    abstract fun canCombine(other: ShuffleTechnique): Boolean
    abstract fun sort(other: ShuffleTechnique): List<ShuffleTechnique>

    override fun compareTo(other: ShuffleTechnique): Int = order.compareTo(other.order)
}

//class NoOpTechnique(val count: Long) : ShuffleTechnique(2) {
//    override fun nextIndex(currentIndex: Long) = currentIndex
//    override fun previousIndex(currentIndex: Long) = currentIndex
////    override fun combineWith(other: ShuffleTechnique): ShuffleTechnique = other
////    override fun canCombine(other: ShuffleTechnique): Boolean = true
//    override fun sort(other: ShuffleTechnique): List<ShuffleTechnique> = listOf(other)
//}


class Reverse(val count: Long) : ShuffleTechnique(2) {
    override fun nextIndex(currentIndex: Long) = count - currentIndex - 1
    override fun previousIndex(currentIndex: Long) = count - currentIndex - 1

//    override fun combineWith(other: ShuffleTechnique): ShuffleTechnique {
//        return when (other) {
//            is NoOpTechnique -> this
//            is Reverse -> NoOpTechnique(count)
//            else -> throw IllegalArgumentException("Cannot combine directly")
//        }
//    }
//
//    override fun canCombine(other: ShuffleTechnique): Boolean = other is NoOpTechnique || other is Reverse

    /**
     * reverse
     * reverse
     * -------------------
     * nothing
     *
     * reverse
     * cut x
     * -------------------
     * cut count-x
     * reverse
     *
     * reverse
     * deal with increment x
     * ---
     * deal with increment x
     * cut -(x-1) = count+1-x
     * reverse
     */
    override fun sort(other: ShuffleTechnique): List<ShuffleTechnique> {
        return when (other) {
//            is NoOpTechnique -> listOf(this)
            is Reverse -> emptyList()
            is Cut -> listOf(Cut(count, count - other.cut), this)
            is DealWithIncrement -> listOf(other, Cut(count, count + 1 - other.increment), this)
        }
    }
}

class Cut(val count: Long, val cut: Long) : ShuffleTechnique(1) {
    override fun nextIndex(currentIndex: Long): Long = (currentIndex - cut + count) % count
    override fun previousIndex(currentIndex: Long): Long = (currentIndex + cut) % count

    /**
     * cut x
     * cut y
     * ---
     * cut (x+y) % count
     *
     * cut x
     * deal with increment y
     * ---
     * deal with increment y
     * cut (x*y) % count
     */
    override fun sort(other: ShuffleTechnique): List<ShuffleTechnique> {
        return when (other) {
//            is NoOpTechnique -> listOf(this)
            is Cut -> listOf(Cut(count, (cut + other.cut) % count), this)
            is DealWithIncrement -> listOf(other, Cut(count, multiplyMod(cut, other.increment, count)))
            else -> listOf(this, other)
        }
    }
}

class DealWithIncrement(val count: Long, val increment: Long) : ShuffleTechnique(0) {
    override fun nextIndex(currentIndex: Long): Long = (currentIndex * increment) % count
    override fun previousIndex(currentIndex: Long): Long =
        solveRemainder(listOf(count, increment), listOf(currentIndex, 0)) / increment

    /**
     * deal with increment x
     * deal with increment y
     * ---
     * deal with increment (x*y) % count
     */
    override fun sort(other: ShuffleTechnique): List<ShuffleTechnique> {
        return when (other) {
//            is NoOpTechnique -> listOf(this)
            is DealWithIncrement -> listOf(other, Cut(count, multiplyMod(increment, other.increment, count)))
            else -> listOf(this, other)
        }
    }

}

//TODO: This might cause overflow :/
private fun multiplyMod(x: Long, y: Long, m: Long) = x * y % m
