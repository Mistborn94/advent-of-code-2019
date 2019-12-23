package day22

class CardStack(val cards: List<Int>) {

    constructor(count: Int) : this((0 until count).toList())

    fun reverse() = CardStack(cards.reversed())

    fun cut(n: Int): CardStack {
        return if (n < 0) {
            val fromIndex = cards.size + n
            val cut = cards.subList(fromIndex, cards.size)
            val remainder = cards.subList(0, fromIndex)
            CardStack(cut + remainder)
        } else {
            val cut = cards.subList(0, n)
            val remainder = cards.subList(n, cards.size)
            CardStack(remainder + cut)
        }
    }

    fun dealWithIncrement(increment: Int): CardStack {
        val array = Array(cards.size) { -1 }
        var dealPosition = 0
        cards.forEach {
            array[dealPosition % cards.size] = it
            dealPosition += increment
        }
        return CardStack(array.asList())
    }

    operator fun get(index: Int): Int = cards[index]

    fun apply(instruction: String): CardStack {
        return when {
            instruction == reversePattern -> reverse()
            instruction.startsWith(cutPattern) -> cut(instruction.substring(4, instruction.length).toInt())
            instruction.startsWith(incrementPattern) -> dealWithIncrement(
                instruction.substring(
                    20,
                    instruction.length
                ).toInt()
            )
            else -> throw IllegalArgumentException("Unknown command $instruction")
        }
    }
}

val reversePattern = "deal into new stack"
val cutPattern = "cut"
val incrementPattern = "deal with increment"

fun solveA(instructions: List<String>, stackSize: Int = 10007): CardStack {

    return instructions.fold(CardStack(stackSize)) { acc, instruction ->
        acc.apply(instruction)
    }
}

fun solveB(instructions: List<String>, stackSize: Long = 1_193_157_175_140_47L): CardStack {
    TODO("Not implemented")
//
//    return instructions.fold(CardStack(stackSize)) { acc, instruction ->
//        acc.apply(instruction)
//    }
}