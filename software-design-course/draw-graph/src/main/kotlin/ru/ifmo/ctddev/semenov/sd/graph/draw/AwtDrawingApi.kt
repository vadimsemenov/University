package ru.ifmo.ctddev.semenov.sd.graph.draw

import java.awt.Color
import java.awt.Graphics2D


class AwtDrawingApi(private val graphics: Graphics2D, private val width: Int, private val height: Int): DrawingApi {
    private val circleColor = Color.RED!!
    private val lineColor = Color.DARK_GRAY!!
    private val textColor = Color.BLACK!!

    override fun getDrawingAreaWidth(): Int = width

    override fun getDrawingAreaHeight(): Int = height

    override fun drawCircle(circle: Circle, label: String?) {
        graphics.paint = circleColor
        graphics.fill(java.awt.geom.Ellipse2D.Double(
                circle.center.x - circle.radius,
                circle.center.y - circle.radius,
                2 * circle.radius,
                2 * circle.radius))
        if (label != null) {
            graphics.paint = textColor
            graphics.drawString(label, (circle.center.x - circle.radius * 0.9).toFloat(), circle.center.y.toFloat())
        }
    }

    override fun drawLine(a: Point, b: Point) {
        graphics.paint = lineColor
        graphics.drawLine(a.x.toInt(), a.y.toInt(), b.x.toInt(), b.y.toInt())
    }
}