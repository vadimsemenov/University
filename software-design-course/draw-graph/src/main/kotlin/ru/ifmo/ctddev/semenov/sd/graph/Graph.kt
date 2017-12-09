package ru.ifmo.ctddev.semenov.sd.graph

import ru.ifmo.ctddev.semenov.sd.graph.draw.*
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin


interface Graph {
    fun addEdge(edge: Edge)
    fun removeEdge(edge: Edge)
    fun exists(edge: Edge): Boolean
    fun getOutgoing(vertex: Vertex): List<Edge>
}

data class Vertex(val name: String)

data class Edge(val from: Vertex, val to: Vertex)

abstract class AbstractDrawableGraph: Graph {
    lateinit var drawingApi: DrawingApi
    var vertexRadius = 50

    private val vertexMapping: MutableMap<Vertex, Int> = hashMapOf()
    private val vertices: MutableList<Vertex> = arrayListOf()

    fun drawGraph() {
        val bound = Point(drawingApi.getDrawingAreaWidth(), drawingApi.getDrawingAreaHeight())
        val radius = min(bound.x, bound.y) / 2 - 2 * vertexRadius
        if (radius < 0) throw IllegalStateException("Too small area")
        val center = bound / 2.0
        val centers = (0 until vertices.size).map {
            val angle = it * 2 * Math.PI / vertices.size
            val circle = Circle(center + Point(cos(angle), sin(angle)) * radius,
                    vertexRadius.toDouble())
            drawingApi.drawCircle(circle, vertices[it].name)
            circle
        }
        vertices.forEach {
            getOutgoing(it).forEach {
                val fromCenter = centers[vertex(it.from)]
                val toCenter   = centers[vertex(it.to)]
                val a = fromCenter.center - (fromCenter.center - center).norm() * vertexRadius.toDouble()
                val b = toCenter.center - (toCenter.center - center).norm() * vertexRadius.toDouble()
                drawingApi.drawLine(a, b)
            }
        }
    }

    fun exists(vertex: Vertex) = vertexMapping.containsKey(vertex)

    protected fun vertex(vertex: Vertex): Int = vertexMapping.computeIfAbsent(vertex) {
        checkSize(vertices.size + 1)
        newVertex(vertex, vertices.size)
        vertices += vertex
        vertices.size - 1
    }

    protected open fun newVertex(vertex: Vertex, id: Int) {
    }

    protected open fun checkSize(verticesQty: Int) {
    }

    protected fun checkVertex(vertex: Vertex) {
        if (!vertexMapping.containsKey(vertex)) {
            throw IllegalArgumentException("Unknown $vertex")
        }
    }
}

class MatrixGraph(private val verticesQty: Int): AbstractDrawableGraph() {
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

class AdjacencyListGraph: AbstractDrawableGraph() {
    private val adjacencyList: MutableList<MutableList<Edge>> = arrayListOf()

    override fun addEdge(edge: Edge) {
        val from = vertex(edge.from)
        vertex(edge.to)
        adjacencyList[from].add(edge)
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

    override fun newVertex(vertex: Vertex, id: Int) {
        super.newVertex(vertex, id)
        while (adjacencyList.size <= id) adjacencyList.add(arrayListOf())
    }
}
