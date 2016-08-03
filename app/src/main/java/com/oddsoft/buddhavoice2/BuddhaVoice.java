package com.oddsoft.buddhavoice2;

import android.app.Application;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Logger;
import com.google.android.gms.analytics.Tracker;

import java.util.HashMap;

import me.majiajie.swipeback.utils.ActivityStack;

/**
 * Created by andycheng on 2015/6/29.
 */
public class BuddhaVoice extends Application {
    // Debugging switch
    public static final boolean APPDEBUG = BuildConfig.DEBUG;

    // Debugging tag for the application
    public static final String APPTAG = "BuddhaVoice";

    public BuddhaVoice() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.registerActivityLifecycleCallbacks(ActivityStack.getInstance());
    }


    // The following line should be changed to include the correct property id.
    private static final String PROPERTY_ID = "UA-19743390-14";
    public enum TrackerName {
        APP_TRACKER // Tracker used only in this app.
    }
    HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();

    public synchronized Tracker getTracker(TrackerName trackerId) {
        if (!mTrackers.containsKey(trackerId)) {

            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            analytics.getLogger().setLogLevel(Logger.LogLevel.VERBOSE);
            if (APPDEBUG) {
                analytics.getInstance(this).setDryRun(true);
            }
            Tracker t = (trackerId == TrackerName.APP_TRACKER) ? analytics.newTracker(PROPERTY_ID)
                    : analytics.newTracker(R.xml.global_tracker);
            t.enableAdvertisingIdCollection(true);
            mTrackers.put(trackerId, t);
        }
        return mTrackers.get(trackerId);
    }
}
