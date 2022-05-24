package com.attej.sudoku;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.attej.sudoku.backend.AdManager;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.Arrays;
import java.util.List;

public class GameLostActivity extends AppCompatActivity {
    FirebaseAnalytics mFirebaseAnalytics;
    private RewardedAd mRewardedAd;

    private final String TAG = "GameLostActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_lost);

        setAnalytics();
    }


    public void onContinueButtonClicked(View view) {
        Log.d(view.toString(), "Game continued");
        getNewTry();
    }


    public void onNewGameButtonClicked (View view) {
        Log.d(view.toString(), "New game");
        Intent intent = new Intent();
        intent.putExtra("Ad watched", 0);
        setResult(1, intent);
        finish();
    }


    private void getNewTry() {
        setTestAds();
        showAd();
        checkReward();
    }


    private void recordEvent(String event, String id, String message) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, id);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, message);
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "game_lost_activity");
        mFirebaseAnalytics.logEvent(event, bundle);
    }


    private void setAnalytics() {
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
    }


    private void setTestAds() {
        List<String> testDeviceIds = Arrays.asList("20D91EB201806F1C7EA6457155F468D8", "62AEE42886038F87608F7F6F5D0B41BA");
        RequestConfiguration configuration =
                new RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build(); // Test ads
        MobileAds.setRequestConfiguration(configuration);                                   // Test ads
    }


    private void showAd() {
        mRewardedAd = AdManager.getAd();
        AdManager.setNull();

        if (mRewardedAd != null)
            mRewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdShowedFullScreenContent() {
                    // Called when ad is shown.
                    Log.d(TAG, "Ad was shown.");
                }

                @Override
                public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                    // Called when ad fails to show.
                    Log.d(TAG, "Ad failed to show.");
                    Toast.makeText(getApplicationContext(), "Ad failed to load", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.putExtra("Ad watched", 2);
                    setResult(1, intent);
                    finish();
                }

                @Override
                public void onAdDismissedFullScreenContent() {
                    // Called when ad is dismissed.
                    // Set the ad reference to null so you don't show the ad a second time.
                    Log.d(TAG, "Ad was dismissed.");
                    AdManager.setNull();
                    Intent intent = new Intent();
                    intent.putExtra("Ad watched", 0);
                    setResult(1, intent);
                    finish();
                }
            });
        else {
            Intent intent = new Intent();
            intent.putExtra("Ad watched", 2);
            setResult(1, intent);
            finish();
        }
    }


    private void checkReward() {
        if (mRewardedAd != null) {
            Activity activityContext = GameLostActivity.this;
            mRewardedAd.show(activityContext, rewardItem -> {
                // Handle the reward.
                Log.d(TAG, "The user earned the reward.");
                recordEvent("rewarded_ad_shown", "rewarded_ad_shown", "User watched rewarded ad");
                Intent intent = new Intent();
                intent.putExtra("Ad watched", 1);
                setResult(1, intent);
                finish();
            });
        } else {
            Log.d(TAG, "The rewarded ad wasn't ready yet.");
            recordEvent("reward_ad_failed", "reward_ad_failed", "Failed to load rewarded ad");
            Intent intent = new Intent();
            intent.putExtra("Ad watched", 2);
            setResult(1, intent);
            finish();
        }
    }
}
