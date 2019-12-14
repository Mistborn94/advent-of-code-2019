package day14

class Recipe(val inputs: List<RecipeItem>, val output: RecipeItem) {

    companion object {
        fun of(definition: String): Recipe {
            val (inputs, output) = definition.split("=>")

            val recipeOutput = RecipeItem.of(output)
            val recipeInputs = inputs.split(", ").map(RecipeItem.Companion::of)

            return Recipe(recipeInputs, recipeOutput)
        }
    }

    class RecipeItem(val name: String, val quantity: Int) {
        companion object {
            private val pattern = "(\\d+) ([A-Z]+)".toRegex()

            fun of(definition: String): RecipeItem {
                val (quantity, item) = pattern.matchEntire(definition.trim())!!.destructured
                return RecipeItem(item, quantity.toInt())
            }
        }

        operator fun plus(other: RecipeItem): RecipeItem {
            if (other.name != this.name) {
                throw IllegalArgumentException("Trying to add unrelated items $name and ${other.name}")
            }
            return RecipeItem(name, quantity + other.quantity)
        }

        operator fun times(int: Int): RecipeItem {
            return RecipeItem(name, quantity * int)
        }
    }
}