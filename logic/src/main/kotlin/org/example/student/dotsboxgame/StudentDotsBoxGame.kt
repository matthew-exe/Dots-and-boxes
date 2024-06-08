package org.example.student.dotsandboxes

import org.example.student.dotsboxgame.SimpleHumanPlayer
import uk.ac.bournemouth.ap.dotsandboxes.SimpleComputerPlayer
import uk.ac.bournemouth.ap.dotsandboxeslib.AbstractDotsAndBoxesGame
import uk.ac.bournemouth.ap.dotsandboxeslib.ComputerPlayer
import uk.ac.bournemouth.ap.dotsandboxeslib.DotsAndBoxesGame
import uk.ac.bournemouth.ap.dotsandboxeslib.Player
import uk.ac.bournemouth.ap.lib.matrix.Matrix
import uk.ac.bournemouth.ap.lib.matrix.MutableMatrix
import uk.ac.bournemouth.ap.lib.matrix.MutableSparseMatrix
import uk.ac.bournemouth.ap.lib.matrix.SparseMatrix


class StudentDotsBoxGame(
    val columns: Int = 4,
    val rows: Int = 4,
    players: List<Player> = listOf(SimpleHumanPlayer("Player 1"), SimpleComputerPlayer("Computer", "easy"))
) : AbstractDotsAndBoxesGame() {

    override var players: List<Player> = players.toList()

    override var currentPlayer: Player = players[0]

    override val boxes: Matrix<StudentBox> = MutableMatrix(columns, rows, ::StudentBox)

    override val lines: SparseMatrix<StudentLine> = MutableSparseMatrix(columns + 1, (rows * 2) + 1,
        { x, y -> x < columns || y % 2 == 1 }, ::StudentLine)
    override var isFinished: Boolean = false
        private set

    var lastDrawn: StudentLine? = null
        private set

    override fun playComputerTurns() {
        var current = currentPlayer
        while (current is ComputerPlayer && ! isFinished) {
            current.makeMove(this)
            current = currentPlayer
        }
    }

    inner class StudentLine(lineX: Int, lineY: Int) : AbstractLine(lineX, lineY) {
        override var isDrawn: Boolean = false

        override val adjacentBoxes: Pair<StudentBox?, StudentBox?>
            get() {
                val halfY = lineY / 2
                return when {
                    // Box on leftmost column and odd row
                    lineX == 0 && lineY % 2 == 1 -> Pair(null, boxes[lineX, halfY])
                    // Box is on rightmost column
                    lineX == columns -> Pair(boxes[lineX - 1, halfY], null)
                    // Box is on the topmost row
                    lineY == 0 -> Pair(null, boxes[lineX, lineY])
                    // Box is on the bottommost row
                    lineY == rows * 2 -> Pair(boxes[lineX, halfY - 1], null)
                    // Box is on an odd row (excluding top and bottom rows)
                    lineY % 2 == 1 -> Pair(boxes[lineX - 1, halfY], boxes[lineX, halfY])
                    // Box is on an even row (exluding top and bottom rows
                    else -> Pair(boxes[lineX, lineY - halfY - 1], boxes[lineX, lineY - halfY])
                }
            }

        override fun drawLine() {
            if (!this.isDrawn) {
                this.isDrawn = true
                lastDrawn = this
                var boxCompleted = false

                for (box in adjacentBoxes.toList()) {
                    if (box != null && box.boundingLines.all { it.isDrawn }) {
                        box.owningPlayer = currentPlayer
                        boxCompleted = true
                    }
                }

                fireGameChange()

                if (!boxCompleted) { // If no box completed, switch to the next player
                    currentPlayer = players[(players.indexOf(currentPlayer) + 1) % players.size]

                    if (currentPlayer is ComputerPlayer) {
                        playComputerTurns()
                    }
                }
            } else {
                throw Exception("Line has already been drawn")
            }

            if (boxes.all { it.owningPlayer != null }) {
                isFinished = true
                val scores = getScores().mapIndexed { index, score -> players[index] to score }
                fireGameOver(scores)
            }
        }
    }

    inner class StudentBox(boxX: Int, boxY: Int) : AbstractBox(boxX, boxY) {
        override var owningPlayer: Player? = null
        override val boundingLines: Iterable<DotsAndBoxesGame.Line>
            get() = lines.filter { it.adjacentBoxes.first == this || it.adjacentBoxes.second == this  }

    }
}