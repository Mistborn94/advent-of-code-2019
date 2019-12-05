package day5

data class Param(val mode: ArgMode, val value: Int) {
    fun resolveValue(program: List<Int>): Int = mode.resolve(value, program)
}

enum class ArgMode(val resolve: (Int, List<Int>) -> Int) {
    POSITION({ value, program -> program[value] }),
    IMMEDIATE({ value, _ -> value }),
}

enum class OperationType(val code: String, val argCount: Int, val run: (IntCode, List<Param>) -> Unit) {
    ADD("01", 3, { elfCode, params ->
        val arg1 = params[0].resolveValue(elfCode.program)
        val arg2 = params[1].resolveValue(elfCode.program)
        val solutionIndex = params[2].value

        elfCode.program[solutionIndex] = arg1 + arg2
    }),
    MULTIPLY("02", 3, { elfCode, params ->
        val arg1 = params[0].resolveValue(elfCode.program)
        val arg2 = params[1].resolveValue(elfCode.program)
        val solutionIndex = params[2].value

        elfCode.program[solutionIndex] = arg1 * arg2
    }),
    INPUT("03", 1, { elfCode, params ->
        val solutionIndex = params[0].value

        elfCode.program[solutionIndex] = elfCode.inputSupplier()
    }),
    OUTPUT("04", 1, { elfCode, params ->
        val arg1 = params[0].resolveValue(elfCode.program)
        elfCode.output(arg1)
    }),
    JUMP_TRUE("05", 2, { elfCode, params ->
        val arg1 = params[0].resolveValue(elfCode.program)
        val arg2 = params[1].resolveValue(elfCode.program)

        if (arg1 != 0) {
            elfCode.index = arg2
        }
    }),
    JUMP_FALSE("06", 2, { elfCode, params ->
        val arg1 = params[0].resolveValue(elfCode.program)
        val arg2 = params[1].resolveValue(elfCode.program)

        if (arg1 == 0) {
            elfCode.index = arg2
        }
    }),
    LESS_THAN("07", 3, { elfCode, params ->
        val arg1 = params[0].resolveValue(elfCode.program)
        val arg2 = params[1].resolveValue(elfCode.program)
        val solutionIndex = params[2].value

        elfCode.program[solutionIndex] = if (arg1 < arg2) 1 else 0
    }),
    EQUALS("08", 3, { elfCode, params ->
        val arg1 = params[0].resolveValue(elfCode.program)
        val arg2 = params[1].resolveValue(elfCode.program)
        val solutionIndex = params[2].value

        elfCode.program[solutionIndex] = if (arg1 == arg2) 1 else 0
    });

    val size = argCount + 1

    companion object {
        fun fromCode(code: String): OperationType = values().first { it.code == code }
    }

}

class IntCode(originalProgram: List<Int>, val inputSupplier: () -> Int) {

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
    }

    fun output(value: Int) {
        outputs += value
    }
}