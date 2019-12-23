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

    fun runIteration(instructions: List<String>): CardStack {
        return instructions.fold(this) { acc, instruction ->
            acc.apply(instruction)
        }
    }
}

class CardSimulator(val count: Long) {
    fun reverse(cardIndex: Long) = count - cardIndex - 1
    fun cut(n: Int, cardIndex: Long): Long = (cardIndex - n + count) % count
    fun dealWithIncrement(increment: Int, cardIndex: Long) = (increment * cardIndex) % count

    fun apply(instruction: String, cardIndex: Long): Long {
        return when {
            instruction == reversePattern -> reverse(cardIndex)
            instruction.startsWith(cutPattern) -> cut(instruction.substring(4, instruction.length).toInt(), cardIndex)
            instruction.startsWith(incrementPattern) -> dealWithIncrement(
                instruction.substring(20, instruction.length).toInt(),
                cardIndex
            )
            else -> throw IllegalArgumentException("Unknown command $instruction")
        }
    }

    fun mapInstruction(instruction: String): (Long) -> Long {
        return when {
            instruction == reversePattern -> { cardIndex -> reverse(cardIndex) }
            instruction.startsWith(cutPattern) -> {
                val cutSize = instruction.substring(4, instruction.length).toInt()
                ({ cardIndex -> cut(cutSize, cardIndex) })
            }
            instruction.startsWith(incrementPattern) -> {
                val increment = instruction.substring(20, instruction.length).toInt()
                ({ cardIndex -> dealWithIncrement(increment, cardIndex) })
            }
            else -> throw IllegalArgumentException("Unknown command $instruction")
        }
    }

    fun mapInstructions(instructions: List<String>) = instructions.map(this::mapInstruction)

    fun runIteration(instructions: List<(Long) -> Long>, cardIndex: Long): Long =
        instructions.fold(cardIndex) { acc, instruction -> instruction(acc) }
}

val reversePattern = "deal into new stack"
val cutPattern = "cut"
val incrementPattern = "deal with increment"

fun solveA(instructions: List<String>, stackSize: Int = 10007): CardStack {

    return CardStack(stackSize).runIteration(instructions)
}

fun solveB(
    instructions: List<String>,
    stackSize: Long,
    trackedCard: Long,
    repetitions: Long
): Long {
    val simulator = CardSimulator(stackSize)
    val instructionFunctions = simulator.mapInstructions(instructions)

    var currentIndex = CardSimulator(stackSize).runIteration(instructionFunctions, trackedCard)

    var iteration = 1
    while (currentIndex != trackedCard && iteration < repetitions) {
        currentIndex = simulator.runIteration(instructionFunctions, currentIndex)
        iteration += 1
        if (iteration % 100_000 == 0) {
            println("Searching for repeats at $iteration")
        }
    }

    if (iteration.toLong() == repetitions) {
        return currentIndex
    }

    val remaining = repetitions % iteration
    println("Found repeats at $iteration, $remaining remaining")

    for (it in 0 until remaining) {
        currentIndex = simulator.runIteration(instructionFunctions, currentIndex)
    }

    return currentIndex
}