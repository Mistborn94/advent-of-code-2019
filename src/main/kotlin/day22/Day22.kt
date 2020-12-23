package day22

class CardSimulator(val count: Long, instructions: List<String>) {

    val instructionFunctions: List<ShuffleTechnique> = reduceInstructions(instructions.map(this::mapInstruction))

    private fun reduceInstructions(list: List<ShuffleTechnique>): List<ShuffleTechnique> {
        var reducedList = list
        while (reducedList.size > 3) {
            val newList = mutableListOf<ShuffleTechnique>()
            reducedList.forEach { current ->
                if (newList.isEmpty()) {
                    newList.add(current)
                } else {
                    val prev = newList.removeAt(newList.lastIndex)
                    val elements = prev.combineWith(current)
                    newList.addAll(elements)
                }
            }
            reducedList = newList
        }
        return reducedList
    }

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

    fun runIteration(trackedCard: Long, instructions: List<ShuffleTechnique> = instructionFunctions): Long =
        instructions.fold(trackedCard) { acc, instruction -> instruction.nextIndex(acc) }

    fun runInverseIteration(trackedIndex: Long, instructions: List<ShuffleTechnique> = instructionFunctions): Long =
        instructions.foldRight(trackedIndex) { instruction, acc -> instruction.previousIndex(acc) }

    tailrec fun runInverseIterations(trackedIndex: Long, iterations: Long): Long {
        if (iterations < 2) {
            var current = trackedIndex
            repeat(iterations.toInt()) {
                current = runInverseIteration(current)
            }
            return current
        }

        var currentMultiple = 1L
        var nextMultiple = currentMultiple * 2
        var currentList = instructionFunctions

        do {
            currentMultiple = nextMultiple
            currentList = reduceInstructions(currentList + currentList)
            nextMultiple *= 2
        } while (nextMultiple < iterations)

        val previousIndex = runInverseIteration(trackedIndex, currentList)
        return runInverseIterations(previousIndex, iterations - currentMultiple)
    }
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
    return CardSimulator(stackSize, instructions).runInverseIterations(trackedIndex, repetitions)
}