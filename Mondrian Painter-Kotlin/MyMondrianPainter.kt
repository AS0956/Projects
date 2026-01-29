import java.awt.Color
import kotlin.random.Random

class MyMondrianPainter : MondrianPainter() {

    override fun doMondrian(x: Int, y: Int, width: Int, height: Int) {
        if (split4Way(x, y, width, height)) {
            return
        } else if (splitLeftRight(x, y, width, height)) {
            return
        } else if (splitTopBottom(x, y, width, height)) {
            return
        } else {
            val split = listOf(
                { splitLeftRight(x, y, width, height) },
                { splitTopBottom(x, y, width, height) },
                { split4Way(x, y, width, height) },
            )
            val randomSplit = split.random()
            randomSplit()
        }

        val fillColor = Color(random.nextInt(256), random.nextInt(256), random.nextInt(256))

        canvas.drawRectangle(
            x,
            y,
            width,
            height,
            fillColor = fillColor,
            outlineColor = Color.BLACK,
        )
    }

    override fun recolorRectangle(x: Int, y: Int) {
        val color = canvas.getColorAt(x, y)
        var randomColor: Color
        do {
            randomColor = Color(random.nextInt(256), random.nextInt(256), random.nextInt(256))
        } while (color == randomColor)

        var leftX = x
        while (canvas.getColorAt(leftX, y) != Color.BLACK) {
            leftX--
        }

        var topY = y
        while (canvas.getColorAt(x, topY) != Color.BLACK) {
            topY--
        }

        var rightX = x
        while (canvas.getColorAt(rightX, y) != Color.BLACK && rightX + 1 < canvas.width) {
            rightX++
        }

        var bottomY = y
        while (canvas.getColorAt(x, bottomY) != Color.BLACK && bottomY + 1 < canvas.height) {
            bottomY++
        }

        canvas.drawRectangle(leftX, topY, rightX - leftX, bottomY - topY, Color.BLACK, randomColor)
    }

}

/**
 * Creates a canvas and paints it in the style of Piet Mondrian.
 */
fun main() {
    require(REQUESTED_CANVAS_HEIGHT >= 4 * MIN_RECTANGLE_HEIGHT)
    require(REQUESTED_CANVAS_WIDTH >= 4 * MIN_RECTANGLE_WIDTH)
    MyMondrianPainter()
}
