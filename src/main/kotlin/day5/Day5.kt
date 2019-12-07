package day5

import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue

private fun binaryOperation(intCode: IntCode, params: List<Param>, operation: (Int, Int) -> Int) {
    val arg1 = params[0].resolveValue(intCode.program)
    val arg2 = params[1].resolveValue(intCode.program)
    val solutionIndex = params[2].value

    intCode.program[solutionIndex] = operation(arg1, arg2)
}

data class Param(val mode: ArgMode, val value: Int) {
    fun resolveValue(program: List<Int>): Int = mode.resolve(value, program)
}

enum class ArgMode(val resolve: (Int, List<Int>) -> Int) {
    POSITION({ value, program -> program[value] }),
    IMMEDIATE({ value, _ -> value }),
}

enum class OperationType(val code: String, val argCount: Int, val run: (IntCode, List<Param>) -> Unit) {
    ADD("01", 3, { intCode, params ->
        binaryOperation(intCode, params) { arg1, arg2 -> arg1 + arg2 }
    }),
    MULTIPLY("02", 3, { intCode, params ->
        binaryOperation(intCode, params) { arg1, arg2 -> arg1 * arg2 }
    }),
    INPUT("03", 1, { intCode, params ->
        val solutionIndex = params[0].value

        intCode.program[solutionIndex] = intCode.nextInput()
    }),
    OUTPUT("04", 1, { intCode, params ->
        val arg1 = params[0].resolveValue(intCode.program)
        intCode.output(arg1)
    }),
    JUMP_TRUE("05", 2, { intCode, params ->
        val arg1 = params[0].resolveValue(intCode.program)
        val arg2 = params[1].resolveValue(intCode.program)

        if (arg1 != 0) {
            intCode.index = arg2
        }
    }),
    JUMP_FALSE("06", 2, { intCode, params ->
        val arg1 = params[0].resolveValue(intCode.program)
        val arg2 = params[1].resolveValue(intCode.program)

        if (arg1 == 0) {
            intCode.index = arg2
        }
    }),
    LESS_THAN("07", 3, { intCode, params ->
        binaryOperation(intCode, params) { arg1, arg2 -> if (arg1 < arg2) 1 else 0 }
    }),
    EQUALS("08", 3, { intCode, params ->
        binaryOperation(intCode, params) { arg1, arg2 -> if (arg1 == arg2) 1 else 0 }
    });

    val size = argCount + 1

    companion object {
        fun fromCode(code: String): OperationType = values().first { it.code == code }
    }

}

class IntCode(
    originalProgram: List<Int>,
    val inputs: BlockingQueue<Int>,
    val outputListener: (Int) -> Unit = {},
    val finishListener: () -> Unit = {}
) {

    constructor(originalProgram: List<Int>, inputs: List<Int>) : this(originalProgram, LinkedBlockingQueue(inputs))

    val program = originalProgram.toMutableList()
    val outputs = mutableListOf<Int>()
    var index = 0

    fun runProgram() {
        index = 0
        var instruction = program[index]

        while (instruction != 99) {
            val startIndex = index
            val paddedInstruction = instruction.toString().padStart(5, '0')

            val opCode = paddedInstruction.substring(3, 5)
            val operation = OperationType.fromCode(opCode)

            val params = (0 until operation.argCount).map { paramIndex ->
                val operationIndex = 2 - paramIndex
                val value = paddedInstruction.substring(operationIndex..operationIndex).toInt()
                val type = ArgMode.values()[value]
                Param(type, program[index + paramIndex + 1])
            }

            operation.run(this, params)

            if (index == startIndex) {
                index += operation.size
            }
            instruction = program[index]
        }

        finishListener()
    }

    fun output(value: Int) {
        outputs += value
        outputListener(value)
    }

    fun nextInput(): Int {
        return inputs.take()
    }
}