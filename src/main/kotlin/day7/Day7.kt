package day7

import day5.IntCode
import java.util.Collections.swap

val permutationsA: List<List<Int>> = findPermutations(mutableListOf(0, 1, 2, 3, 4))
val permutationsB: List<List<Int>> = findPermutations(mutableListOf(5, 6, 7, 8, 9))

fun findPermutations(list: MutableList<Int>, k: Int = list.size): List<List<Int>> {
    if (k == 1) {
        return listOf(ArrayList(list))
    }

    val outputs = mutableListOf<List<Int>>()

    outputs += findPermutations(list, k - 1)

    for (i in 0 until (k - 1)) {
        if (k % 2 == 0) {
            swap(list, i, k - 1)
        } else {
            swap(list, 0, k - 1)
        }

        outputs += findPermutations(list, k - 1)
    }
    return outputs
}

fun solveA(program: List<Int>): Int {
    return permutationsA.map { permutation ->
        (0 until 5).fold(0) { prev, i -> runAmplifier(program, permutation[i], prev) }
    }.max()!!
}

private fun runAmplifier(
    program: List<Int>,
    phase: Int,
    previousOutput: Int
): Int {
    return IntCode(program, listOf(phase, previousOutput)).let {
        it.runProgram()
        it.outputs.first()
    }
}