package com.attej.sudoku;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.Arrays;
import java.util.List;

public class GameDifficultyActivity extends AppCompatActivity {

    private FirebaseAnalytics mFireBaseAnalytics;
    private AdView mAdView;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_difficulty);

        disableButtons(false);

        setTestAds();
        setAds();
        setAnalytics();
   }


   private void setAnalytics() {
       // Obtain the FirebaseAnalytics instance.
       mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
       Bundle bundle = new Bundle();
       bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "Difficulty screen");
       bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "difficulty");
       bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "difficulty");
       mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
   }


   private void setAds() {
       mAdView = findViewById(R.id.adView2);
       AdRequest adRequest = new AdRequest.Builder().build();
       mAdView.loadAd(adRequest);
   }


    private void setTestAds() {
        List<String> testDeviceIds = Arrays.asList("20D91EB201806F1C7EA6457155F468D8", "62AEE42886038F87608F7F6F5D0B41BA");     // Test ads TODO: remove in prod
        RequestConfiguration configuration =
                new RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build(); // Test ads
        MobileAds.setRequestConfiguration(configuration);                                   // Test ads
    }


    private void disableButtons(boolean isDisabled) {
        findViewById(R.id.buttonEasy).setEnabled(!isDisabled);
        findViewById(R.id.buttonNormal).setEnabled(!isDisabled);
        findViewById(R.id.buttonHard).setEnabled(!isDisabled);
        findViewById(R.id.buttonExpert).setEnabled(!isDisabled);
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
