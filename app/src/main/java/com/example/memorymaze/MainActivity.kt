package com.example.memorymaze

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.example.memorymaze.databinding.ActivityMainBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.core.view.forEach

class MainActivity : AppCompatActivity(), View.OnClickListener{
    private var score: Int? = null //to store the score
    private lateinit var binding: ActivityMainBinding //instance to access views in the layout files
    private var result: String = "" //to store randomly generated sequence of tiles
    private var userAnswer: String = "" //to store sequence of user inputs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            panel1.setOnClickListener(this@MainActivity)
            panel2.setOnClickListener(this@MainActivity)
            panel3.setOnClickListener(this@MainActivity)
            panel4.setOnClickListener(this@MainActivity)
            startGame()
            tvScoreboard.setOnClickListener {
                // Navigate to ScoreboardActivity when scoreboard text is clicked
                startActivity(Intent(this@MainActivity, ScoreboardActivity::class.java))
            }
        }
    }

    private fun startGame() {
        // Reset variables and disable buttons
        result = ""
        userAnswer = ""
        disableButtons()

        // Coroutine scope for generating and displaying sequence of tiles
        lifecycleScope.launch {
            val round = (3..5).random() //generates a random number between 3 and 5 (inclusive) to determine the number of rounds in the game
            //repeats the round value of times
            repeat(round) {
                delay(400)

                //generate a random number between 1 and 4 (inclusive) to represent the panel that will light up in this round
                val randomPanel = (1..4).random()
                //stores the sequence of panels that light up
                result += randomPanel

                val panel = when (randomPanel) {
                    1 -> binding.panel1
                    2 -> binding.panel2
                    3 -> binding.panel3
                    else -> binding.panel4
                }

                val drawableYellow = ActivityCompat.getDrawable(this@MainActivity, R.drawable.btn_yellow)
                val drawableDefault = ActivityCompat.getDrawable(this@MainActivity, R.drawable.btn_state)
                panel.background = drawableYellow
                delay(1000)
                panel.background = drawableDefault
            }
            // Enabling buttons after tiles are displayed
            runOnUiThread {
                enableButtons()
            }
        }
    }

    private fun loseAnimation() {
        binding.apply {
            // Store the score if it's not null
            score?.let{
                storeScore(it)
            }
            score = 0
            tvScore.text = ""
            disableButtons()
            val drawableLose = ActivityCompat.getDrawable(this@MainActivity, R.drawable.btn_lose)
            val drawableDefault = ActivityCompat.getDrawable(this@MainActivity, R.drawable.btn_state)
            lifecycleScope.launch {
                binding.root.forEach { view ->
                    if (view is Button) {
                        view.background = drawableLose
                        delay(200)
                        view.background = drawableDefault
                    }
                }
                delay(4000)
                startGame()
            }
        }
    }

    private fun enableButtons() {
        binding.root.forEach { view ->
            if (view is Button) {
                view.isEnabled = true
            }
        }
    }

    private fun disableButtons() {
        binding.root.forEach { view ->
            if (view is Button) {
                view.isEnabled = false
            }
        }
    }

    override fun onClick(view: View?) {
        view?.let {
            userAnswer += when (it.id) {
                R.id.panel1 -> "1"
                R.id.panel2 -> "2"
                R.id.panel3 -> "3"
                R.id.panel4 -> "4"
                else -> ""
            }

            if (userAnswer == result) {
                Toast.makeText(this@MainActivity, "W I N :)", Toast.LENGTH_SHORT).show()
                score = (score ?: 0) + 1 // Increment score, defaulting to 0 if score is null
                binding.tvScore.text = score.toString()
                startGame()
            } else if (userAnswer.length >= result.length) {
                loseAnimation()
            }
        }
    }


    private fun storeScore(score: Int) {
        val sharedPrefs = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val editor = sharedPrefs.edit()
        val topScores = mutableListOf<Int>()

        // Load existing scores
        repeat(5) { index ->
            val highScore = sharedPrefs.getInt("score$index", 0)
            topScores.add(highScore)
        }

        // Add current score
        topScores.add(score)

        // Sort scores in descending order
        topScores.sortDescending()

        // Keep only the top 5 scores
        val updatedTopScores = topScores.take(5)

        // Save the updated top scores
        updatedTopScores.forEachIndexed { index, topScore ->
            editor.putInt("score$index", topScore)
        }

        editor.apply()
    }
}