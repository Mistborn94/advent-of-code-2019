package day6

class OrbitNode(val name: String, var orbitsAround: OrbitNode? = null) {
    val orbits: MutableList<OrbitNode> = mutableListOf()

    fun chainLength(): Int {
        val localOrbitAround = orbitsAround
        return if (localOrbitAround == null) {
            0
        } else {
            localOrbitAround.chainLength() + 1
        }
    }

    fun traverse(
        target: String,
        currentPath: Set<OrbitNode>,
        shortestPaths: MutableMap<String, Int>
    ): Boolean {
        val bestDistance = shortestPaths.getOrDefault(name, Int.MAX_VALUE)
        val currentDistance = currentPath.size

        if (currentDistance >= bestDistance) {
            return false
        }

        shortestPaths[name] = currentDistance

        if (name == target) {
            return true
        }

        val newPath = currentPath + this
        val unvisitedNeighbors = (orbits + orbitsAround).filterNotNull().filter { it !in newPath }
        return unvisitedNeighbors.any {
            it.traverse(target, newPath, shortestPaths)
        }
    }
}

class OrbitGraph {

    val allNodes: MutableMap<String, OrbitNode> = mutableMapOf()

    fun addOrbit(orbitDefinition: String) {
        val (center, orbit) = orbitDefinition.split(")")
        val centerNode = allNodes.getOrPut(center) { OrbitNode(center) }
        val orbitNode = allNodes.getOrPut(orbit) { OrbitNode(orbit) }

        linkNodes(centerNode, orbitNode)
    }

    private fun linkNodes(centerNode: OrbitNode, orbitNode: OrbitNode) {
        orbitNode.orbitsAround = centerNode
        centerNode.orbits.add(orbitNode)
    }

    fun countAllOrbits(): Int {
        return allNodes.values.sumBy { it.chainLength() }
    }

    fun pathBetween(from: String, to: String): Int {
        val entryNode = allNodes.getValue(from).orbitsAround!!
        val targetNode = allNodes.getValue(to).orbitsAround!!

        val shortestPaths = mutableMapOf<String, Int>()
        entryNode.traverse(targetNode.name, setOf(entryNode), shortestPaths)
        return shortestPaths.getValue(targetNode.name)
    }
}

fun solveA(orbits: List<String>): Int {
    val graph = OrbitGraph()

    orbits.forEach {
        graph.addOrbit(it)
    }

    return graph.countAllOrbits()
}

fun solveB(orbits: List<String>): Int {
    val graph = OrbitGraph();

    orbits.forEach {
        graph.addOrbit(it)
    }

    return graph.pathBetween("YOU", "SAN")
}