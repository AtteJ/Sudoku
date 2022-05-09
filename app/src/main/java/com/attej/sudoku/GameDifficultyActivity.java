package com.attej.sudoku;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class GameDifficultyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_difficulty);
        disableButtons(false);
   }


    private void disableButtons(boolean isDisabled) {
        findViewById(R.id.buttonEasy).setEnabled(!isDisabled);
        findViewById(R.id.buttonNormal).setEnabled(!isDisabled);
        findViewById(R.id.buttonHard).setEnabled(!isDisabled);
    }


    public void startGame(int difficulty) {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("difficulty", difficulty);
        startActivityForResult(intent, 0);
    }


    public void onDifficultyButtonClicked(View view) {
        disableButtons(true);

        int selectedDifficulty = Integer.parseInt((view).getTag().toString());
        startGame(selectedDifficulty);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1) {
            disableButtons(false);
        }
        if (resultCode == 2) {
            disableButtons(false);
            Intent intent = new Intent();
            intent.putExtra("Lost", 1);
            setResult(1, intent);
            finish();
        }
    }


    public void onGoBackButtonClicked(View view) {
        Intent intent = new Intent();
        intent.putExtra("Lost", 1);
        setResult(1, intent);
        finish();
    }
}
