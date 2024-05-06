package com.example.memorymaze

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.memorymaze.databinding.ActivityScoreboardBinding

class ScoreboardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityScoreboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScoreboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retrieve and display highest 5 scores
        val scores = getScores()
        displayScores(scores)
        binding.buttonBackToStart.setOnClickListener {
            startActivity(Intent(this, StartActivity::class.java))
            finish()
        }
        binding.buttonClearScores.setOnClickListener {
            clearScoreHistory()
            Toast.makeText(this, "Score history cleared", Toast.LENGTH_SHORT).show()
            // After clearing the score history, you may want to update the displayed scores
            val updatedScores = getScores()
            displayScores(updatedScores)
        }
    }

    private fun getScores(): List<Int> {
        val sharedPrefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val scores = mutableListOf<Int>()
        repeat(5) { index ->
            val score = sharedPrefs.getInt("score$index", 0)
            scores.add(score)
        }
        return scores
    }
    private fun clearScoreHistory() {

        val sharedPrefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        sharedPrefs.edit().clear().apply()
    }

    private fun displayScores(scores: List<Int>) {
        val uniqueScores = scores.distinct()
        binding.textViewScore1.text = uniqueScores.getOrElse(0) { 0 }.toString()
        binding.textViewScore2.text = uniqueScores.getOrElse(1) { 0 }.toString()
        binding.textViewScore3.text = uniqueScores.getOrElse(2) { 0 }.toString()
        binding.textViewScore4.text = uniqueScores.getOrElse(3) { 0 }.toString()
        binding.textViewScore5.text = uniqueScores.getOrElse(4) { 0 }.toString()
    }
}
