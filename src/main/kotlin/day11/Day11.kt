package day11

import helper.Point
import day5.IntCode
import helper.Direction
import java.util.concurrent.BlockingQueue
import java.util.concurrent.CompletableFuture
import java.util.concurrent.LinkedBlockingQueue

data class Panel(var colour: Int, var painted: Boolean = false)

class Robot(
    val cameraQueue: BlockingQueue<Long>,
    val instructionQueue: BlockingQueue<Long>,
    startingColour: Int = 0
) {
    val hull = mutableMapOf<Point, Panel>(Point(0, 0) to Panel(startingColour)).withDefault { Panel(0) }

    @Volatile
    var running = true

    var currentPosition = Point(0, 0)
    var currentDirection = Direction.UP

    fun run() {
        while (running) {
            val panel = hull.getOrPut(currentPosition) { Panel(0) }
            cameraQueue.put(panel.colour.toLong())

            val newColour = instructionQueue.take()
            panel.colour = newColour.toInt()
            panel.painted = true

            val newDirection = instructionQueue.take()
            currentDirection = if (newDirection == 0L) {
                currentDirection.left
            } else {
                currentDirection.right
            }
            currentPosition += currentDirection.point
        }
    }
}

fun solveA(program: List<Long>): Int {

    val instructionQueue = LinkedBlockingQueue<Long>()
    val cameraQueue = LinkedBlockingQueue<Long>()
    val intcode = IntCode(program, cameraQueue, instructionQueue)
    val robot = Robot(cameraQueue, instructionQueue)

    val robotFuture = CompletableFuture.runAsync { robot.run() }
    CompletableFuture.runAsync { intcode.runProgram() }.get()

    robot.running = false
    robotFuture.cancel(true)

    return robot.hull.values.count { it.painted }
}

fun solveB(program: List<Long>): String {

    val instructionQueue = LinkedBlockingQueue<Long>()
    val cameraQueue = LinkedBlockingQueue<Long>()
    val intcode = IntCode(program, cameraQueue, instructionQueue)
    val robot = Robot(cameraQueue, instructionQueue, 1)

    val robotFuture = CompletableFuture.runAsync { robot.run() }
    CompletableFuture.runAsync { intcode.runProgram() }.get()

    robot.running = false
    robotFuture.cancel(true)

    val minX = robot.hull.keys.minBy { it.x }!!.x
    val minY = robot.hull.keys.minBy { it.y }!!.y

    val maxX = robot.hull.keys.maxBy { it.x }!!.x
    val maxY = robot.hull.keys.maxBy { it.y }!!.y

    return (minY..maxY).joinToString(separator = "\n") { y ->
        (minX..maxX).map { x ->
            robot.hull.getValue(Point(x, y)).colour
        }.joinToString(separator = "")
    }

}