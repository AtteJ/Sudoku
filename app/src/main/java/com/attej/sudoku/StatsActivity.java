package com.attej.sudoku;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.attej.sudoku.backend.Stats;
import com.google.firebase.analytics.FirebaseAnalytics;


public class StatsActivity extends AppCompatActivity {
    private Stats stats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        // Obtain the FirebaseAnalytics instance.
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

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
        TextView expert = findViewById(R.id.expertBestTime);

        int easyBest = stats.getBestTime(0);
        int normalBest = stats.getBestTime(1);
        int hardBest = stats.getBestTime(2);
        int expertBest = stats.getBestTime(3);
        if (easyBest != 0) {
            int minutes = easyBest / 60;
            int seconds = easyBest % 60;
            easy.setText(String.format(getResources().getString(R.string.best_time), minutes, seconds));
        }
        else
            easy.setText(getResources().getString(R.string.no_best_time));

        if (normalBest != 0) {
            int minutes = normalBest / 60;
            int seconds = normalBest % 60;
            normal.setText(String.format(getResources().getString(R.string.best_time), minutes, seconds));
        }
        else
            normal.setText(getResources().getString(R.string.no_best_time));

        if (hardBest != 0) {
            int minutes = hardBest / 60;
            int seconds = hardBest % 60;
            hard.setText(String.format(getResources().getString(R.string.best_time), minutes, seconds));
        }
        else
            hard.setText(getResources().getString(R.string.no_best_time));

        if (expertBest != 0) {
            int minutes = expertBest / 60;
            int seconds = expertBest % 60;
            expert.setText(String.format(getResources().getString(R.string.best_time), minutes, seconds));
        }
        else
            expert.setText(getResources().getString(R.string.no_best_time));
    }


    private void setAverageTimes() {
        TextView easy = findViewById(R.id.easyAverageTime);
        TextView normal = findViewById(R.id.normalAverageTime);
        TextView hard = findViewById(R.id.hardAverageTime);
        TextView expert = findViewById(R.id.expertAverageTime);

        int easyAverage = stats.getAverage(0);
        int normalAverage = stats.getAverage(1);
        int hardAverage = stats.getAverage(2);
        int expertAverage = stats.getAverage(3);
        if (easyAverage != 0) {
            int minutes = easyAverage / 60;
            int seconds = easyAverage % 60;
            easy.setText(String.format(getString(R.string.average_time), minutes, seconds));
        }
        else
            easy.setText(getString(R.string.no_average_time));

        if (normalAverage != 0) {
            int minutes = normalAverage / 60;
            int seconds = normalAverage % 60;
            normal.setText(String.format(getString(R.string.average_time), minutes, seconds));
        }
        else
            normal.setText(getString(R.string.no_average_time));

        if (hardAverage != 0) {
            int minutes = hardAverage / 60;
            int seconds = hardAverage % 60;
            hard.setText(String.format(getString(R.string.average_time), minutes, seconds));
        }
        else
            hard.setText(getString(R.string.no_average_time));

        if (expertAverage != 0) {
            int minutes = expertAverage / 60;
            int seconds = expertAverage % 60;
            expert.setText(String.format(getString(R.string.average_time), minutes, seconds));
        }
        else
            expert.setText(getString(R.string.no_average_time));
    }


    private void setWinPercentages() {
        TextView easy = findViewById(R.id.easyWinPercent);
        TextView normal = findViewById(R.id.normalWinPercent);
        TextView hard = findViewById(R.id.hardWinPercent);
        TextView expert = findViewById(R.id.expertWinPercent);

        easy.setText(String.format(getString(R.string.win_percentage), stats.getWinPercentage(0)));
        normal.setText(String.format(getString(R.string.win_percentage), stats.getWinPercentage(1)));
        hard.setText(String.format(getString(R.string.win_percentage), stats.getWinPercentage(2)));
        expert.setText(String.format(getString(R.string.win_percentage), stats.getWinPercentage(3)));
    }


    private void setGamesWon() {
        TextView easy = findViewById(R.id.easyGamesWon);
        TextView normal = findViewById(R.id.normalGamesWon);
        TextView hard = findViewById(R.id.hardGamesWon);
        TextView expert = findViewById(R.id.expertGamesWon);

        easy.setText(String.format(getString(R.string.games_won), stats.getDifficultyWon(0)));
        normal.setText(String.format(getString(R.string.games_won), stats.getDifficultyWon(1)));
        hard.setText(String.format(getString(R.string.games_won), stats.getDifficultyWon(2)));
        expert.setText(String.format(getString(R.string.games_won), stats.getDifficultyWon(3)));
    }


    private void setGamesPlayed() {
        TextView easy = findViewById(R.id.easyGamesPlayed);
        TextView normal = findViewById(R.id.normalGamesPlayed);
        TextView hard = findViewById(R.id.hardGamesPlayed);
        TextView expert = findViewById(R.id.expertGamesPlayed);

        easy.setText(String.format(getString(R.string.games_played), stats.getDifficultyPlayed(0)));
        normal.setText(String.format(getString(R.string.games_played), stats.getDifficultyPlayed(1)));
        hard.setText(String.format(getString(R.string.games_played), stats.getDifficultyPlayed(2)));
        expert.setText(String.format(getString(R.string.games_played), stats.getDifficultyPlayed(3)));
    }


    public void onGoBackButtonClicked(View view) {
        Intent intent = new Intent();
        intent.putExtra("Lost", 1);
        setResult(1, intent);
        finish();
    }


    public void onClearStatsClicked(View view) {
        new AlertDialog.Builder(this)
                .setTitle("Are you sure?")
                .setMessage("This is irreversible")
                .setPositiveButton("Clear stats", (dialog, whichButton) -> {
                    stats.clearStats();
                    refreshStats();
                })
                .setNegativeButton("Cancel", null).show();
    }
}
