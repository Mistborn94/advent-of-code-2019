package day21

import day5.IntCode
import helper.cartesianProduct
import helper.drainToList
import java.util.*

//T and J always start as false
//Jump if A,B or C is a hole and D is ground
val instructionsA = listOf(
    //Check if A is a hole
    "NOT A T",
    "OR T J",
    //Check if B is a hole
    "NOT B T",
    "OR T J",
    //Check if C is a hole
    "NOT C T",
    "OR T J",
    //Set J if A, B or C is a hole and D ground
    "AND D J"
)

val instructionsB = instructionsA + listOf(
    "NOT A T",
    "OR H T",
    "AND T J"
)

fun solveA(program: List<Long>): Long {
    return (runSpringdroid(program, instructionsA + "WALK") as SpringdroidResult.SuccessResult).output
}


fun solveB(program: List<Long>): Long {
//    solveBDynamic(program)
    return (runSpringdroid(program, instructionsB + "RUN") as SpringdroidResult.SuccessResult).output
//    return solveBDynamic(program)
}

sealed class SpringdroidResult(val duration: Int) {

    class SuccessResult(val output: Long, duration: Int) : SpringdroidResult(duration)
    class FailureResult(duration: Int) : SpringdroidResult(duration)
}

private fun runSpringdroid(
    program: List<Long>,
    instructions: List<String>
): SpringdroidResult {
    val intCode = IntCode(program)
    if (instructions.size > 16) {
        throw IllegalArgumentException("Springer droid has limited memory")
    }

    instructions.forEach {
        intCode.sendAscii(it)
    }

    intCode.runUtilInput()

    val outputs = intCode.outputs.drainToList()
    if (outputs.last() < Char.MAX_VALUE.toLong()) {
//        println(outputs.joinToString(separator = "") { it.toInt().toChar().toString() })
        return SpringdroidResult.FailureResult(intCode.executionDuration)
    }

    return SpringdroidResult.SuccessResult(outputs.last(), intCode.executionDuration)
}

data class SpringdroidCommand(val executionDuration: Int, val command: List<String>) : Comparable<SpringdroidCommand> {
    override fun compareTo(other: SpringdroidCommand): Int = other.executionDuration.compareTo(executionDuration)
}

private fun solveBDynamic(program: List<Long>): Long {
    val possibleInstructions = listOf("NOT", "OR", "AND")
    val sensors = 'A'..'I'
    val registers = listOf('J', 'T')

    val possibleCommands = possibleInstructions.cartesianProduct((sensors + "T")) { a, b -> "$a $b" }
        .cartesianProduct(registers) { a, b -> "$a $b" }

    val toVisit = PriorityQueue<SpringdroidCommand>()
    toVisit.add(SpringdroidCommand(0, instructionsA))

    var i = 0
    while (toVisit.size > 0) {
        i++
        val (_, command) = toVisit.remove()

        val result = runSpringdroid(program, command + "RUN")

        if (result is SpringdroidResult.SuccessResult) {
            println("Command is $command")
            return result.output
        } else {
            if (command.size < 15) {
                toVisit.addAll(possibleCommands.mapNotNull {
                    if (it == command.last()) {
                        null
                    } else {
                        SpringdroidCommand(result.duration, command + it)
                    }
                })
            }
            if (i % 1000 == 0) {
                println("Iteration $i: ToVisit size is ${toVisit.size}")
            }
        }
    }

    throw IllegalStateException("No working programs")
}