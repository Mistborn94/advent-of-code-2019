package helper

import java.io.File

fun readInput(day: Int) = File("src/main/resources/day$day/input.txt")
fun readSampleInput(day: Int, id: Int = 1) = File("src/main/resources/day$day/sample_input_$id.txt")