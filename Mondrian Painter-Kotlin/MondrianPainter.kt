import java.awt.Color
import kotlin.random.Random

/**
 * The desired canvas width, which must be at least 4 times [MIN_RECTANGLE_WIDTH].
 */
const val REQUESTED_CANVAS_WIDTH = 400

/**
 * The desired canvas height, which must be at least 4 times [MIN_RECTANGLE_HEIGHT].
 */
const val REQUESTED_CANVAS_HEIGHT = 400

/**
 * The minimum width of a colored rectangle.
 */
const val MIN_RECTANGLE_WIDTH = 80

/**
 * The minimum height of a colored rectangle.
 */
const val MIN_RECTANGLE_HEIGHT = 80

const val SEED = 2500

open class MondrianPainter {

    protected val random = Random(SEED)

    protected val canvas =
        Canvas(
            "Mondrian Painter",
            this,
            REQUESTED_CANVAS_WIDTH,
            REQUESTED_CANVAS_HEIGHT,
        )

    /**
     * Performs a side-by-side split of the region specified by [x], [y],
     * [width], and [height], if it is wide enough to split, and calls
     * [doMondrian] on each of the two smaller regions. If the original
     * region is too narrow to split, no action is taken.
     *
     * @return true if the original region was wide enough to split, false
     * otherwise
     */
    protected fun splitLeftRight(x: Int, y: Int, width: Int, height: Int): Boolean {
        if (width > 0.5 * REQUESTED_CANVAS_WIDTH && height <= 0.5 * REQUESTED_CANVAS_HEIGHT) {
            val xOffset = random.nextInt(MIN_RECTANGLE_WIDTH, width - MIN_RECTANGLE_WIDTH)
            doMondrian(x, y, xOffset, height) // left region
            doMondrian(x + xOffset, y, width - xOffset, height) // right region
            return true
        } else {
            return false
        }
    }

    /**
     * Performs an over-under split of the region specified by [x], [y],
     * [width], and [height], if it is tall enough to split, and calls
     * [doMondrian] on each of the two smaller regions. If the original
     * region is too short to split, no action is taken.
     *
     * @return true if the original region was tall enough to split, false
     * otherwise
     */
    protected fun splitTopBottom(x: Int, y: Int, width: Int, height: Int): Boolean {
        if (height > 0.5 * REQUESTED_CANVAS_HEIGHT && width <= 0.5 * REQUESTED_CANVAS_WIDTH) {
            val yOffset = random.nextInt(MIN_RECTANGLE_HEIGHT, height - MIN_RECTANGLE_HEIGHT)
            doMondrian(x, y, width, yOffset) // top region
            doMondrian(x, y + yOffset, width, height - yOffset) // bottom region
            return true
        } else {
            return false
        }
    }

    /**
     * Performs a horizontal and vertical split of the region specified
     * by [x], [y], [width], and [height], if it is both wide and tall enough
     * to split, and calls [doMondrian] on each of the four smaller regions.
     * If the original region is too small to split, no action is taken.
     *
     * @return true if the original region could be split, false otherwise
     */
    protected fun split4Way(x: Int, y: Int, width: Int, height: Int): Boolean {
        if (width > 0.5 * REQUESTED_CANVAS_WIDTH && height > 0.5 * REQUESTED_CANVAS_HEIGHT) {
            val xOffset = random.nextInt(MIN_RECTANGLE_WIDTH, width - MIN_RECTANGLE_WIDTH)
            val yOffset = random.nextInt(MIN_RECTANGLE_HEIGHT, height - MIN_RECTANGLE_HEIGHT)
            doMondrian(x, y, xOffset, yOffset) // top-left region
            doMondrian(x + xOffset, y, width - xOffset, yOffset) // top-right region
            doMondrian(x, y + yOffset, xOffset, height - yOffset) // bottom-left region
            doMondrian(x + xOffset, y + yOffset, width - xOffset, height - yOffset) // bottom-right region
            return true
        } else {
            return false
        }
    }

    /**
     * Divides the region with the given [x] and [y] coordinates and having
     * width [width] and height [height] into one or more colored rectangles,
     * in the style of Piet Mondrian.
     */
    open fun doMondrian(
        x: Int,
        y: Int,
        width: Int,
        height: Int,
    ) {

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

        val color = random.nextInt(0, 2)
        val colors = mutableListOf({ Color.RED }, { Color.YELLOW }, { Color.BLUE })

        val fillColor = if (color == 0) {
            Color.WHITE
        } else {
            val randomColor = colors.random()
            randomColor()
        }

        canvas.drawRectangle(
            x,
            y,
            width,
            height,
            fillColor = fillColor,
            outlineColor = Color.BLACK,
        )
    }

    /**
     * Handles a click at the specified [x]-[y] coordinates.
     */
    fun handleClick(x: Int, y: Int) {
        recolorRectangle(x, y)
    }

    /**
     * Changes the fill color of the rectangle containing ([x], [y]).
     */
    open fun recolorRectangle(x: Int, y: Int) {
        val color = canvas.getColorAt(x, y)
        val colors = listOf(Color.RED, Color.YELLOW, Color.BLUE)
        var randomColor: Color
        do {
            randomColor = colors[Random.nextInt(colors.size)]
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
    MondrianPainter()
}
