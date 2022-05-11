package com.attej.sudoku;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.analytics.FirebaseAnalytics;

public class GameDifficultyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_difficulty);

        // Obtain the FirebaseAnalytics instance.
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "Difficulty screen");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "difficulty");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "difficulty");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

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
        NewGameActivityResultLauncher.launch(intent);
    }

    final ActivityResultLauncher<Intent> NewGameActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == 1) {
                    disableButtons(false);
                }
                if (result.getResultCode() == 2) {
                    disableButtons(false);
                    Intent intent = new Intent();
                    intent.putExtra("Lost", 1);
                    setResult(1, intent);
                    finish();
                }
            });


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
