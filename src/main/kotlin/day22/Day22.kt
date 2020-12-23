package day22

class CardSimulator(val count: Long, instructions: List<String>) {

    val instructionFunctions: List<ShuffleTechnique> = instructions.map(this::mapInstruction)

    private fun mapInstruction(instruction: String): ShuffleTechnique {
        return when {
            instruction == reversePattern -> Reverse(count)
            instruction.startsWith(cutPattern) -> Cut(count, instruction.substring(4, instruction.length).toLong())
            instruction.startsWith(incrementPattern) -> DealWithIncrement(
                count,
                instruction.substring(20, instruction.length).toLong()
            )
            else -> throw IllegalArgumentException("Unknown command $instruction")
        }
    }

    fun runIteration(cardIndex: Long, instructions: List<ShuffleTechnique> = instructionFunctions): Long =
        instructions.fold(cardIndex) { acc, instruction -> instruction.nextIndex(acc) }

    fun runInverseIteration(cardIndex: Long, instructions: List<ShuffleTechnique> = instructionFunctions): Long =
        instructions.foldRight(cardIndex) { instruction, acc -> instruction.previousIndex(acc) }
}

val reversePattern = "deal into new stack"
val cutPattern = "cut"
val incrementPattern = "deal with increment"

fun solveA(instructions: List<String>, stackSize: Long = 10007, trackedCard: Long = 2019): Long {
    return CardSimulator(stackSize, instructions).runIteration(trackedCard)
}

fun solveB(
    instructions: List<String>,
    trackedIndex: Long,
    stackSize: Long,
    repetitions: Long
): Long {
    val simulator = CardSimulator(stackSize, instructions)

//    val compositeInstruction = instructionFunctions.reduce { acc, next -> acc.andThen(next) }

    var previousIndex = CardSimulator(stackSize, instructions).runInverseIteration(trackedIndex)

    var iteration = 1
    while (iteration < repetitions) {
//        println("Current Index $currentIndex")
        previousIndex = simulator.runInverseIteration(previousIndex)
        iteration += 1
//        if (iteration % 100_000 == 0) {
//            println("Searching for repeats at $iteration")
//        }
    }

//    if (iteration.toLong() == repetitions) {
//        return previousIndex
//    }
//
//    val remaining = repetitions % iteration
//    println("Found repeats at $iteration, $remaining remaining")
//
//    for (it in 0 until remaining) {
//        previousIndex = simulator.runIterationB(instructionFunctions, previousIndex)
//    }

    return previousIndex
}