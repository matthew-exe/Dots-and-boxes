package uk.ac.bournemouth.ap.dotsandboxes

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import org.example.student.dotsandboxes.StudentDotsBoxGame
import org.example.student.dotsboxgame.SimpleHumanPlayer
import uk.ac.bournemouth.ap.dotsandboxeslib.Player


class MainActivity : AppCompatActivity() {
    private var rows: Int = 0
    private var columns: Int = 0
    private var players: List<Player> = listOf(SimpleHumanPlayer("Player 1"), SimpleHumanPlayer("Player 2"))
    private var difficulty: String = "easy"
    private var playAgainst: String = "human"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get the game settings from the intent
        rows = intent.getIntExtra("rows", 4)
        columns = intent.getIntExtra("columns", 4)
        difficulty = intent.getStringExtra("difficulty") ?: "easy"
        playAgainst = intent.getStringExtra("playAgainst") ?: "human"

        players = when (playAgainst) {
            "human" -> listOf(SimpleHumanPlayer("Player 1"), SimpleHumanPlayer("Player 2"))
            else -> listOf(SimpleHumanPlayer("Player 1"), SimpleComputerPlayer("Computer", difficulty))
        }

        startGame()
    }

    fun startGame() {
        val game = StudentDotsBoxGame(rows, columns, players)
        val gameView = GameView(this, game)
        setContentView(gameView)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.endGame -> {
                val intent = Intent(this, StartActivity::class.java)
                this.startActivity(intent)
                (this as Activity).finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}

