package day7

import day5.IntCode
import java.util.Collections.swap
import java.util.concurrent.CompletableFuture
import java.util.concurrent.LinkedBlockingQueue

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

fun solveA(program: List<Int>): Long {
    return permutationsA.map { permutation ->
        (0 until 5).fold(0L) { prev, i -> runAmplifier(program, permutation[i], prev) }
    }.max()!!
}

fun solveB(program: List<Long>): Long {
    return permutationsB.map { permutation ->
        runUntilStopping(program, permutation)
    }.max()!!
}

fun runUntilStopping(program: List<Long>, phases: List<Int>): Long {

    val queueA = LinkedBlockingQueue(listOf(phases[0].toLong(), 0))
    val queueB = LinkedBlockingQueue(listOf(phases[1].toLong()))
    val queueC = LinkedBlockingQueue(listOf(phases[2].toLong()))
    val queueD = LinkedBlockingQueue(listOf(phases[3].toLong()))
    val queueE = LinkedBlockingQueue(listOf(phases[4].toLong()))

    val intcodeA = IntCode(program, queueA, queueB)
    val intcodeB = IntCode(program, queueB, queueC)
    val intcodeC = IntCode(program, queueC, queueD)
    val intcodeD = IntCode(program, queueD, queueE)
    val intcodeE = IntCode(program, queueE, queueA)

    CompletableFuture.runAsync { intcodeA.runProgram() }
    CompletableFuture.runAsync { intcodeB.runProgram() }
    CompletableFuture.runAsync { intcodeC.runProgram() }
    CompletableFuture.runAsync { intcodeD.runProgram() }
    CompletableFuture.runAsync { intcodeE.runProgram() }.get()

    return intcodeE.outputs.last()
}

private fun runAmplifier(
    program: List<Int>,
    phase: Int,
    previousOutput: Long
): Long {
    return IntCode(program, listOf(phase, previousOutput.toInt())).let {
        it.runProgram()
        it.outputs.first()
    }
}