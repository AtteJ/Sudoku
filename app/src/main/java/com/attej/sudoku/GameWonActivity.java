package com.attej.sudoku;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.attej.sudoku.backend.GameRecord;
import com.attej.sudoku.backend.Stats;

public class GameWonActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_won);

        Stats stats = new Stats(this);
        int time = getIntent().getIntExtra("time", 0);
        int difficulty = getIntent().getIntExtra("difficulty", 0);

        String solveTime = String.format("Time: %1$02d:%2$02d", time / 60, time % 60);

        if (stats.getBestTime(difficulty) > time) {
            TextView textBestTime = findViewById(R.id.textBestTime);
            textBestTime.setText("New Best Time!\n" + solveTime);
        }
        else {
            TextView textBestTime = findViewById(R.id.textBestTime);
            textBestTime.setText(solveTime);
        }

        stats.addPlaytime(time);
        saveRecord(time, difficulty);
    }


    private void saveRecord(int time, int difficulty) {
        GameRecord record;
        record = new GameRecord(time, difficulty);

        Stats stats = new Stats(this);
        stats.addRecord(record);
        stats.saveStats();
    }


    public void onNewGameButtonClicked(View view) {
        Intent intent = new Intent();
        intent.putExtra("Game won", 1);
        intent.putExtra("Go home", 0);
        setResult(1, intent);
        finish();
    }


    public void onGoHomeButtonClicked(View view) {
        Intent intent = new Intent();
        intent.putExtra("Game won", 1);
        intent.putExtra("Go home", 1);
        setResult(1, intent);
        finish();
    }
}
