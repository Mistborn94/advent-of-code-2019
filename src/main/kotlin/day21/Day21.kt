package day21

import day5.IntCode
import helper.drainToList

fun solveA(program: List<Long>): Long {

    val intCode = IntCode(program)

    //T and J always start as false
    val instructions = listOf(
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
        "AND D J",

        "WALK"
    )

    if (instructions.size > 16) {
        throw IllegalArgumentException("Springer droid has limited memory")
    }

    intCode.inputs.addAll(instructions.joinToString(separator = "\n", postfix = "\n").map { it.toInt().toLong() })

    intCode.runUtilInput()

    val outputs = intCode.outputs.drainToList()
    if (outputs.last() < Char.MAX_VALUE.toLong()) {
        println(outputs.joinToString(separator = "") { it.toInt().toChar().toString() })
        throw IllegalStateException("Droid fell :(")
    }

    return outputs.last()
}

fun solveB(program: List<Long>): Long {

    val intCode = IntCode(program)

    //T and J always start as false
    val instructions = listOf(
        //Check if E is a hole
        "NOT E T",
        "OR T J",
        //Check if F is a hole
        "NOT F T",
        "OR T J",
        //Check if G is a hole
        "NOT G T",
        "OR T J",
        //Set J if E,F,G is a hole and H ground
        "AND H J",

        //Check if A is a hole
        "NOT A T",
        "OR T J",
        //Check if B is a hole
        "NOT B T",
        "OR T J",
        //Check if C is a hole
        "NOT C T",
        "OR T J",
        //Only jump if landing spot is ground
        "AND D J",
        "RUN"
    )

    if (instructions.size > 16) {
        throw IllegalArgumentException("Springer droid has limited memory")
    }

    intCode.inputs.addAll(instructions.joinToString(separator = "\n", postfix = "\n").map { it.toInt().toLong() })

    intCode.runUtilInput()

    val outputs = intCode.outputs.drainToList()
    if (outputs.last() < Char.MAX_VALUE.toLong()) {
        println(outputs.joinToString(separator = "") { it.toInt().toChar().toString() })
        throw IllegalStateException("Droid fell :(")
    }

    return outputs.last()
}