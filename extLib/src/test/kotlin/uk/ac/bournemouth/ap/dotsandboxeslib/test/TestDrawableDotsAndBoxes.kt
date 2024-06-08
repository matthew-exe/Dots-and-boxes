package uk.ac.bournemouth.ap.dotsandboxeslib.test

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import uk.ac.bournemouth.ap.dotsandboxeslib.HumanPlayer
import uk.ac.bournemouth.ap.dotsandboxeslib.Player
import uk.ac.bournemouth.ap.dotsandboxeslib.drawablegame.DrawableDotsAndBoxesGame
import kotlin.random.Random

abstract class TestDrawableDotsAndBoxes: TestDotsAndBoxes() {
    override fun createGame(
        columns: Int,
        rows: Int,
        players: List<Player>
                                    ): DrawableDotsAndBoxesGame =
        createGame(columns, rows, players, Random.nextInt(players.size))

    abstract fun createGame(
        columns: Int = 8,
        rows: Int = 8,
        players: List<Player>,
        currentPlayerIndex: Int
                                    ): DrawableDotsAndBoxesGame


    /**
     * Check that the player assigned as active player on game creation is the
     * active player from the perspective of currentPlayer.
     */
    @ParameterizedTest(name = "playerCount = {0}, idx=#{1}")
    @CsvSource("1, 0", "2, 1", "3, 1", "5, 3")
    fun testPassActivePlayer(playerCount: Int, currentPlayerIdx:Int) {
        val players = MutableList(playerCount) { object: HumanPlayer {} }
        val game = createGame(players = players, currentPlayerIndex = currentPlayerIdx)
        assertEquals(players[currentPlayerIdx], game.currentPlayer)
    }

}