package day16

import kotlin.math.abs

val basePattern = listOf(0, 1, 0, -1)

fun patternValue(outputElement: Int, index: Int): Int {
    val resolvedIndex = (index + 1) % (4 * outputElement)
    val group = resolvedIndex / outputElement
    return basePattern[group]
}

fun solveA(pattern: String, iterations: Int = 100): String {
    var currentPattern = pattern.toCharArray().map { it.toString().toInt() }

    repeat(iterations) {
        currentPattern = calculatePhase(currentPattern)
    }

    return currentPattern.subList(0, 8).joinToString(separator = "")
}

private fun calculatePhase(
    currentSignal: List<Int>
): List<Int> {
    return currentSignal.mapIndexed { outputElementIndex, _ ->

        val sum = currentSignal.mapIndexed { index, value ->
            val patternValue = patternValue(outputElementIndex + 1, index)
            value * patternValue
        }.sum()

        val lastDigit = abs(sum) % 10
        lastDigit
    }
}

private fun calculatePhase(
    iteration: Int,
    currentSignal: Sequence<Int>
): Sequence<Int> {
    val signalList = currentSignal.toList()
    return signalList.asSequence().mapIndexed { outputElementIndex, _ ->
        val digit = calculateDigit(outputElementIndex, iteration, signalList)
        digit
    }
}

private fun calculateDigit(
    outputElementIndex: Int,
    iteration: Int,
    currentSignal: List<Int>
): Int {
    if (outputElementIndex % 10_000 == 0) {
        println("Mapping iteration $iteration $outputElementIndex")
    }
    val sum = currentSignal.mapIndexed { index, value ->
        val patternValue = patternValue(outputElementIndex + 1, index)
        value * patternValue
    }.sum()
    return abs(sum) % 10
}

fun solveB(pattern: String, iterations: Int = 100): String {
    val patternNumbers = pattern.toCharArray().map { it.toString().toInt() }

    var currentPattern = sequence {
        repeat(10_000) {
            yieldAll(patternNumbers)
        }
    }

    repeat(iterations) {
        currentPattern = calculatePhase(it, currentPattern)
    }
    val solutionOffset = pattern.substring(0, 8).toInt()

    currentPattern.take(solutionOffset)
    return currentPattern.take(8).joinToString(separator = "")
}

