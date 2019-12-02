package day2

fun runProgram(program: Array<Int>) {

    var index = 0;

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
}