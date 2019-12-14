package day14

import java.lang.Long.max

const val product = "FUEL"
const val ore = "ORE"
const val availableOre = 1_000_000_000_000

fun solveA(recipeDefinitions: List<String>): Int {

    val recipes = recipeDefinitions.map { Recipe.of(it) }
        .groupBy { it.output.name }
        .mapValues { (_, value) -> value[0] }

    val recipeGraph = RecipeGraph.of(recipes)

    return findRequiredOre(recipeGraph, 1).toInt()
}

fun solveB(recipeDefinitions: List<String>): Long {
    val recipes = recipeDefinitions.map { Recipe.of(it) }
        .groupBy { it.output.name }
        .mapValues { (_, value) -> value[0] }

    val recipeGraph = RecipeGraph.of(recipes)

    val requiredOreForOne = findRequiredOre(recipeGraph, 1)

    var fuelEstimate = availableOre / requiredOreForOne
    var requiredOre = findRequiredOre(recipeGraph, fuelEstimate)

    while (requiredOre < availableOre) {
        val mismatch = max((fuelEstimate - requiredOre) / requiredOreForOne, 1)
        fuelEstimate += mismatch
        requiredOre = findRequiredOre(recipeGraph, fuelEstimate)
    }

    while (requiredOre > availableOre) {
        fuelEstimate -= 1
        requiredOre = findRequiredOre(recipeGraph, fuelEstimate)
    }

    return fuelEstimate
}

fun findRequiredOre(recipeGraph: RecipeGraph, requiredFuel: Long): Long {
    recipeGraph.reset()
    val fuelNode = recipeGraph[product]
    fuelNode.requiredQuantity = requiredFuel

    while (recipeGraph.hasUntraversedNodes()) {
        recipeGraph.getReadyNode().traverse()
    }

    return recipeGraph[ore].requiredQuantity
}