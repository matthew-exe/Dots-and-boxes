package uk.ac.bournemouth.ap.dotsandboxes

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import com.google.android.material.snackbar.Snackbar
import org.example.student.dotsandboxes.StudentDotsBoxGame
import org.example.student.dotsboxgame.SimpleHumanPlayer
import uk.ac.bournemouth.ap.dotsandboxeslib.DotsAndBoxesGame
import uk.ac.bournemouth.ap.dotsandboxeslib.NamedPlayer
import uk.ac.bournemouth.ap.dotsandboxeslib.Player


class GameView(context: Context?, game: StudentDotsBoxGame) : View(context) {
    constructor(context: Context?) : this(context, StudentDotsBoxGame())
    constructor(context: Context?, attrs: AttributeSet?) : this(context, StudentDotsBoxGame())
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) :
            this(context, StudentDotsBoxGame())

    var game: StudentDotsBoxGame = game
        set(value) {
            field = value
            recalculateDimensions()
            invalidate()
        }

    private val gameOverListener = object : DotsAndBoxesGame.GameOverListener {
        override fun onGameOver(game: DotsAndBoxesGame, scores: List<Pair<Player, Int>>) {
            Log.d("GameOver", "Game over detected")
            val winner = scores.maxByOrNull { it.second }?.first
            val winnerName = when (winner) {
                is SimpleHumanPlayer    -> winner.name
                is SimpleComputerPlayer -> winner.name
                else                    -> "Unknown player"
            }
            showEndGameDialog(winnerName)

        }
    }
    init {
        game.addOnGameOverListener(gameOverListener)
    }

    private val columns = game.boxes.maxWidth
    private var lineLength: Float = 0f
    private var lineWidth: Float = 10f
    private val clickDetectionArea: Float = 30f
    private val padding = 20f

    private var viewWidth: Float = 0f
    private var viewHeight: Float = 0f

    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.argb(255, 48, 113, 182)
    }

    private val lastDrawnLinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.argb(255, 255, 0, 0)
    }

    private val dotPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.argb(255, 0, 0, 0)
    }

    private val takenLinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.argb(255, 255, 165, 0)
    }

    private val boxPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL;
        color = Color.argb(255, 204, 204, 204)
    }

    private val playerOneBoxPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.argb(255, 30, 144, 255)
    }

    private val playerTwoBoxPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.argb(255, 102, 187, 106)
    }

    private val scoreTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.BLACK
        textSize = 100f
        typeface = Typeface.MONOSPACE
    }

    private val gestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
        override fun onDown(event: MotionEvent): Boolean = true

        override fun onSingleTapUp(event: MotionEvent): Boolean {
            val touchX = event.x
            val touchY = event.y

            // Check if the touch is within any line's bounds
            for (line in game.lines) {
                val lineLeft: Float
                val lineRight: Float
                val lineTop: Float
                val lineBottom: Float

                if (line.lineY % 2 == 0) {
                    // Horizontal line
                    lineTop = line.lineY / 2 * lineLength + lineWidth * line.lineY / 2 - clickDetectionArea
                    lineBottom = lineTop + lineWidth + 2 * clickDetectionArea
                    lineLeft = lineWidth * (line.lineX + 1) + lineLength * line.lineX - clickDetectionArea
                    lineRight = lineLeft + lineLength + 2 * clickDetectionArea
                } else {
                    // Vertical line
                    lineTop = lineWidth * ((line.lineY + 1) / 2) + lineLength * (((line.lineY + 1) / 2) - 1) - clickDetectionArea
                    lineBottom = lineTop + lineLength + 2 * clickDetectionArea
                    lineLeft = lineWidth * line.lineX + lineLength * line.lineX - clickDetectionArea
                    lineRight = lineLeft + lineWidth + 2 * clickDetectionArea
                }

                if (touchX >= lineLeft && touchX <= lineRight && touchY >= lineTop && touchY <= lineBottom) {
                    // Touch is within this line's bounds
                    try {
                        line.drawLine()
                        invalidate()
                        if (!game.isFinished && game.players.none { it is SimpleComputerPlayer }) {
                            Snackbar.make(this@GameView, "It is now ${(game.currentPlayer as NamedPlayer).name}'s turn", Snackbar.LENGTH_SHORT).show()
                        }
                        return true
                    } catch (e: Exception) {
                        if (!game.isFinished) {
                            Snackbar.make(this@GameView, "Line has already been drawn", Snackbar.LENGTH_SHORT).show()
                        } else {
                            Snackbar.make(this@GameView, "The game has already been finished!", Snackbar.LENGTH_SHORT).show()
                        }
                        return false
                    }
                }
            }

            return false
        }
    })
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return gestureDetector.onTouchEvent(event) || super.onTouchEvent(event)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        recalculateDimensions()
    }

    private fun recalculateDimensions() {
        viewWidth = width.toFloat() - 2 * padding
        viewHeight = height.toFloat() - 2 * padding
        val maxLineWidth = (viewWidth - 10 * (columns + 1)) / columns
        val maxLineHeight = (viewHeight - 10 * (columns + 1)) / columns

        lineLength = minOf(maxLineWidth, maxLineHeight)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        recalculateDimensions()

        val gridHeight = columns * lineLength + (columns + 1) * lineWidth

        // Draw lines
        for (line in game.lines) {
            var lineTop: Float
            var lineBottom: Float
            var lineLeft: Float
            var lineRight: Float

            if (line.lineY % 2 == 0) {
                // Draw line horizontally
                lineTop = line.lineY / 2 * lineLength + lineWidth * line.lineY / 2
                lineBottom = lineTop + lineWidth
                lineLeft = lineWidth * (line.lineX + 1) + lineLength * line.lineX
                lineRight = lineLeft + lineLength
            } else {
                // Draw line vertically
                lineTop = lineWidth * ((line.lineY + 1) / 2) + lineLength * (((line.lineY + 1) / 2) - 1)
                lineBottom = lineTop + lineLength
                lineLeft = lineWidth * line.lineX + lineLength * line.lineX
                lineRight = lineLeft + lineWidth
            }

            val linePaintToUse = if (line == game.lastDrawn) lastDrawnLinePaint else if (line.isDrawn) takenLinePaint else linePaint

            canvas.drawRect(lineLeft + padding , lineTop + padding, lineRight + padding, lineBottom + padding, linePaintToUse)
        }

        // Draw boxes
        for (box in game.boxes) {
            val boxLeft = lineWidth * (box.boxX + 1) + lineLength * box.boxX + padding
            val boxRight = boxLeft + lineLength
            val boxTop = lineWidth * (box.boxY + 1) + lineLength * box.boxY + padding
            val boxBottom = boxTop + lineLength

            val chosenBoxPaint: Paint

            when (box.owningPlayer) {
                game.players[0] -> chosenBoxPaint = playerOneBoxPaint
                game.players[1] -> chosenBoxPaint = playerTwoBoxPaint
                else -> chosenBoxPaint = boxPaint
            }

            canvas.drawRect(boxLeft, boxTop, boxRight, boxBottom, chosenBoxPaint)
        }

        // Draw dots
        for (line in game.lines) {
            var lineTop: Float
            var lineLeft: Float
            var lineRight: Float

            if (line.lineY % 2 == 0) {
                lineTop = line.lineY / 2 * lineLength + lineWidth * line.lineY / 2 + padding
                lineLeft = lineWidth * (line.lineX + 1) + lineLength * line.lineX + padding
                lineRight = lineLeft + lineLength

                val circleLeft = lineLeft - lineWidth / 2
                val circleRight = lineRight + lineWidth / 2

                canvas.drawCircle(circleLeft, lineTop + lineWidth / 2, lineWidth, dotPaint) // Left circle
                canvas.drawCircle(circleRight, lineTop + lineWidth / 2, lineWidth, dotPaint) // Right circle
            }
        }

        // Draw player names and scores
        val playerOneText = "${(game.players[0] as NamedPlayer).name}: ${game.getScores()[0]}"
        val playerTwoText = "${(game.players[1] as NamedPlayer).name}: ${game.getScores()[1]}"

        val playerOneTextWidth = scoreTextPaint.measureText(playerOneText)
        val playerTwoTextWidth = scoreTextPaint.measureText(playerTwoText)

        val textYPlayerOne = viewHeight - gridHeight / 2
        val textYPlayerTwo = viewHeight - gridHeight / 2 + 150

        val orientation = resources.configuration.orientation
        val textXPlayerOne: Float
        val textXPlayerTwo: Float

        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // Landscape orientation
            textXPlayerOne = (viewWidth / 2 - playerOneTextWidth) / 2 + viewWidth / 2
            textXPlayerTwo = (viewWidth / 2 - playerTwoTextWidth) / 2 + viewWidth / 2
        } else {
            // Portrait orientation
            textXPlayerOne = (viewWidth - playerOneTextWidth) / 2
            textXPlayerTwo = (viewWidth - playerTwoTextWidth) / 2
        }

        canvas.drawText(playerOneText, textXPlayerOne, textYPlayerOne, scoreTextPaint)
        canvas.drawText(playerTwoText, textXPlayerTwo, textYPlayerTwo, scoreTextPaint)
    }

    private fun showEndGameDialog(winnerName: String) {
        game.removeOnGameOverListener(gameOverListener)

        val builder = AlertDialog.Builder(context)
        builder.setTitle("Game Over!")
        builder.setMessage("$winnerName won the game! With the scores being ${game.getScores()[0]} - ${game.getScores()[1]}, would you like to play again?")

        builder.setPositiveButton("Yes") { _, _ ->
            (context as MainActivity).startGame()
            invalidate()
        }

        builder.setNegativeButton("No") { _, _ ->
            val intent = Intent(context, StartActivity::class.java)
            context.startActivity(intent)
            (context as Activity).finish()

        }

        builder.create().show()
    }
}

