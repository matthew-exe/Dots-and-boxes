package uk.ac.bournemouth.ap.dotsandboxes

import uk.ac.bournemouth.ap.dotsandboxeslib.ComputerPlayer
import uk.ac.bournemouth.ap.dotsandboxeslib.DotsAndBoxesGame
import uk.ac.bournemouth.ap.dotsandboxeslib.NamedPlayer

class SimpleComputerPlayer(override val name: String = "Computer", val difficulty: String = "easy") : ComputerPlayer, NamedPlayer {
    override fun makeMove(game: DotsAndBoxesGame) {
        if (difficulty == "easy") {
            makeEasyMove(game)
        } else if (difficulty == "medium") {
            makeMediumMove(game)
        } else {
            makeHardMove(game)
        }
    }

    private fun makeEasyMove(game: DotsAndBoxesGame) {
        val undrawnBoxes = game.boxes.filter { it.owningPlayer == null }
        var selectedBox: DotsAndBoxesGame.Box

        val firstPriorityBoxes =
            undrawnBoxes.filter { it.boundingLines.count { line -> line.isDrawn } == 3 }

        if (firstPriorityBoxes.isNotEmpty()) {
            selectedBox = firstPriorityBoxes.random()
        } else {
            selectedBox = undrawnBoxes.random()
        }

        val selectedBoxUndrawnLines = selectedBox.boundingLines.filter { !it.isDrawn }
        val chosenLine = selectedBoxUndrawnLines.random()
        chosenLine.drawLine()
    }

    private fun makeMediumMove(game: DotsAndBoxesGame) {
        val undrawnBoxes = game.boxes.filter { it.owningPlayer == null }
        var selectedBox: DotsAndBoxesGame.Box

        val firstPriorityBoxes =
            undrawnBoxes.filter { it.boundingLines.count { line -> line.isDrawn } == 3 }
        val secondPriorityBoxes =
            undrawnBoxes.filter { it.boundingLines.count { line -> line.isDrawn } == 1 }
        val thirdPriorityBoxes =
            undrawnBoxes.filter { it.boundingLines.count { line -> line.isDrawn } == 0 }
        val lastPriorityBoxes =
            undrawnBoxes.filter { it.boundingLines.count { line -> line.isDrawn } == 2 }

        if (firstPriorityBoxes.isNotEmpty()) {
            selectedBox = firstPriorityBoxes.random()
        } else if (secondPriorityBoxes.isNotEmpty()) {
            selectedBox = secondPriorityBoxes.random()
        } else if (thirdPriorityBoxes.isNotEmpty()) {
            selectedBox = thirdPriorityBoxes.random()
        } else {
            selectedBox = lastPriorityBoxes.random()
        }

        val selectedBoxUndrawnLines = selectedBox.boundingLines.filter { !it.isDrawn }
        val chosenLine = selectedBoxUndrawnLines.random()
        chosenLine.drawLine()
    }

    private fun makeHardMove(game: DotsAndBoxesGame) {
        val undrawnBoxes = game.boxes.filter { it.owningPlayer == null }
        var selectedBox: DotsAndBoxesGame.Box

        val firstPriorityBoxes =
            undrawnBoxes.filter { it.boundingLines.count { line -> line.isDrawn } == 3 }
        val secondPriorityBoxes =
            undrawnBoxes.filter { it.boundingLines.count { line -> line.isDrawn } == 0 }
        val thirdPriorityBoxes =
            undrawnBoxes.filter { it.boundingLines.count { line -> line.isDrawn } == 1 }
        val lastPriorityBoxes =
            undrawnBoxes.filter { it.boundingLines.count { line -> line.isDrawn } == 2 }

        if (firstPriorityBoxes.isNotEmpty()) {
            selectedBox = firstPriorityBoxes.random()
        } else if (secondPriorityBoxes.isNotEmpty()) {
            selectedBox = secondPriorityBoxes.random()
        } else if (thirdPriorityBoxes.isNotEmpty()) {
            selectedBox = thirdPriorityBoxes.random()
        } else {
            selectedBox = lastPriorityBoxes.random()
        }

        val selectedBoxUndrawnLines = selectedBox.boundingLines.filter { !it.isDrawn }
        val chosenLine = selectedBoxUndrawnLines.random()
        chosenLine.drawLine()
    }
}