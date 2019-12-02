package day2

fun runProgram(originalProgram: Array<Int>, noun: Int? = null, verb: Int? = null): Array<Int> {
    val program = originalProgram.copyOf()

    program[1] = noun ?: program[1]
    program[2] = verb ?: program[2]

    var index = 0

    var operation = program[index]

    while (operation != 99) {

        val arg1Index = program[index + 1]
        val arg1 = program[arg1Index]
        val arg2Index = program[index + 2]
        val arg2 = program[arg2Index]
        val solutionIndex = program[index + 3]

        when (operation) {
            1 -> program[solutionIndex] = arg1 + arg2
            2 -> program[solutionIndex] = arg1 * arg2
            else -> throw IllegalStateException("Unknown OpCode $operation")
        }

        index += 4
        operation = program[index]
    }

    return program
}

fun solveB(initialProgram: Array<Int>, expectedResult: Int): Int {

    for (noun in 0..99) {
        for (verb in 0..99) {
            val result = runProgram(initialProgram, noun, verb)
            if (result[0] == expectedResult) {
                return 100 * noun + verb
            }
        }
    }

    throw IllegalStateException("No results found")
}