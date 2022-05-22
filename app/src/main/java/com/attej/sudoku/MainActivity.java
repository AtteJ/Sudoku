package com.attej.sudoku;

import android.os.Bundle;

import com.attej.sudoku.backend.ExperienceBar;
import com.attej.sudoku.backend.Stats;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.games.GamesSignInClient;
import com.google.android.gms.games.PlayGames;
import com.google.android.gms.games.PlayGamesSdk;
import com.google.firebase.analytics.FirebaseAnalytics;

import com.google.android.ump.ConsentForm;
import com.google.android.ump.ConsentInformation;
import com.google.android.ump.ConsentRequestParameters;
import com.google.android.ump.UserMessagingPlatform;

import java.util.Arrays;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private int experience = 0;
    // private static final String TAG = "MainActivity";
    private AdView mAdView;
    private ConsentInformation consentInformation;
    private ConsentForm consentForm;
    FirebaseAnalytics mFirebaseAnalytics;
    GamesSignInClient gamesSignInClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setConsentForm();
        if (consentInformation.getConsentStatus() == ConsentInformation.ConsentStatus.NOT_REQUIRED) {
            setAnalytics();
            setTestAds();
            setAds();
            // recordEvent("consent_not_required", "consent_not_required", "Consent not required");
        }

        // consentInformation.reset();  // TODO: remove in prod
        refreshStats();
        PlayGamesSdk.initialize(this);
        verifyGamesSignIn();
    }


    private void setConsentForm() {
        // Set tag for underage of consent. false means users are not underage.
        ConsentRequestParameters params = new ConsentRequestParameters
                .Builder()
                .setTagForUnderAgeOfConsent(false)
                .build();

        consentInformation = UserMessagingPlatform.getConsentInformation(this);
        consentInformation.requestConsentInfoUpdate(
                this,
                params,
                () -> {
                    // The consent information state was updated.
                    // You are now ready to check if a form is available.
                    if (consentInformation.isConsentFormAvailable()) {
                        loadForm();
                    }

                },
                formError -> {
                    // Handle the error.
                    setAnalytics();
                });
    }


    public void loadForm() {
        UserMessagingPlatform.loadConsentForm(
                this,
                consentForm -> {
                    MainActivity.this.consentForm = consentForm;
                    if(consentInformation.getConsentStatus() == ConsentInformation.ConsentStatus.REQUIRED) {
                        consentForm.show(
                                MainActivity.this,
                                formError -> {
                                    // Handle dismissal by reloading form.
                                    loadForm();
                                });
                    }
                    if (consentInformation.getConsentStatus() == ConsentInformation.ConsentStatus.OBTAINED) {
                        Log.d(this.consentForm.toString(), "Consent obtained");
                        System.out.println("Consent succeeded");
                        setAnalytics();
                        setTestAds();
                        setAds();
                        Bundle bundle = new Bundle();
                        recordEvent("consent_obtained", "consent_obtained", "Consent obtained");
                    }

                },
                formError -> {
                    // Handle the error
                }
        );
    }


    private void recordEvent(String event, String id, String message) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, id);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, message);
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "main");
        mFirebaseAnalytics.logEvent(event, bundle);
    }


    private void setAnalytics() {
        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
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


    private void setTestAds() {
        List<String> testDeviceIds = Arrays.asList("20D91EB201806F1C7EA6457155F468D8", "62AEE42886038F87608F7F6F5D0B41BA");
        RequestConfiguration configuration =
              new RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build(); // Test ads
        MobileAds.setRequestConfiguration(configuration);                                   // Test ads
    }


    private void verifyGamesSignIn() {
        gamesSignInClient = PlayGames.getGamesSignInClient(getParent());

        gamesSignInClient.isAuthenticated().addOnCompleteListener(isAuthenticatedTask -> {
            boolean isAuthenticated =
                    (isAuthenticatedTask.isSuccessful() &&
                            isAuthenticatedTask.getResult().isAuthenticated());

            // Continue with Play Games Services
            // Disable your integration with Play Games Services or show a
            // login button to ask  players to sign-in. Clicking it should
            // call GamesSignInClient.signIn().
            enableSignInButton(!isAuthenticated);
        });
    }


    private void enableSignInButton(boolean enabled) {
        Button leaderboard = findViewById(R.id.buttonLeaderboard);
        Button achievements = findViewById(R.id.buttonAchievements);
        Button signIn = findViewById(R.id.buttonSignIn);

        int playGamesVisibility;
        int signInVisibility;
        if (enabled) {
            playGamesVisibility = View.INVISIBLE;
            signInVisibility = View.VISIBLE;
        }
        else {
            playGamesVisibility = View.VISIBLE;
            signInVisibility = View.INVISIBLE;
        }

        leaderboard.setVisibility(playGamesVisibility);
        leaderboard.setEnabled(!enabled);
        achievements.setVisibility(playGamesVisibility);
        achievements.setEnabled(!enabled);
        signIn.setVisibility(signInVisibility);
        signIn.setEnabled(enabled);
        signIn.setBackgroundColor(getResources().getColor(R.color.googlePlayGames));
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
                enableButtons(true);
                refreshStats();
            });


    private void enableButtons(boolean enabled) {
        findViewById(R.id.buttonStartNewGame).setEnabled(enabled);
        findViewById(R.id.buttonViewStats).setEnabled(enabled);
        findViewById(R.id.buttonLeaderboard).setEnabled(enabled);
        findViewById(R.id.buttonAchievements).setEnabled(enabled);
        findViewById(R.id.buttonSignIn).setEnabled(enabled);
    }


    public void onStartNewGameButtonClicked(View view) {
        Log.d(((Button) view).getText().toString(), "New game button clicked");
        enableButtons(false);
        Intent intent = new Intent(this, GameDifficultyActivity.class);
        NewGameActivityResultLauncher.launch(intent);
    }


    public void onViewStatsButtonClicked(View view) {
        Log.d(((Button) view).getText().toString(), "View stats button clicked");
        enableButtons(false);
        Intent intent = new Intent(this, StatsActivity.class);
        NewGameActivityResultLauncher.launch(intent);
    }


    public void onLeaderboardButtonClicked(View view) {
        Log.d(((Button) view).getText().toString(), "Leaderboard button clicked");
        enableButtons(false);
        Intent intent = new Intent(this, LeaderboardActivity.class);
        NewGameActivityResultLauncher.launch(intent);
    }


    public void onAchievementsButtonClicked(View view) {
        Log.d(((Button) view).getText().toString(), "Achievements button clicked");
        PlayGames.getAchievementsClient(this)
                .getAchievementsIntent()
                .addOnSuccessListener(intent -> startActivityForResult(intent, 1));
    }

    public void onSignInButtonClicked(View view) {
        gamesSignInClient.signIn();
        Button signIn = findViewById(R.id.buttonSignIn);
        signIn.setEnabled(false);
        signIn.setVisibility(View.INVISIBLE);
        verifyGamesSignIn();
    }

}