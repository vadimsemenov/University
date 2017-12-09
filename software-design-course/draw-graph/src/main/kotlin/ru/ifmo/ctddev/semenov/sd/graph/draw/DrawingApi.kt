package ru.ifmo.ctddev.semenov.sd.graph.draw

import kotlin.math.sqrt


interface DrawingApi {
    fun getDrawingAreaWidth(): Int
    fun getDrawingAreaHeight(): Int
    fun drawCircle(circle: Circle, label: String? = null)
    fun drawLine(a: Point, b: Point)
}

data class Point(val x: Double, val y: Double) {
    constructor(x: Int, y: Int) : this(x.toDouble(), y.toDouble())

    fun norm(): Point = sqrt(x * x + y * y).let { this / it }

    operator fun plus(other: Point): Point = Point(x + other.x, y + other.y)
    operator fun minus(other: Point): Point = Point(x - other.x, y - other.y)
    operator fun times(scalar: Double): Point = Point(x * scalar, y * scalar)
    operator fun div(scalar: Double): Point = Point(x / scalar, y / scalar)
}

data class Circle(val center: Point, val radius: Double)