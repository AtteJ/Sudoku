package com.attej.sudoku;

import android.app.Activity;
import android.os.Bundle;

import com.attej.sudoku.backend.ExperienceBar;
import com.attej.sudoku.backend.Stats;
import com.google.firebase.analytics.FirebaseAnalytics;

import android.content.Intent;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {
    private int experience = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Obtain the FirebaseAnalytics instance.
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "Game opened");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Main");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "main");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

        refreshStats();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        refreshStats();
    }


    private void refreshStats() {
        Stats stats = new Stats(getApplicationContext());
        experience = stats.getExperience();

        setExperience();
    }


    private void setExperience() {
        ExperienceBar bar = (ExperienceBar) getSupportFragmentManager().findFragmentById(R.id.experience);
        if (bar != null) {
            bar.setExperience(experience % 100);
            bar.setLevel((int)Math.floor(experience / 100.0) + 1);
        }
    }


    final ActivityResultLauncher<Intent> NewGameActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    refreshStats();
                }
            });



    public void onStartNewGameButtonClicked(View view) {
        Intent intent = new Intent(this, GameDifficultyActivity.class);
        NewGameActivityResultLauncher.launch(intent);
    }


    public void onViewStatsButtonClicked(View view) {
        Intent intent = new Intent(this, StatsActivity.class);
        startActivity(intent);
    }

}