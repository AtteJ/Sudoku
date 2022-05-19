package com.attej.sudoku;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.games.GamesSignInClient;
import com.google.android.gms.games.PlayGames;
import com.google.android.gms.games.PlayGamesSdk;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.ump.ConsentInformation;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.Arrays;
import java.util.List;

public class LeaderboardActivity extends AppCompatActivity {
    private FirebaseAnalytics mFirebaseAnalytics;
    private AdView mAdView;
    GamesSignInClient gamesSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        setAnalytics();
        // setTestAds();
        // setAds();

        PlayGamesSdk.initialize(this);
        verifyGamesSignIn();
    }


    private void setAnalytics() {
        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
    }


    private void setTestAds() {
        List<String> testDeviceIds = Arrays.asList("20D91EB201806F1C7EA6457155F468D8", "62AEE42886038F87608F7F6F5D0B41BA");
        RequestConfiguration configuration =
                new RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build(); // Test ads
        MobileAds.setRequestConfiguration(configuration);                                   // Test ads
    }


    private void setAds() {
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        MobileAds.initialize(this, initializationStatus -> {
            mAdView.loadAd(adRequest);
            System.out.println("Is test device: " + adRequest.isTestDevice(this));
            System.out.println("Ads loaded: " + initializationStatus);
        });
    }


    private void verifyGamesSignIn() {
        gamesSignInClient = PlayGames.getGamesSignInClient(getParent());

        gamesSignInClient.isAuthenticated().addOnCompleteListener(isAuthenticatedTask -> {
            boolean isAuthenticated =
                    (isAuthenticatedTask.isSuccessful() &&
                            isAuthenticatedTask.getResult().isAuthenticated());

            if (isAuthenticated) {
                // Continue with Play Games Services
            } else {
                // Disable your integration with Play Games Services or show a
                // login button to ask  players to sign-in. Clicking it should
                // call GamesSignInClient.signIn().
                Toast.makeText(getApplicationContext(), "Log in to Google Play Games to view leaderboards", Toast.LENGTH_LONG).show();
                Button signIn = findViewById(R.id.buttonSignIn);
                signIn.setBackgroundColor(getResources().getColor(R.color.googlePlayGames));
                signIn.setEnabled(true);
                signIn.setVisibility(View.VISIBLE);
            }
        });
    }


    public void onDifficultyButtonClicked(View view) {
        int difficulty = Integer.parseInt((view).getTag().toString());

        String leaderboard_id = "";
        if (difficulty == 0)
            leaderboard_id = getString(R.string.leaderboard_easy);
        if (difficulty == 1)
            leaderboard_id = getString(R.string.leaderboard_normal);
        if (difficulty == 2)
            leaderboard_id = getString(R.string.leaderboard_hard);
        if (difficulty == 3)
            leaderboard_id = getString(R.string.leaderboard_extreme);

        PlayGames.getLeaderboardsClient(this)
                .getLeaderboardIntent(leaderboard_id)
                .addOnSuccessListener(intent -> startActivityForResult(intent, 1));
    }


    public void onSignInButtonClicked(View view) {
        gamesSignInClient.signIn();
        Button signIn = findViewById(R.id.buttonSignIn);
        signIn.setEnabled(false);
        signIn.setVisibility(View.INVISIBLE);
        verifyGamesSignIn();
    }


    public void onGoBackButtonClicked(View view) {
        finish();
    }

}
