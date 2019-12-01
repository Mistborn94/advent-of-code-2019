package day1

import kotlin.math.max

fun solvePartA(lines: List<String>): Int {
    return lines.asSequence()
        .map(String::toInt)
        .map(::calculateFuel)
        .sum()
}

fun solvePartB(lines: List<String>): Int {
    return lines.asSequence()
        .map(String::toInt)
        .map(::calculateFuel)
        .map { it + calculateExtraFuel(it) }
        .sum()
}

fun calculateFuel(mass: Int) = max(0, mass / 3 - 2)

fun calculateExtraFuel(fuelMass: Int): Int {
    if (fuelMass <= 0) {
        return 0
    }

    val extraFuel = calculateFuel(fuelMass)

    return extraFuel + calculateExtraFuel(extraFuel)
}