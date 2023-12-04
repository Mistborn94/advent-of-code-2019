package helper

class GraphSearchResult<K>(val end: K?, private val result: Map<K, SeenVertex<K>>) {

    fun getPath() = end?.let { getPath(it, emptyList()) } ?: throw IllegalStateException("No path found")

    private tailrec fun getPath(endVertex: K, pathEnd: List<K>): List<K> {
        val previous = result[endVertex]?.prev

        return if (previous == null) {
            listOf(endVertex) + pathEnd
        } else {
            getPath(previous, listOf(endVertex) + pathEnd)
        }
    }
}

data class SeenVertex<K>(val cost: Int, val prev: K?)

data class ScoredVertex<K>(val vertex: K, val score: Int, val heuristic: Int) : Comparable<ScoredVertex<K>> {
    override fun compareTo(other: ScoredVertex<K>): Int = (score + heuristic).compareTo(other.score + other.heuristic)
}
