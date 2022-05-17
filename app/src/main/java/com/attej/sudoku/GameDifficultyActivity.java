package com.attej.sudoku;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.attej.sudoku.backend.Stats;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.tasks.Task;
import com.google.android.play.core.review.ReviewException;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.review.model.ReviewErrorCode;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class GameDifficultyActivity extends AppCompatActivity {
    private int difficulty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_difficulty);

        disableButtons(false);

        Stats stats = new Stats(this);
        if (stats.getTotalPlaytime() > 900)
            askReview();

        setTestAds();
        setAds();
        setAnalytics();
   }


    private void askReview() {
        ReviewManager manager = ReviewManagerFactory.create(this);
        Task<ReviewInfo> request = manager.requestReviewFlow();
        request.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // We can get the ReviewInfo object
                ReviewInfo reviewInfo = task.getResult();
                Task<Void> flow = manager.launchReviewFlow(GameDifficultyActivity.this, reviewInfo);
                flow.addOnCompleteListener(task2 -> {
                    // The flow has finished. The API does not indicate whether the user
                    // reviewed or not, or even whether the review dialog was shown. Thus, no
                    // matter the result, we continue our app flow.
                });
            } else {
                // There was some problem, log or handle the error code.
                @ReviewErrorCode int reviewErrorCode = ((ReviewException) task.getException()).getErrorCode();
            }
        });
    }


   private void setAnalytics() {
       // Obtain the FirebaseAnalytics instance.
       FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
       Bundle bundle = new Bundle();
       bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "Difficulty screen");
       bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "difficulty");
       bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "difficulty");
       mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
   }


   private void setAds() {
       AdView mAdView = findViewById(R.id.adView2);
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


    public void startGame(int givens) {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("givens", givens);
        intent.putExtra("difficulty", difficulty);
        NewGameActivityResultLauncher.launch(intent);
    }

    final ActivityResultLauncher<Intent> NewGameActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                disableButtons(false);
                if (result.getResultCode() == 1) {
                    int givens = drawGivens(difficulty);
                    startGame(givens);
                }
            });


    public void onDifficultyButtonClicked(View view) {
        disableButtons(true);

        difficulty = Integer.parseInt((view).getTag().toString());
        int givens = drawGivens(difficulty);
        startGame(givens);
    }


    private int drawGivens(int difficulty) {
        switch (difficulty) {
            case 0: {
                return ThreadLocalRandom.current().nextInt(38, 41);
            }
            case 1: {
                return ThreadLocalRandom.current().nextInt(31, 34);
            }
            case 2: {
                return ThreadLocalRandom.current().nextInt(25, 29);
            }
            case 3: {
                return ThreadLocalRandom.current().nextInt(17, 24);
            }
        }
        return 0;
    }


    public void onGoBackButtonClicked(View view) {
        Intent intent = new Intent();
        intent.putExtra("Lost", 1);
        setResult(1, intent);
        finish();
    }
}
