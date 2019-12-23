package day17

import day5.IntCode
import helper.Direction
import helper.Point
import helper.drainToList
import kotlin.math.max

fun solveA(program: List<Long>): Int {

    val intCode = IntCode(program) { println("Waiting for input") }
    intCode.runProgram()

    val outputList = intCode.outputs.drainToList().map { it.toChar() }
    val lines = buildMap(outputList)

    val intersections = findIntersections(lines)

    return sumIntersections(intersections)
}

data class Function(val commands: List<Command>) {

    constructor(command: Command) : this(listOf(command))

    val compressed = compressCommands(commands)

    val instructionLength = commands.size
    val compressedLength = compressed.length

    operator fun plus(command: Command): Function {
        return Function(commands + command)
    }
}

val Function?.compressedLength get() = this?.compressedLength ?: 0

data class Functions(
    val final: List<Char> = listOf(),
    val a: Function,
    val b: Function,
    val c: Function
) {
    override fun toString(): String {
        return "${final.joinToString(separator = ",")}\n" +
                "${a.compressed}\n" +
                "${b.compressed}\n" +
                "${c.compressed}\n"
    }
}

val possibleStartChars = listOf('^', '<', '>', 'v')
fun solveB(program: List<Long>): Long? {
    //Main Routine: A,B,C ... \n   -> Max 20 chars / 10 operations
    //Function (x3) L,R,4 .... \n  -> Max 20 chars / 10 operations
    //Video y/n\n
    val intCode = IntCode(program) { println("Waiting for input") }
    intCode.runUtilInput()
    val outputList = intCode.outputs.drainToList().map { it.toChar() }
//    println(outputList.joinToString(separator = ""))
//    println("Scaffold Count " + outputList.count { it == '#' })

    val lines = buildMap(outputList)
    val commands = commandList(lines)
    val functions = findFunctions(commands)

    val asciiCommand = functions.toString() + "n\n"
    intCode.inputs.addAll(asciiCommand.toCharArray().map { it.toLong() })
    intCode.runProgram()
    return intCode.outputs.last()
}

fun findFunctions(commands: List<Command>): Functions {
    return findFunctions(commands, emptyList(), Function(commands[0]))
        ?: throw IllegalStateException("No paths found")
}

private val emptyFunction = Function(listOf())

fun findFunctions(
    commands: List<Command>,
    currentPath: List<Char> = emptyList(),
    a: Function,
    b: Function? = null,
    c: Function? = null,
    cFinal: Boolean = false
): Functions? {
    val aTrim = a.compressed
    val bTrim = b?.compressed
    val cTrim = c?.compressed
    val pathLog = currentPath.joinToString(separator = ",")
    val commandString = Function(commands).compressed

    if (a.compressedLength > 20 || b.compressedLength > 20 || c.compressedLength > 20) {
        return null
    }

    val verify = verify(commands, a, b ?: emptyFunction, c ?: emptyFunction, currentPath.size)

    if (verify != null) {
        val result = Functions(currentPath + verify, a, b ?: emptyFunction, c ?: emptyFunction)
//        println("Verified: $aTrim, $bTrim, $cTrim: $pathLog, $commandString")
//        println(result)
        return result
    }
    //Verify current
    //Compressedlenghts +
    //If working, create return

    return sequence {
        if (b != null && commands.startsWith(a.commands)) {
            yield(findFunctions(commands.subList(a.instructionLength, commands.size), currentPath + 'A', a, b, c))
        }

        if (b != null && c != null && commands.startsWith(b.commands)) {
            yield(findFunctions(commands.subList(b.instructionLength, commands.size), currentPath + 'B', a, b, c))
        }

        if (c != null && commands.startsWith(c.commands)) {
//            println("Restarting C $cTrim at $commandString,  A is $aTrim, B is $bTrim")
            yield(findFunctions(commands.subList(c.instructionLength, commands.size), currentPath + 'C', a, b, c, true))
        }

        if (b == null && c == null && commands.size > a.instructionLength) {
            val instructionLength = a.instructionLength
            val next = commands[instructionLength]

//            println("Continuing A $aTrim with $next at $instructionLength")
            yield(findFunctions(commands, currentPath, a + next))

//            println("Starting B with $next at $instructionLength; A is $aTrim")
            yield(
                findFunctions(
                    commands.subList(instructionLength, commands.size),
                    currentPath + 'A',
                    a,
                    Function(next)
                )
            )

        } else if (b != null && c == null && commands.size > b.instructionLength) {
            val instructionLength = b.instructionLength
            val next = commands[instructionLength]

//            println("Continuing B $bTrim  with $next at $instructionLength;  A is $aTrim")
            yield(findFunctions(commands, currentPath, a, b + next))

//            println("Starting C with $next at $instructionLength: $commandString;  A is $aTrim, B is $bTrim")
            yield(
                findFunctions(
                    commands.subList(instructionLength, commands.size), currentPath + 'B',
                    a, b, Function(next)
                )
            )
        } else if (!cFinal && b != null && c != null && commands.size > c.instructionLength) {
            val instructionLength = c.instructionLength
            val next = commands[instructionLength]

//            println("Continuing C $cTrim  with $next at $instructionLength: $commandString;  A is $aTrim, B is $bTrim")
            yield(findFunctions(commands, currentPath, a, b, c + next))
        }
    }.firstOrNull { it != null }
}

fun verify(commands: List<Command>, a: Function, b: Function, c: Function, depth: Int = 0): List<Char>? {
    val totalPossibleSize = max(max(a.instructionLength, b.instructionLength), c.instructionLength) * (10 - depth)

    return when {
        depth > 10 || totalPossibleSize < commands.size -> null
        commands.isEmpty() -> listOf()
        commands.startsWith(a.commands) -> {
            val verify = verify(commands.subList(a.instructionLength, commands.size), a, b, c, depth + 1)
            verify?.let { listOf('A') + it }
        }
        commands.startsWith(b.commands) -> {
            val verify = verify(commands.subList(b.instructionLength, commands.size), a, b, c, depth + 1)
            verify?.let { listOf('B') + it }
        }
        commands.startsWith(c.commands) -> {
            val verify = verify(commands.subList(c.instructionLength, commands.size), a, b, c, depth + 1)
            verify?.let { listOf('C') + it }
        }
        else -> null
    }
}

private fun <E> List<E>.startsWith(other: List<E>): Boolean {
    return other.isNotEmpty() && size >= other.size && other.indices.all { this[it] == other[it] }
}

val directions = mapOf(
    '<' to Direction.LEFT,
    '^' to Direction.UP,
    '>' to Direction.RIGHT,
    'v' to Direction.DOWN
)

enum class Command(val letter: Char) {
    LEFT('L'),
    RIGHT('R'),
    STRAIGHT('S')
}

fun compressCommands(commandList: List<Command>): String {
    var mutableList = commandList
    return buildString {
        while (mutableList.isNotEmpty()) {
            val first = mutableList.elementAt(0)
            mutableList =
                if (first == Command.STRAIGHT) {
                    val stopIndex = mutableList.indexOfFirst { it != first }
                    if (stopIndex == -1) {
                        append(mutableList.size)
                        emptyList()
                    } else {
                        append(stopIndex)
                        mutableList.subList(stopIndex, mutableList.size)
                    }
                } else {
                    append(first.letter)
                    mutableList.subList(1, mutableList.size)
                }
            append(',')
        }
    }.run { removeSuffix(",") }
}

fun commandList(map: List<List<Char>>): List<Command> {
    var currentPosition = findStartPosition(map)
    var currentDirection = directions.getValue(map[currentPosition])
    val instructions = mutableListOf<Command>()
    var done = false

    while (!done) {
        val straightNext = currentPosition + currentDirection.point
        val leftNext = currentPosition + currentDirection.left.point
        val rightNext = currentPosition + currentDirection.right.point

        if (inRange(map, straightNext) && map[straightNext] == '#') {
            currentPosition = straightNext
            instructions.add(Command.STRAIGHT)
        } else if (inRange(map, leftNext) && map[leftNext] == '#') {
            instructions.add(Command.LEFT)
            currentDirection = currentDirection.left
        } else if (inRange(map, rightNext) && map[rightNext] == '#') {
            instructions.add(Command.RIGHT)
            currentDirection = currentDirection.right
        } else {
            done = true
        }
    }
    return instructions
}

private fun findStartPosition(lines: List<List<Char>>): Point {
    val startingY = lines.indexOfFirst { value -> possibleStartChars.intersect(value).isNotEmpty() }
    return Point(lines[startingY].indexOfFirst { it in possibleStartChars }, startingY)
}

private fun buildMap(outputList: List<Char>): List<List<Char>> {
    val firstLineEnd = outputList.indexOf('\n')
    return outputList.filter { it != '\n' }.chunked(firstLineEnd)
}


fun sumIntersections(intersections: MutableList<Point>) =
    intersections.sumBy { it.x * it.y }

fun findIntersections(lines: List<List<Char>>): MutableList<Point> {
    val intersections = mutableListOf<Point>()
    for (y in 1 until lines.lastIndex) {
        for (x in 1 until lines[0].lastIndex) {
            if (lines[y][x] == '#' &&
                lines[y - 1][x] == '#' &&
                lines[y + 1][x] == '#' &&
                lines[y][x - 1] == '#' &&
                lines[y][x + 1] == '#'
            ) {
                intersections.add(Point(x, y))
            }
        }
    }
    return intersections
}

fun <T> inRange(list: List<List<T>>, point: Point): Boolean = point.y in list.indices && point.x in list[0].indices

operator fun <T> List<List<T>>.get(point: Point) = this[point.y][point.x]