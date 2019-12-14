package day14

import kotlin.math.ceil

class RecipeGraph(val nodes: Collection<RecipeNode>) {

    class RecipeNode(val name: String, val createdQuantity: Int) {
        val inputs = mutableListOf<RecipeEdge>()
        val outputsTo = mutableListOf<RecipeEdge>()

        var requiredQuantity: Long = 0
        var traversed = false
            private set

        val recipeCount: Long
            get() = ceil(requiredQuantity / createdQuantity.toDouble()).toLong()

        val ready get() = !traversed && outputsTo.all { it.outputNode.traversed }

        fun traverse() {
            if (!ready) {
                throw IllegalStateException("Traversing non-ready node $name")
            }

            inputs.forEach { edge ->
                edge.inputNode.requiredQuantity += recipeCount * edge.requiredQuantity
            }
            traversed = true
        }

        fun reset() {
            traversed = false
            requiredQuantity = 0
        }
    }

    class RecipeEdge(val requiredQuantity: Int, val inputNode: RecipeNode, val outputNode: RecipeNode)

    operator fun get(node: String): RecipeNode = nodes.first { it.name == node }

    fun hasUntraversedNodes(): Boolean = nodes.any { !it.traversed }

    fun getReadyNode() = nodes.first { it.ready }

    fun reset() {
        nodes.forEach(RecipeNode::reset)
    }

    companion object {
        fun of(recipes: Map<String, Recipe>): RecipeGraph {
            val recipeNodes = recipes.mapValues { (_, value) -> createNode(value.output) } +
                    Pair(ore, RecipeNode(ore, 1))

            recipes.values.forEach {
                val outputNode = recipeNodes.getValue(it.output.name)

                it.inputs.forEach { input ->
                    val inputNode = recipeNodes.getValue(input.name)
                    val edge = RecipeEdge(input.quantity, inputNode, outputNode)
                    inputNode.outputsTo.add(edge)
                    outputNode.inputs.add(edge)
                }
            }

            return RecipeGraph(recipeNodes.values)
        }

        private fun createNode(recipeOutput: Recipe.RecipeItem) = RecipeNode(recipeOutput.name, recipeOutput.quantity)
    }
}