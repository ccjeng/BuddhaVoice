package com.oddsoft.buddhavoice2.app;

import android.app.Activity;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

/**
 * Created by andycheng on 2015/6/28.
 */
public class Analytics {
    public static void initTracker(Activity activity) {
        Tracker t = ((BuddhaVoice) activity.getApplication()).getTracker(
                BuddhaVoice.TrackerName.APP_TRACKER);
        t.setScreenName(activity.getClass().getSimpleName());
        t.send(new HitBuilders.AppViewBuilder().build());
    }
}
