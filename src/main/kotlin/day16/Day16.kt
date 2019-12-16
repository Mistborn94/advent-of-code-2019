package day16

import java.time.LocalDateTime
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
        currentPattern = calculatePhase(it, currentPattern)
    }

    return currentPattern.subList(0, 8).joinToString(separator = "")
}

private fun calculatePhase(
    iteration: Int,
    currentSignal: List<Int>
): List<Int> {
    return currentSignal.mapIndexed { outputElementIndex, _ ->
        calculateDigit(outputElementIndex + 1, iteration, currentSignal)
    }
}

private fun calculateDigit(
    outputElement: Int,
    iteration: Int,
    currentSignal: List<Int>
): Int {
    if (outputElement % 5_000 == 0) {
        logIteration(iteration, outputElement, currentSignal)
    }

    val sum = currentSignal.subList(outputElement - 1, currentSignal.size).foldIndexed(0) { index, acc, next ->
        val patternValue = patternValue(outputElement, outputElement - 1 + index)
        acc + (next * patternValue)
    }

    return abs(sum) % 10
}

fun solveB(pattern: String, iterations: Int = 100, repeat: Int): String {
    val patternNumbers = pattern.toCharArray().map { it.toString().toInt() }

    var currentPattern = sequence {
        repeat(repeat) {
            yieldAll(patternNumbers)
        }
    }.toList()

    repeat(iterations) { iteration ->
        currentPattern = calculatePhase(iteration, currentPattern)
    }
    val solutionOffset = pattern.substring(0, 8).toInt()

    currentPattern.take(solutionOffset)
    return currentPattern.take(8).joinToString(separator = "")
}

private fun logIteration(
    iteration: Int,
    outputElement: Int,
    currentSignal: List<Int>
) {
    println("${LocalDateTime.now()} Mapping iteration $iteration output $outputElement with size ${currentSignal.size}")
}


