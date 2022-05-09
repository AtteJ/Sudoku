package com.attej.sudoku;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.attej.sudoku.backend.GameRecord;
import com.attej.sudoku.backend.Stats;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class StatsActivity extends AppCompatActivity {
    private Stats stats;
    private String FILENAME = "game_records.dat";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        stats = new Stats(getApplicationContext());

        refreshStats();
    }


    private void refreshStats() {
        setBestTimes();
        setAverageTimes();
        setWinPercentages();
        setGamesWon();
        setGamesPlayed();
    }


    private void setBestTimes() {
        TextView easy = findViewById(R.id.easyBestTime);
        TextView normal = findViewById(R.id.normalBestTime);
        TextView hard = findViewById(R.id.hardBestTime);

        int easyBest = stats.getBestTime(0);
        int normalBest = stats.getBestTime(1);
        int hardBest = stats.getBestTime(2);
        if (easyBest != 0) {
            int minutes = easyBest / 60;
            int seconds = easyBest % 60;
            easy.setText("Best Time: " + String.format("%02d:%02d", minutes, seconds));
        }
        else
            easy.setText("Best Time:");

        if (normalBest != 0) {
            int minutes = normalBest / 60;
            int seconds = normalBest % 60;
            normal.setText("Best Time: " + String.format("%02d:%02d", minutes, seconds));
        }
        else
            normal.setText("Best Time:");

        if (hardBest != 0) {
            int minutes = hardBest / 60;
            int seconds = hardBest % 60;
            hard.setText("Best Time: " + String.format("%02d:%02d", minutes, seconds));
        }
        else
            hard.setText("Best Time:");
    }


    private void setAverageTimes() {
        TextView easy = findViewById(R.id.easyAverageTime);
        TextView normal = findViewById(R.id.normalAverageTime);
        TextView hard = findViewById(R.id.hardAverageTime);

        int easyAverage = stats.getAverage(0);
        int normalAverage = stats.getAverage(1);
        int hardAverage = stats.getAverage(2);
        if (easyAverage != 0) {
            int minutes = easyAverage / 60;
            int seconds = easyAverage % 60;
            easy.setText("Average Time: " + String.format("%02d:%02d", minutes, seconds));
        }
        else
            easy.setText("Average Time:");

        if (normalAverage != 0) {
            int minutes = normalAverage / 60;
            int seconds = normalAverage % 60;
            normal.setText("Average Time: " + String.format("%02d:%02d", minutes, seconds));
        }
        else
            normal.setText("Average Time:");

        if (hardAverage != 0) {
            int minutes = hardAverage / 60;
            int seconds = hardAverage % 60;
            hard.setText("Average Time: " + String.format("%02d:%02d", minutes, seconds));
        }
        else
            hard.setText("Average Time:");
    }


    private void setWinPercentages() {
        TextView easy = findViewById(R.id.easyWinPercent);
        TextView normal = findViewById(R.id.normalWinPercent);
        TextView hard = findViewById(R.id.hardWinPercent);

        easy.setText("Win Percentage: " + stats.getWinPercentage(0) + "%");
        normal.setText("Win Percentage: " + stats.getWinPercentage(1) + "%");
        hard.setText("Win Percentage: " + stats.getWinPercentage(2) + "%");
    }


    private void setGamesWon() {
        TextView easy = findViewById(R.id.easyGamesWon);
        TextView normal = findViewById(R.id.normalGamesWon);
        TextView hard = findViewById(R.id.hardGamesWon);

        easy.setText("Games Won: " + stats.getDifficultyWon(0));
        normal.setText("Games Won: " + stats.getDifficultyWon(1));
        hard.setText("Games Won: " + stats.getDifficultyWon(2));
    }


    private void setGamesPlayed() {
        TextView easy = findViewById(R.id.easyGamesPlayed);
        TextView normal = findViewById(R.id.normalGamesPlayed);
        TextView hard = findViewById(R.id.hardGamesPlayed);

        easy.setText("Games Played: " + stats.getDifficultyPlayed(0));
        normal.setText("Games Played: " + stats.getDifficultyPlayed(1));
        hard.setText("Games Played: " + stats.getDifficultyPlayed(2));
    }


    public void onGoBackButtonClicked(View view) {
        finish();
    }


    public void onClearStatsClicked(View view) {
        new AlertDialog.Builder(this)
                .setTitle("Are you sure?")
                .setMessage("This is irreversible")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("Clear stats", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        stats.clearStats();
                        refreshStats();
                    }})
                .setNegativeButton(android.R.string.no, null).show();
    }
}
