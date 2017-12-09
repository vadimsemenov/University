package ru.ifmo.ctddev.semenov.sd.graph.draw

import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color


class FxDrawingApi(private val graphicsContext: GraphicsContext): DrawingApi {
    private val circleColor = Color.RED!!
    private val lineColor = Color.DARKGRAY!!
    private val textColor = Color.BLACK!!
    override fun getDrawingAreaWidth(): Int = graphicsContext.canvas.width.toInt()

    override fun getDrawingAreaHeight(): Int = graphicsContext.canvas.height.toInt()

    override fun drawCircle(circle: Circle, label: String?) {
        graphicsContext.fill = circleColor
        graphicsContext.fillOval(
                circle.center.x - circle.radius,
                circle.center.y - circle.radius,
                circle.radius * 2,
                circle.radius * 2
        )
        if (label != null) {
            graphicsContext.fill = textColor
            graphicsContext.fillText(
                    label,
                    (circle.center.x - circle.radius * 0.9),
                    circle.center.y
            )
        }
    }

    override fun drawLine(a: Point, b: Point) {
        graphicsContext.fill = lineColor
        graphicsContext.strokeLine(a.x, a.y, b.x, b.y)
    }

}