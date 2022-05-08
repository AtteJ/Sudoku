package com.attej.sudoku;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import android.content.Intent;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.attej.sudoku.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "MainActivity";
    private ActivityMainBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    public void onStartNewGameButtonClicked(View view) {
        Intent intent = new Intent(this, GameDifficultyActivity.class);
        startActivity(intent);
    }


    public void onViewStatsButtonClicked(View view) {
        Intent intent = new Intent(this, StatsActivity.class);
        startActivity(intent);
    }

}