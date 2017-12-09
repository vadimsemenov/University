package ru.ifmo.ctddev.semenov.sd.graph

import javafx.application.Application
import javafx.scene.Group
import javafx.scene.Scene
import javafx.stage.Stage
import ru.ifmo.ctddev.semenov.sd.graph.draw.AwtDrawingApi
import ru.ifmo.ctddev.semenov.sd.graph.draw.FxDrawingApi
import java.awt.*
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import javax.swing.JFrame
import javax.swing.JPanel
import kotlin.system.exitProcess


fun main(args: Array<String>) {
    if (args.size != 2 || args[0] !in arrayOf(matrix, list)) {
        printUsage()
    }

    GraphBuilder.graph = readGraph(args[0])
    when (args[1]) {
        awt  -> runAwt()
        fx   -> runFx()
        else -> printUsage()
    }
}

fun readGraph(type: String): AbstractDrawableGraph {
    var line: String?
    val graph = when (type) {
        matrix -> {
            do {
                line = readLine()?.trim()
            } while (line != null && line == "")
            MatrixGraph(line!!.toInt())
        }
        list -> AdjacencyListGraph()
        else -> throw IllegalStateException("Unknown graph type: $type")
    }
    line = readLine()?.trim()
    while (line != null) {
        val tokens = line.split(Regex("\\s+"))
        when {
            tokens.size == 1 -> graph.addVertex(Vertex(tokens[0]))
            tokens.size == 2 -> graph.addEdge(Edge(Vertex(tokens[0]), Vertex(tokens[1])))
            tokens.isNotEmpty() -> throw IllegalArgumentException("Too many tokens: $line")
        }
        line = readLine()?.trim()
    }
    return graph
}

val matrix = "matrix"
val list = "list"
val awt = "awt"
val fx = "fx"
val usage = "Usage: <main> ($matrix|$list) ($awt|$fx)"

private fun printUsage() {
    println(usage)
    exitProcess(-1)
}

object GraphBuilder {
    var graph: AbstractDrawableGraph? = null
}

fun runAwt() {
    class AwtDrawer : JFrame() {
        fun run() {
            contentPane.add(object : JPanel() {
                override fun paint(g: Graphics?) {
                    g!!
                    val drawingApi = AwtDrawingApi(g as Graphics2D, width, height)
                    val graph = GraphBuilder.graph!!
                    graph.drawingApi = drawingApi
                    graph.drawGraph()
                }
            })
            addWindowListener(object : WindowAdapter() {
                override fun windowClosing(e: WindowEvent?) {
                    System.exit(0)
                }
            })

            setSize(1920, 1080)
            isVisible = true
        }
    }

    AwtDrawer().run()
}

fun runFx() {
    class FxDrawer : Application() {
        override fun start(primaryStage: Stage?) {
            primaryStage!!
            primaryStage.title = "Fx Graph Drawer"
            val root = Group()
            val canvas = javafx.scene.canvas.Canvas(1920.0, 1080.0)

            val graph = GraphBuilder.graph!!
            graph.drawingApi = FxDrawingApi(canvas.graphicsContext2D)
            graph.drawGraph()

            root.children.add(canvas)
            primaryStage.scene = Scene(root)
            primaryStage.show()
        }
    }

    Application.launch(FxDrawer::class.java)
}