package day4

fun solveA(min: Int, max: Int): Int {
    return (min..max).count {
        matchesA(it.toString())
    }

}

fun solveB(min: Int, max: Int): Int {
    return (min..max).count {
        matchesB(it.toString())
    }

}

fun matchesA(it: String): Boolean = isIncreasing(it) && hasAdjacentNumbers(it)

fun matchesB(it: String): Boolean = isIncreasing(it) && hasLonelyAdjacentNumbers(it)

fun hasAdjacentNumbers(it: String): Boolean {
    return it.zipWithNext().any { (a, b) ->
        a == b
    }
}

fun hasLonelyAdjacentNumbers(it: String): Boolean {

    var i = 0
    while (i < it.length - 1) {
        val c = it[i]

        var count = 1
        while ((i + count) < it.length && it[i + count] == c) {
            count++
        }

        if (count == 2) {
            return true
        }

        i += count
    }
    return false;
}

fun isIncreasing(it: String): Boolean {
    return it.zipWithNext().all { (a, b) ->
        a.toInt() <= b.toInt()
    }
}
