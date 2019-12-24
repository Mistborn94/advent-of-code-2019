package day5

import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue

private fun binaryOperation(intCode: IntCode, params: List<Param>, operation: (Long, Long) -> Long) {
    val arg1 = params[0].resolveValue(intCode)
    val arg2 = params[1].resolveValue(intCode)
    val solutionIndex = params[2].solutionIndex(intCode)

    intCode[solutionIndex] = operation(arg1, arg2)
}

data class Param(val mode: ArgMode, val value: Long) {
    fun resolveValue(intCode: IntCode): Long = mode.resolveValue(value, intCode)
    fun solutionIndex(intCode: IntCode): Int = mode.resolveSolutionIndex(value, intCode)
}

enum class ArgMode(
    val resolveValue: (Long, IntCode) -> Long,
    val resolveSolutionIndex: (Long, IntCode) -> Int
) {
    POSITION({ value, intCode -> intCode[value.toInt()] }, { value, _ -> value.toInt() }),
    IMMEDIATE({ value, _ -> value }, { _, _ -> throw kotlin.IllegalArgumentException() }),
    RELATIVE(
        { value, intCode -> intCode[intCode.relativeBase + value.toInt()] },
        { value, intCode -> value.toInt() + intCode.relativeBase }),
}

enum class OperationType(val code: String, val argCount: Int, val run: (IntCode, List<Param>) -> Unit) {
    ADD("01", 3, { intCode, params ->
        binaryOperation(intCode, params) { arg1, arg2 -> arg1 + arg2 }
    }),
    MULTIPLY("02", 3, { intCode, params ->
        binaryOperation(intCode, params) { arg1, arg2 -> arg1 * arg2 }
    }),
    INPUT("03", 1, { intCode, params ->
        val solutionIndex = params[0].solutionIndex(intCode)
        val nextInput = intCode.nextInput()
        intCode[solutionIndex] = nextInput
    }),
    OUTPUT("04", 1, { intCode, params ->
        val arg1 = params[0].resolveValue(intCode)
        intCode.output(arg1)
    }),
    JUMP_TRUE("05", 2, { intCode, params ->
        val arg1 = params[0].resolveValue(intCode)
        val arg2 = params[1].resolveValue(intCode)

        if (arg1 != 0L) {
            intCode.instructionPointer = arg2.toInt()
        }
    }),
    JUMP_FALSE("06", 2, { intCode, params ->
        val arg1 = params[0].resolveValue(intCode)
        val arg2 = params[1].resolveValue(intCode)

        if (arg1 == 0L) {
            intCode.instructionPointer = arg2.toInt()
        }
    }),
    LESS_THAN("07", 3, { intCode, params ->
        binaryOperation(intCode, params) { arg1, arg2 -> if (arg1 < arg2) 1 else 0 }
    }),
    EQUALS("08", 3, { intCode, params ->
        binaryOperation(intCode, params) { arg1, arg2 -> if (arg1 == arg2) 1 else 0 }
    }),
    ADJUST_OFFSET("09", 1, { intCode, params ->
        intCode.relativeBase += params[0].resolveValue(intCode).toInt()
    });

    val size = argCount + 1

    companion object {
        fun fromCode(code: String): OperationType = values().first { it.code == code }
    }

}

class IntCode(
    originalProgram: List<Long>,
    val inputs: BlockingQueue<Long> = LinkedBlockingQueue(),
    val outputs: BlockingQueue<Long> = LinkedBlockingQueue(),
    val inputNotifier: () -> Unit = {}
) {

    constructor(originalProgram: List<Int>, inputs: List<Int>) : this(
        originalProgram.map(Int::toLong),
        LinkedBlockingQueue(inputs.map(Int::toLong))
    )

    var memory = originalProgram.toMutableList()
    var instructionPointer = 0
    var relativeBase = 0

    /**
     * Run until the program ends (OpCode 99), blocking when waiting for input
     */
    fun runProgram() {
        instructionPointer = 0
        var instruction = memory[instructionPointer]

        while (instruction != 99L) {
            instruction = runInstruction(instruction)
        }
    }

    /**
     * Run until either
     * - The program ends (OpCode 99)
     * - Or an input is requested but the input queue is empty
     */
    fun runUtilInput() {
        var instruction = memory[instructionPointer]

        while (instruction != 99L && !(instruction == 3L && inputs.isEmpty())) {
            instruction = runInstruction(instruction)
        }
    }

    private fun runInstruction(instruction: Long): Long {
        val initialInstructionPointer = instructionPointer
        val paddedInstruction = instruction.toString().padStart(5, '0')

        val opCode = paddedInstruction.substring(3, 5)
        val operation = OperationType.fromCode(opCode)

        val params = (0 until operation.argCount).map { paramIndex ->
            val operationIndex = 2 - paramIndex
            val value = paddedInstruction.substring(operationIndex..operationIndex).toInt()
            val type = ArgMode.values()[value]
            Param(type, memory[instructionPointer + paramIndex + 1])
        }

        operation.run(this, params)

        if (instructionPointer == initialInstructionPointer) {
            instructionPointer += operation.size
        }
        return memory[instructionPointer]
    }

    fun output(value: Long) {
        outputs += value
    }

    fun nextInput(): Long {
        inputNotifier()
        return inputs.take()
    }

    operator fun set(index: Int, value: Long) {
        if (index !in memory.indices) {
            resizeMemory(index)
        }

        memory[index] = value
    }

    operator fun get(index: Int): Long {
        if (index !in memory.indices) {
            resizeMemory(index)
        }

        return memory[index]
    }

    private fun resizeMemory(maxIndex: Int) {
        val currentMemory = memory
        memory = MutableList(maxIndex + 1) { 0L }
        currentMemory.forEachIndexed { index, value ->
            memory[index] = value
        }
    }
}