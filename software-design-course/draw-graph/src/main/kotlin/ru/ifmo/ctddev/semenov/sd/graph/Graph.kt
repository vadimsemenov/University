package ru.ifmo.ctddev.semenov.sd.graph



interface Graph {
    fun addEdge(edge: Edge)
    fun removeEdge(edge: Edge)
    fun exists(edge: Edge): Boolean
    fun getOutgoing(vertex: Vertex): List<Edge>
}

data class Vertex(val name: String)

data class Edge(val from: Vertex, val to: Vertex)

abstract class AbstractGraph: Graph {
    private val vertexMapping: MutableMap<Vertex, Int> = hashMapOf()
    private val vertices: MutableList<Vertex> = arrayListOf()

    fun exists(vertex: Vertex) = vertexMapping.containsKey(vertex)

    protected fun vertex(vertex: Vertex): Int = vertexMapping.computeIfAbsent(vertex) {
        checkSize(vertices.size + 1)
        vertices += vertex
        vertices.size - 1
    }

    protected open fun newVertex(vertex: Vertex) {
    }

    protected open fun checkSize(verticesQty: Int) {
    }

    protected fun checkVertex(vertex: Vertex) {
        if (!vertexMapping.containsKey(vertex)) {
            throw IllegalArgumentException("Unknown $vertex")
        }
    }
}

class MatrixGraph(val verticesQty: Int): AbstractGraph() {
    private val graph: Array<Array<Edge?>> = Array(verticesQty) { Array(verticesQty) { null as Edge? } }

    override fun addEdge(edge: Edge) {
        graph[vertex(edge.from)][vertex(edge.to)] = edge
    }

    override fun removeEdge(edge: Edge) {
        checkVertex(edge.from)
        checkVertex(edge.to)
        val (from, to) = Pair(vertex(edge.from), vertex(edge.to))
        if (graph[from][to] != edge) {
            throw IllegalArgumentException("There's ${graph[from][to]} but not $edge")
        }
        graph[from][to] = null
    }

    override fun exists(edge: Edge): Boolean = exists(edge.from) && exists(edge.to) &&
            graph[vertex(edge.from)][vertex(edge.to)] != null

    override fun getOutgoing(vertex: Vertex): List<Edge> {
        checkVertex(vertex)
        val v = vertex(vertex)
        return graph[v].filter { it != null }.map { it!! }
    }

    override fun checkSize(verticesQty: Int) {
        if (verticesQty >= this.verticesQty) {
            throw IllegalStateException("Too many vertices")
        }
    }
}

class AdjacencyListGraph: AbstractGraph() {
    private val adjacencyList: MutableList<MutableList<Edge>> = arrayListOf()

    override fun addEdge(edge: Edge) {
        adjacencyList[vertex(edge.from)].add(edge)
    }

    override fun removeEdge(edge: Edge) {
        if (exists(edge.from) && exists(edge.to)) {
            adjacencyList[vertex(edge.from)].remove(edge)
        }
    }

    override fun exists(edge: Edge): Boolean = exists(edge.from) && exists(edge.to) &&
            adjacencyList[vertex(edge.from)].contains(edge)

    override fun getOutgoing(vertex: Vertex): List<Edge> {
        checkVertex(vertex)
        return adjacencyList[vertex(vertex)]
    }

    override fun newVertex(vertex: Vertex) {
        super.newVertex(vertex)
        adjacencyList[vertex(vertex)] = arrayListOf()
    }
}
