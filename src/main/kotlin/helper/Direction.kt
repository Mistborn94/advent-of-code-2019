package helper

enum class Direction(val point: Point) {

    UP(Point(0, -1)) {
        override val right: Direction
            get() = RIGHT
        override val left: Direction
            get() = LEFT
    },
    RIGHT(Point(1, 0)) {
        override val right: Direction
            get() = DOWN
        override val left: Direction
            get() = UP
    },
    DOWN(Point(0, 1)) {
        override val right: Direction
            get() = LEFT
        override val left: Direction
            get() = RIGHT
    },
    LEFT(Point(-1, 0)) {
        override val right: Direction
            get() = UP
        override val left: Direction
            get() = DOWN
    };

    abstract val right: Direction
    abstract val left: Direction
}