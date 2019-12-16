package day16

import kotlin.math.abs
import kotlin.math.min

val basePattern = listOf(0, 1, 0, -1)

fun solveA(pattern: String, iterations: Int = 100): String {
    val currentPattern = pattern.toCharArray().map { it.toString().toInt() }
    return Solver(currentPattern, iterations, 1..8).solve().joinToString(separator = "")
}

fun solveB(pattern: String, iterations: Int = 100, repeat: Int): String {
    val patternNumbers = pattern.toCharArray().map { it.toString().toInt() }

    val currentPattern = sequence {
        repeat(repeat) {
            yieldAll(patternNumbers)
        }
    }.toList()

    val solutionOffset = pattern.substring(0, 7).toInt()
    val solver = Solver(currentPattern, iterations, solutionOffset + 1..solutionOffset + 8)

    return solver.solve().joinToString(separator = "")
}

class Solver(
    private val initialPattern: List<Int>,
    private val requiredIterations: Int,
    private val requiredOutputElements: IntRange
) {
    private val lastOutputElement = initialPattern.size
    private val firstOutputElement = requiredOutputElements.first

    private val iterations: MutableMap<Int, Array<Int?>> = mutableMapOf()
    private val finalDigit = initialPattern[lastOutputElement - 1]

    fun solve(): List<Int> {
        return requiredOutputElements.map { solveDigit(requiredIterations, it) }
    }

    private fun sumDigits(iteration: Int, digits: Iterable<Int>): Int {
        return digits.fold(0) { acc, it -> acc + solveDigit(iteration, it) }
    }

    private fun solveDigit(iteration: Int, outputElement: Int): Int {
        if (iteration == 0) {
            return initialPattern[outputElement - 1]
        }

        if (outputElement == lastOutputElement) {
            return finalDigit
        }

        val iterationItem = iterations.getOrPut(iteration) { emptyArray() }
        val cacheIndex = outputElement - firstOutputElement
        val knownValue = iterationItem[cacheIndex]

        if (knownValue != null) {
            return knownValue
        }

        val solution = when {
            //Solution by adding only digits after itself
            outputElement > lastOutputElement / 3 -> sumDigits(
                iteration - 1,
                outputElement until min(lastOutputElement + 1, outputElement * 2)
            )
            else -> {
                val ranges = (outputElement..lastOutputElement).chunked(outputElement)

                ranges.foldIndexed(0) { index, acc, value ->
                    val patternValue = basePattern[(index + 1) % 4]
                    if (patternValue == 0) {
                        acc
                    } else {
                        acc + sumDigits(iteration - 1, value) * patternValue
                    }
                }
            }
        }
        val solutionDigit = abs(solution) % 10
        iterationItem[cacheIndex] = solutionDigit
        return solutionDigit
    }

    private fun emptyArray(): Array<Int?> {
        return Array(lastOutputElement - firstOutputElement) { null }
    }
}
