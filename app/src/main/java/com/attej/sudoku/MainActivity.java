package com.attej.sudoku;

import android.os.Bundle;

import com.attej.sudoku.backend.ExperienceBar;
import com.attej.sudoku.backend.Stats;

import android.content.Intent;
import android.view.View;
import android.widget.Button;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import com.google.android.gms.ads.RequestConfiguration;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setConsentForm();
        if (consentInformation.getConsentStatus() == ConsentInformation.ConsentStatus.NOT_REQUIRED) {
            setAnalytics();
            setTestAds();
            setAds();
        }

        // consentInformation.reset();  // TODO: remove in prod
        refreshStats();
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
                        System.out.println("Consent succeeded");
                        setAnalytics();
                        setTestAds();
                        setAds();
                    }

                },
                formError -> {
                    // Handle the error
                }
        );
    }


    private void setAnalytics() {
        // Obtain the FirebaseAnalytics instance.
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "Game opened");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Main");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "main");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
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
                if (result.getResultCode() == 1) {
                    enableButtons(true);
                    refreshStats();
                }
            });


    private void enableButtons(boolean enabled) {
        Button newGame = findViewById(R.id.buttonStartNewGame);
        Button stats = findViewById(R.id.buttonViewStats);

        newGame.setEnabled(enabled);
        stats.setEnabled(enabled);
    }


    public void onStartNewGameButtonClicked(View view) {
        enableButtons(false);
        Intent intent = new Intent(this, GameDifficultyActivity.class);
        NewGameActivityResultLauncher.launch(intent);
    }


    public void onViewStatsButtonClicked(View view) {
        enableButtons(false);
        Intent intent = new Intent(this, StatsActivity.class);
        NewGameActivityResultLauncher.launch(intent);
    }

}