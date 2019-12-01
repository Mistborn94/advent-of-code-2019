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
        .map(::calculateTotalFuel)
        .sum()
}

fun calculateFuel(mass: Int) = max(0, mass / 3 - 2)

fun calculateTotalFuel(mass: Int): Int {
    if (mass <= 0) {
        return 0
    }

    val fuel = calculateFuel(mass)

    return fuel + calculateTotalFuel(fuel)
}