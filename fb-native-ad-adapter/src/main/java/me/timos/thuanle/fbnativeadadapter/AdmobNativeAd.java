package me.timos.thuanle.fbnativeadadapter;

import android.content.Context;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.formats.MediaView;
import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;
import com.rockerhieu.rvadapter.RecyclerViewAdapterWrapper;

public class AdmobNativeAd extends RecyclerViewAdapterWrapper {

    private static final int TYPE_FB_NATIVE_ADS = 900;
    private static final int DEFAULT_AD_ITEM_INTERVAL = 10;
    private final AdmobParam admobParam;


    private AdmobNativeAd(AdmobParam admobParam) {
        super(admobParam.adapter);
        this.admobParam = admobParam;

        assertConfig();
        setSpanAds();
    }

    private void assertConfig() {
        if (admobParam.gridLayoutManager != null) {
            //if user set span ads
            int nCol = admobParam.gridLayoutManager.getSpanCount();
            if (admobParam.adItemInterval % nCol != 0) {
                throw new IllegalArgumentException(String.format("The adItemInterval (%d) is not divisible by number of columns in GridLayoutManager (%d)", admobParam.adItemInterval, nCol));
            }
        }
    }

    private int convertAdPosition2OrgPosition(int position) {
        return position - (position + 1) / (admobParam.adItemInterval + 1);
    }

    @Override
    public int getItemCount() {
        int realCount = super.getItemCount();
        return realCount + realCount / admobParam.adItemInterval;
    }

    @Override
    public int getItemViewType(int position) {
        if (isAdPosition(position)) {
            return TYPE_FB_NATIVE_ADS;
        }
        return super.getItemViewType(convertAdPosition2OrgPosition(position));
    }

    private boolean isAdPosition(int position) {
        return (position + 1) % (admobParam.adItemInterval + 1) == 0;
    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_FB_NATIVE_ADS) {
            onBindViewHolder(holder);
        } else {
            super.onBindViewHolder(holder, convertAdPosition2OrgPosition(position));
        }

    }

    private void onBindViewHolder(final RecyclerView.ViewHolder holder){
        final UnifiedNativeAdViewHolder unifiedNativeAdViewHolder = (UnifiedNativeAdViewHolder) holder;
        if (admobParam.forceReloadAdOnBind || !unifiedNativeAdViewHolder.loaded){
                AdLoader adLoader = new AdLoader.Builder(((UnifiedNativeAdViewHolder) holder).getContext(), admobParam.facebookPlacementId)
                        .forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
                            @Override
                            public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                                // Show the native ad.
                                unifiedNativeAdViewHolder.adCard.setVisibility(View.VISIBLE);
                                unifiedNativeAdViewHolder.adView.setNativeAd(unifiedNativeAd);
                                populateNativeAdView(unifiedNativeAd, unifiedNativeAdViewHolder.adView);
                            }
                        })
                        .withAdListener(new AdListener() {
                            @Override
                            public void onAdFailedToLoad(int errorCode) {
                                // Handle the failure byonCreateViewHolder logging, altering the UI, and so on.
                                Log.d("ADMOB_AD" , ""+errorCode);
                            }
                        })
                        .withNativeAdOptions(new NativeAdOptions.Builder()
                                .build())
                        .build();
            adLoader.loadAd(new AdRequest.Builder().build());

        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_FB_NATIVE_ADS) {
            return onCreateAdViewHolder(parent);
        }
        return super.onCreateViewHolder(parent, viewType);
    }
    private void setSpanAds() {
        if (admobParam.gridLayoutManager == null) {
            return;
        }
        final GridLayoutManager.SpanSizeLookup spl = admobParam.gridLayoutManager.getSpanSizeLookup();
        admobParam.gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (isAdPosition(position)) {
                    return spl.getSpanSize(position);
                }
                return 1;
            }
        });
    }

    public static class Builder {

        private final AdmobParam admobParam;

        private Builder(AdmobParam admobParam) {
            this.admobParam = admobParam;
        }

        public static Builder with(String placementId, RecyclerView.Adapter wrapped) {
            AdmobParam admobParam = new AdmobParam();
            admobParam.admobNativeAdId = placementId;
            admobParam.adapter = wrapped;

            //default value
            admobParam.adItemInterval = DEFAULT_AD_ITEM_INTERVAL;
            admobParam.itemContainerLayoutRes = R.layout.item_facebook_native_ad_outline;
            admobParam.itemContainerId = R.id.ad_container;
            admobParam.forceReloadAdOnBind = true;
            return new Builder(admobParam);
        }

        public Builder adItemInterval(int interval) {
            admobParam.adItemInterval = interval;
            return this;
        }

        public Builder adLayout(@LayoutRes int layoutContainerRes, @IdRes int itemContainerId) {
            admobParam.itemContainerLayoutRes = layoutContainerRes;
            admobParam.itemContainerId = itemContainerId;
            return this;
        }

        public AdmobNativeAd build() {
            return new AdmobNativeAd(admobParam);
        }

        public Builder enableSpanRow(GridLayoutManager layoutManager) {
            admobParam.gridLayoutManager = layoutManager;
            return this;
        }

        public Builder forceReloadAdOnBind(boolean forced) {
            admobParam.forceReloadAdOnBind = forced;
            return this;
        }
    }

    private RecyclerView.ViewHolder onCreateAdViewHolder(ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View adLayoutOutline = inflater
                .inflate(admobParam.itemContainerLayoutRes, parent, false);
        ViewGroup vg = adLayoutOutline.findViewById(admobParam.itemContainerId);

        LinearLayout adLayoutContent = (LinearLayout) inflater
                .inflate(R.layout.item_admob_native_ad, parent, false);
        vg.addView(adLayoutContent);
        return new UnifiedNativeAdViewHolder(adLayoutOutline);
    }


    private void populateNativeAdView(UnifiedNativeAd nativeAd,
                                      UnifiedNativeAdView adView) {
        // Some assets are guaranteed to be in every UnifiedNativeAd.
        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());
        ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());

        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        NativeAd.Image icon = nativeAd.getIcon();

        if (icon == null) {
            adView.getIconView().setVisibility(View.INVISIBLE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(icon.getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getPrice() == null) {
            adView.getPriceView().setVisibility(View.INVISIBLE);
        } else {
            adView.getPriceView().setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
        }

        if (nativeAd.getStore() == null) {
            adView.getStoreView().setVisibility(View.INVISIBLE);
        } else {
            adView.getStoreView().setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
        }

        if (nativeAd.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) adView.getStarRatingView())
                    .setRating(nativeAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getAdvertiser() == null) {
            adView.getAdvertiserView().setVisibility(View.INVISIBLE);
        } else {
            ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
            adView.getAdvertiserView().setVisibility(View.VISIBLE);
        }

        // Assign native ad object to the native view.
        adView.setNativeAd(nativeAd);
    }


    public class UnifiedNativeAdViewHolder extends RecyclerView.ViewHolder {

        private UnifiedNativeAdView adView;
        boolean loaded;
        public LinearLayout adCard;

        UnifiedNativeAdViewHolder(View view) {
            super(view);
            adView = (UnifiedNativeAdView) view.findViewById(R.id.ad_view);
            adCard = (LinearLayout) view.findViewById(R.id.ad_card_view);

            // The MediaView will display a video asset if one is present in the ad, and the
            // first image asset otherwise.
            adView.setMediaView((MediaView) adView.findViewById(R.id.ad_media));

            // Register the view used for each individual asset.
            adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
            adView.setBodyView(adView.findViewById(R.id.ad_body));
            adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
            adView.setIconView(adView.findViewById(R.id.ad_icon));
            adView.setPriceView(adView.findViewById(R.id.ad_price));
            adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
            adView.setStoreView(adView.findViewById(R.id.ad_store));
            adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));
            loaded = false;

        }

        Context getContext() {
            return adCard.getContext();
        }
    }

    private static class AdmobParam {
        String admobNativeAdId;
        RecyclerView.Adapter adapter;
        int adItemInterval;
        boolean forceReloadAdOnBind;

        @LayoutRes
        int itemContainerLayoutRes;

        @IdRes
        int itemContainerId;

        GridLayoutManager gridLayoutManager;
    }
}
