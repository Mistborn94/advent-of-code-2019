package day16

import kotlin.math.abs

val basePattern = listOf(0, 1, 0, -1)

fun solveA(pattern: String, iterations: Int = 100): String {
    var currentPattern = pattern.toCharArray().map { it.toString().toInt() }

    repeat(iterations) {
        currentPattern = calculatePhase(currentPattern)
    }

    return currentPattern.subList(0, 8).joinToString(separator = "")
}

fun patternValue(outputElement: Int, index: Int): Int {
    val resolvedIndex = (index + 1) % (4 * outputElement)
    val group = resolvedIndex / outputElement
    return basePattern[group]
}

private fun calculateDigit(outputElement: Int, currentSignal: List<Int>): Int {
    val sum = currentSignal.subList(outputElement - 1, currentSignal.size).foldIndexed(0) { index, acc, next ->
        val patternValue = patternValue(outputElement, outputElement - 1 + index)
        acc + (next * patternValue)
    }

    return abs(sum) % 10
}

private fun calculatePhase(currentSignal: List<Int>): List<Int> {
    return currentSignal.mapIndexed { outputElementIndex, _ ->
        calculateDigit(outputElementIndex + 1, currentSignal)
    }
}

fun solveB(pattern: String, iterations: Int = 100, repeat: Int): String {
    val patternNumbers = pattern.toCharArray().map { it.toString().toInt() }
    val solutionOffset = pattern.substring(0, 7).toInt()
    val finalDigit = patternNumbers[patternNumbers.lastIndex]
    val totalPatternSize = patternNumbers.size * repeat
    val effectivePatternSize = totalPatternSize - solutionOffset

    if (solutionOffset <= totalPatternSize / 2) {
        throw IllegalArgumentException("This is not gonna work")
    }

    var currentPattern: List<Int?> = ArrayList<Int>(effectivePatternSize).run {
        sequence {
            repeat(repeat) {
                yieldAll(patternNumbers)
            }
        }.drop(solutionOffset).toCollection(this)
    }

    repeat(iterations) {
        val newIteration = Array<Int?>(effectivePatternSize) { null }
        newIteration[effectivePatternSize - 1] = finalDigit

        for (elementIndex in IntProgression.fromClosedRange(effectivePatternSize - 2, 0, -1)) {
            newIteration[elementIndex] = (newIteration[elementIndex + 1]!! + currentPattern[elementIndex]!!) % 10
        }

        currentPattern = newIteration.asList()
    }

    return currentPattern.subList(0,8).joinToString(separator = "")
}

