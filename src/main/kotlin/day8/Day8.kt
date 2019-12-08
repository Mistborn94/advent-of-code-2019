package day8

fun String.count(char: Char) = this.count { it == char }

fun solveA(width: Int, height: Int, image: String): Int {
    val size = width * height

    val minLayer = image.chunked(size)
        .minBy { it.count('0') }!!

    return minLayer.count('1') * minLayer.count('2')
}

fun solveB(width: Int, height: Int, image: String): String {
    val size = width * height

    val layers = image.chunked(size)

    val finalImage = MutableList(size) { '2' }
    layers.forEach { layer ->
        for (i in finalImage.indices) {
            if (finalImage[i] == '2') {
                finalImage[i] = layer[i]
            }
        }
    }

    return finalImage.chunked(width).joinToString(separator = "\n") { it.joinToString(separator = "") }
}
