package com.attej.sudoku.backend;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.Arrays;
import java.util.List;

public class AdManager {
    private static RewardedAd mRewardedAd;
    private static FirebaseAnalytics mFirebaseAnalytics;

    public static void loadAd(Context context) {
        setAnalytics(context);
        setTestAds();

        AdRequest adRequest = new AdRequest.Builder().build();

        RewardedAd.load(context, "ca-app-pub-6148517439938867/1368714923",
                adRequest, new RewardedAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error.
                        Log.d("AdManager", loadAdError.getMessage());
                        mRewardedAd = null;
                        recordEvent("ad_loaded_fail", "ad_loaded_fail", "Ad failed to load");
                    }

                    @Override
                    public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                        mRewardedAd = rewardedAd;
                        Log.d("AdManager", "Ad was loaded.");
                        recordEvent("ad_loaded", "ad_loaded", "Ad was loaded");
                    }
                });

        final Handler adHandler = new Handler();
        final int delay = 5000;

        adHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mRewardedAd == null)
                    loadAd(context);
                adHandler.postDelayed(this, delay);
            }
        }, delay);
    }


    private static void setTestAds() {
        List<String> testDeviceIds = Arrays.asList("20D91EB201806F1C7EA6457155F468D8", "62AEE42886038F87608F7F6F5D0B41BA");
        RequestConfiguration configuration =
                new RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build(); // Test ads
        MobileAds.setRequestConfiguration(configuration);                                   // Test ads
    }


    public static RewardedAd getAd() {
        return mRewardedAd;
    }


    public static void setNull() {
        mRewardedAd = null;
    }


    private static void recordEvent(String event, String id, String message) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, id);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, message);
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "game_lost_activity");
        mFirebaseAnalytics.logEvent(event, bundle);
    }


    private static void setAnalytics(Context context) {
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
    }
}
