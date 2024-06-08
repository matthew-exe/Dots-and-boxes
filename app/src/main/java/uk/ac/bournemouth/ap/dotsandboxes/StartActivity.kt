package uk.ac.bournemouth.ap.dotsandboxes
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class StartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        val rgPlayers = findViewById<RadioGroup>(R.id.rgPlayers)
        val tvDifficulty = findViewById<TextView>(R.id.tvDifficulty)
        val rgDifficulty = findViewById<RadioGroup>(R.id.rgDifficulty)
        val startButton = findViewById<Button>(R.id.startButton)

        rgPlayers.setOnCheckedChangeListener { _, checkedId ->
            val isDifficultyVisible = checkedId == R.id.rbComputer
            tvDifficulty.visibility = if (isDifficultyVisible) View.VISIBLE else View.GONE
            rgDifficulty.visibility = if (isDifficultyVisible) View.VISIBLE else View.GONE
        }

        startButton.setOnClickListener {
            val rgGameSize = findViewById<RadioGroup>(R.id.rgGameSize)
            val rows: Int
            val columns: Int
            val difficulty: String
            val playAgainst: String

            when (rgGameSize.checkedRadioButtonId) {
                R.id.rb4x4 -> {
                    rows = 4
                    columns = 4
                } else -> {
                    rows = 6
                    columns = 6
                }
            }

            when (rgDifficulty.checkedRadioButtonId) {
                R.id.rbEasy -> difficulty = "easy"
                R.id.rbMedium -> difficulty = "medium"
                else -> difficulty = "hard"
            }

            when (rgPlayers.checkedRadioButtonId) {
                R.id.rbHuman -> playAgainst = "human"
                else -> playAgainst = "computer"
            }

            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("rows", rows)
            intent.putExtra("columns", columns)
            intent.putExtra("difficulty", difficulty)
            intent.putExtra("playAgainst", playAgainst)
            startActivity(intent)
        }
    }
}