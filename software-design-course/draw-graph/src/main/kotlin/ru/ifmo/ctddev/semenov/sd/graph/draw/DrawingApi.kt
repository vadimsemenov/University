package ru.ifmo.ctddev.semenov.sd.graph.draw


interface DrawingApi {
    fun getDrawingAreaWidth(): Int
    fun getDrawingAreaHeigh(): Int
    fun drawCircle(circle: Circle, label: String? = null)
    fun drawLine(a: Point, b: Point)
}

data class Point(val x: Double, val y: Double)

data class Circle(val center: Point, val radius: Double)