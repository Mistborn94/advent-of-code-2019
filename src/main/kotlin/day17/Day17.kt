package day17

import day5.IntCode
import helper.*
import java.util.*
import kotlin.collections.set

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
//
//    val compressed = compressCommands(commands)
//
//    val instructionLength = commands.size
//    val compressedLength = compressed.length

    operator fun plus(command: Command): Function {
        return Function(commands + command)
    }
}

//val Function?.compressedLength get() = this?.compressedLength ?: 0

data class Functions(
    val final: List<Char> = listOf(),
    val a: Function,
    val b: Function,
    val c: Function
) {
//    override fun toString(): String {
//        return "${final.joinToString(separator = ",")}\n" +
//                "${a.compressed}\n" +
//                "${b.compressed}\n" +
//                "${c.compressed}\n"
//    }
}

sealed class Command {

    abstract override fun toString(): String
    abstract fun append(next: Command): Command
    abstract fun newState(position: Point, direction: Direction): RobotState
    open fun newState(robotState: RobotState): RobotState = newState(robotState.position, robotState.direction)

    sealed class Turn : Command() {
        override fun append(next: Command): Command {
            return Composite(listOf(this, next))
        }

        object Left : Turn() {
            override fun toString(): String = "L"
            override fun newState(position: Point, direction: Direction): RobotState =
                RobotState(position, direction.left)
        }

        object Right : Turn() {
            override fun toString(): String = "R"
            override fun newState(position: Point, direction: Direction): RobotState =
                RobotState(position, direction.right)
        }
    }

    sealed class Straight : Command() {
        abstract val distance: Int
        override fun append(next: Command): Command {
            return if (next is Straight) {
                MultiStraight(distance + next.distance)
            } else {
                Composite(listOf(this, next)).simplify()
            }
        }

        object SingleStraight : Straight() {
            override val distance: Int = 1

            override fun newState(position: Point, direction: Direction): RobotState =
                RobotState(position + direction.point, direction)

            override fun toString(): String = "$distance"
        }

        data class MultiStraight(override val distance: Int) : Straight() {

            override fun newState(position: Point, direction: Direction): RobotState =
                RobotState(position + (direction.point * distance), direction)

            override fun toString(): String = "$distance"
        }
    }

    data class Composite(val commands: List<Command>) : Command() {
        override fun toString(): String = commands.joinToString(separator = ",")

        override fun append(next: Command): Command {
            return if (next is Composite) {
                Composite(this.commands + next.commands).simplify()
            } else {
                Composite(this.commands + next).simplify()
            }.simplify()
        }

        fun simplify(): Composite {
            val newCommands = mutableListOf<Command>()
            val oldCommands = LinkedList(commands)
            while (oldCommands.isNotEmpty()) {
                val nextCommand = oldCommands.remove()
                if (nextCommand is Composite) {
                    oldCommands.addAll(0, nextCommand.commands)
                } else if (newCommands.isEmpty()) {
                    newCommands.add(nextCommand)
                } else if (nextCommand is Straight && newCommands.last() is Straight) {
                    val removed = newCommands.removeAt(newCommands.lastIndex)
                    newCommands.add(removed.append(nextCommand))
                } else {
                    newCommands.add(nextCommand)
                }
            }
            return Composite(newCommands)
        }

        override fun newState(robotState: RobotState): RobotState {
            return commands.fold(robotState) { (position, direction), command ->
                command.newState(
                    position,
                    direction
                )
            }
        }

        override fun newState(position: Point, direction: Direction): RobotState =
            newState(RobotState(position, direction))
    }
}

fun List<Command>.compress() = reduce { acc, next -> acc.append(next) }

data class RobotState(val position: Point, val direction: Direction) {

    fun neighbours(map: List<List<Char>>): List<RobotStep> {
        return possibleNeighbours(this, null, map).mapNotNull { explorePath(map, it) }
    }

    private fun RobotState.explorePath(
        map: List<List<Char>>,
        start: Command
    ): RobotStep? {
        if (start is Command.Turn) {
            return RobotStep.TurnStep(start.newState(this), start)
        } else {
            var possibleNeighbours = setOf(start)
            var previousPoint: Point? = null
            var currentState = this
            val commands = mutableListOf<Command>()
            while (possibleNeighbours.size == 1) {
                val command = possibleNeighbours.first()
                commands.add(command)
                if (command is Command.Straight) {
                    previousPoint = currentState.position
                }
                currentState = command.newState(currentState)
                possibleNeighbours = possibleNeighbours(currentState, previousPoint, map)
            }
            val exploredStep = if (commands.isEmpty()) {
                null
            } else {
                RobotStep.EdgeStep(map[previousPoint!!], currentState, commands.compress())
            }
            return exploredStep
        }
    }

    private fun possibleNeighbours(
        currentState: RobotState,
        previousPoint: Point?,
        map: List<List<Char>>
    ): Set<Command> = mapOf(
        Command.Straight.SingleStraight to currentState.position + currentState.direction.point,
        Command.Turn.Left to currentState.position + currentState.direction.left.point,
        Command.Turn.Right to currentState.position + currentState.direction.right.point
    ).filterValues { it != previousPoint && map[it] != '.' }.keys
}

sealed class RobotStep(val endState: RobotState, val command: Command) {
    class EdgeStep(val edge: Char, endState: RobotState, command: Command) : RobotStep(endState, command) {
        override fun toString(): String {
            return "EdgeStep($edge)"
        }
    }

    class TurnStep(endState: RobotState, command: Command.Turn) : RobotStep(endState, command) {
        override fun toString(): String {
            return "TurnStep($command)"
        }
    }
}

fun solveB(program: List<Long>): Long {
    val a = "R,12,L,10,R,12"
    val b = "L,8,R,10,R,6"
    val c = "R,12,L,10,R,10,L,8"
    val solution = "A,B,A,C,B,C,B,C,A,C"

    val mutableProgram = program.toMutableList()
    mutableProgram[0] = 2L
    val intCode = IntCode(mutableProgram) { println("Waiting for input") }
    intCode.runUtilInput()
    intCode.sendAscii(listOf(solution, a, b, c, "n"))
    intCode.runUtilInput()
    val outputList = intCode.outputs.drainToList()

    println(outputList.last())
    return outputList.last()
}

val possibleStartChars = listOf('^', '<', '>', 'v')
fun solveBDynamic(program: List<Long>): Long {
    //Main Routine: A,B,C ... \n   -> Max 20 chars / 10 operations
    //Function (x3) L,R,4 .... \n  -> Max 20 chars / 10 operations
    //Video y/n\n
    val mutableProgram = program.toMutableList()
    mutableProgram[0] = 2L
    val intCode = IntCode(mutableProgram) { println("Waiting for input") }
    intCode.runUtilInput()
    val outputList = intCode.outputs.drainToList().map { it.toChar() }
//    println(outputList.joinToString(separator = ""))
//    println("Scaffold Count " + outputList.count { it == '#' })
    val map = padMap(buildMap(outputList))
    val intersections = findIntersections(map)
    val start = findStart(map)
    val (labelledMap, labelRange) = labelEdges(start, intersections, map.map { it.toMutableList() })
    val graph = buildGraph(intersections + start.position, labelledMap)
    val edgeCount = labelRange.last - labelRange.first + 1

    val paths = findPaths(start, graph, edgeCount).map { reconstructPath(graph, it.getPath()) }.take(5)

    paths.forEach {
        println("Found path: $it")
    }
//    val commands = commandList(lines)
//    val functions = findFunctions(commands)
//
//    val asciiCommand = functions.toString() + "n\n"
//    intCode.inputs.addAll(asciiCommand.toCharArray().map { it.toLong() })
//    intCode.runProgram()
//    return intCode.outputs.last()
    return 0
}

fun labelEdges(
    start: RobotState,
    intersections: List<Point>,
    newMap: List<MutableList<Char>>
): Pair<List<List<Char>>, CharRange> {
    var lastLabel = 'A'

    val visited = mutableSetOf<Point>()
    val toVisit = LinkedList<Pair<Point, Char?>>()
    toVisit.add(start.position to lastLabel)

    while (toVisit.isNotEmpty()) {
        val (next, label) = toVisit.remove()
        if (next !in visited) {
            val possibleNeighbours = next.neighbours().filter { newMap[it] == '#' }
            if (next in intersections) {
                newMap[next] = '*'
                toVisit.addAll(possibleNeighbours.map { it to null })
            } else {
                val effectiveLabel = label ?: ++lastLabel
                newMap[next] = effectiveLabel
                toVisit.addAll(0, possibleNeighbours.map { it to effectiveLabel })
            }
            visited.add(next)
        }
    }

    return newMap to 'A'..lastLabel
}

data class SearchState(val robot: RobotState, val seenEdges: Set<Char>)

fun findPaths(
    start: RobotState,
    graph: Map<RobotState, List<RobotStep>>,
    edgeCount: Int
): Sequence<GraphSearchResult<SearchState>> {

    return sequence {
        val startState = SearchState(start, emptySet())
        val endCondition: (SearchState) -> Boolean = { it.seenEdges.size == edgeCount }

        val toVisit = PriorityQueue<ScoredVertex<SearchState>>(
            listOf(ScoredVertex(startState, 0, edgeCount))
        )
        val seenPoints = mutableMapOf(
            startState to SeenVertex<SearchState>(0, null)
        )
        while (toVisit.isNotEmpty<ScoredVertex<SearchState>>()) {
            val (currentVertex, currentScore) = toVisit.remove()
            val endVertex = if (endCondition(currentVertex)) currentVertex else null

            val nextPoints = searchStateNeighbours(graph, currentVertex)
                .filter { it !in seenPoints }
                .map { next ->
                    ScoredVertex(
                        next,
                        currentScore + 1,
                        edgeCount - next.seenEdges.size
                    )
                }

            toVisit.addAll(nextPoints)
            seenPoints.putAll(nextPoints.associate {
                it.vertex to SeenVertex(
                    it.score,
                    currentVertex
                )
            })
            if (endVertex != null) {
                yield(GraphSearchResult(endVertex, seenPoints))
            }
        }
    }
}

private fun searchStateNeighbours(graph: Map<RobotState, List<RobotStep>>, searchState: SearchState): List<SearchState> {
    return (graph[searchState.robot] ?: emptyList()).map { step ->
        SearchState(
            step.endState,
            if (step is RobotStep.EdgeStep) {
                searchState.seenEdges + step.edge
            } else {
                searchState.seenEdges
            }
        )
    }
}

fun reconstructPath(graph: Map<RobotState, List<RobotStep>>, searchStates: List<SearchState>): Command {
    val commands = searchStates.windowed(2).map { (first, second) ->
        graph[first.robot]!!.first { it.endState == second.robot }.command
    }
    val composite = Command.Composite(commands)
    val simplified = composite.simplify()
    return simplified
}


fun padMap(map: List<List<Char>>): List<List<Char>> {
    val sidesPadded = map.map {
        val mutableList = it.toMutableList()
        mutableList.add(0, '.')
        mutableList.add('.')
        mutableList
    }

    val width = sidesPadded[0].size
    val newRow = Array(width) { '.' }.toList()
    val newMap = mutableListOf(newRow)
    newMap.addAll(sidesPadded)
    newMap.add(newRow)

    return newMap
}

fun buildGraph(points: List<Point>, map: List<List<Char>>): Map<RobotState, List<RobotStep>> {
    val graph = mutableMapOf<RobotState, List<RobotStep>>()
    val toVisit = points.flatMapTo(mutableSetOf()) {
        listOf(
            RobotState(it, Direction.DOWN),
            RobotState(it, Direction.UP),
            RobotState(it, Direction.LEFT),
            RobotState(it, Direction.RIGHT)
        )
    }

    while (toVisit.isNotEmpty()) {
        val state = toVisit.first()
        toVisit.remove(state)
        if (state !in graph) {
            val neighbours = state.neighbours(map)
            graph[state] = neighbours
        }
    }

    return graph
}

fun min(a: Point, b: Point): Point = when {
    a.x < b.x -> a
    a.x > b.x -> b
    a.y < b.y -> a
    else -> b
}

fun max(a: Point, b: Point): Point = when {
    a.x > b.x -> a
    a.x < b.x -> b
    a.y > b.y -> a
    else -> b
}

val directions = mapOf(
    '<' to Direction.LEFT,
    '^' to Direction.UP,
    '>' to Direction.RIGHT,
    'v' to Direction.DOWN
)

fun sumIntersections(intersections: List<Point>) =
    intersections.sumBy { it.x * it.y }

fun findIntersections(lines: List<List<Char>>): List<Point> {
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

private fun findStart(map: List<List<Char>>): RobotState {
    val position = map.mapIndexedNotNull { y, value ->
        val x = value.indexOfFirst { it in possibleStartChars }
        if (x > -1) {
            Point(x, y)
        } else {
            null
        }
    }.first()
    val direction = directions.getValue(map[position])
    return RobotState(position, direction)
}

private fun buildMap(outputList: List<Char>): List<List<Char>> {
    val firstLineEnd = outputList.indexOf('\n')
    return outputList.filter { it != '\n' }.chunked(firstLineEnd)
}


operator fun <T> List<List<T>>.get(point: Point) = this[point.y][point.x]
operator fun <T> List<MutableList<T>>.set(point: Point, value: T) {
    this[point.y][point.x] = value
}