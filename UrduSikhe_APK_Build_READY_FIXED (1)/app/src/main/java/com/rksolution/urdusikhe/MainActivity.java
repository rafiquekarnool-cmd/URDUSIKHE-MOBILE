package com.rksolution.urdusikhe;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;

public class MainActivity extends Activity {
    private static final String LIVE_BANNER_AD_UNIT_ID =
            "ca-app-pub-3822431624321367/9355830974";
    private static final String TEST_BANNER_AD_UNIT_ID =
            "ca-app-pub-3940256099942544/6300978111";

    private WebView webView;
    private AdView bannerAd;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RequestConfiguration requestConfiguration =
                new RequestConfiguration.Builder()
                        .setTagForChildDirectedTreatment(
                                RequestConfiguration.TAG_FOR_CHILD_DIRECTED_TREATMENT_TRUE)
                        .setMaxAdContentRating(
                                RequestConfiguration.MAX_AD_CONTENT_RATING_G)
                        .build();
        MobileAds.setRequestConfiguration(requestConfiguration);
        MobileAds.initialize(this, status -> {});

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        webView = new WebView(this);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(true);
        settings.setBuiltInZoomControls(false);
        settings.setDisplayZoomControls(false);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl("file:///android_asset/index.html");

        root.addView(webView, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));

        boolean isDebuggable =
                (getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        bannerAd = new AdView(this);
        bannerAd.setAdSize(AdSize.BANNER);
        bannerAd.setAdUnitId(isDebuggable
                ? TEST_BANNER_AD_UNIT_ID
                : LIVE_BANNER_AD_UNIT_ID);
        root.addView(bannerAd, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

        Bundle nonPersonalizedAds = new Bundle();
        nonPersonalizedAds.putString("npa", "1");
        AdRequest adRequest = new AdRequest.Builder()
                .addNetworkExtrasBundle(AdMobAdapter.class, nonPersonalizedAds)
                .build();
        bannerAd.loadAd(adRequest);

        setContentView(root);
    }

    @Override
    public void onBackPressed() {
        if (webView != null && webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onPause() {
        if (bannerAd != null) bannerAd.pause();
        if (webView != null) webView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (webView != null) webView.onResume();
        if (bannerAd != null) bannerAd.resume();
    }

    @Override
    protected void onDestroy() {
        if (bannerAd != null) bannerAd.destroy();
        if (webView != null) webView.destroy();
        super.onDestroy();
    }
}
