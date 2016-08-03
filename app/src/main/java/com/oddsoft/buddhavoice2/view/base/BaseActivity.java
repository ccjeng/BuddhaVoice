package com.oddsoft.buddhavoice2.view.base;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.oddsoft.buddhavoice2.BuddhaVoice;

import java.util.Locale;

import me.majiajie.swipeback.SwipeBackActivity;

/**
 * Created by andycheng on 2016/8/3.
 */
public class BaseActivity extends SwipeBackActivity {


    @Override
    protected void onStart() {
        super.onStart();
        if (!BuddhaVoice.APPDEBUG)
            GoogleAnalytics.getInstance(this).reportActivityStart(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (!BuddhaVoice.APPDEBUG)
            GoogleAnalytics.getInstance(this).reportActivityStop(this);
    }

    public void getPrefs() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String langPreference = prefs.getString("lang", "NA");
        //onlinePreference = prefs.getString("online", "google");
        String lang = null;

        Locale appLoc;
        if (langPreference.equals("NA")) {
            appLoc = new Locale(Locale.getDefault().getLanguage());
        } else if (langPreference.equals("zh_TW")) {
            lang = langPreference.substring(0, 2).toLowerCase();
            appLoc = new Locale("zh", "TW");
        } else if (langPreference.equals("zh_CN")) {
            lang = langPreference.substring(0, 2).toLowerCase();
            appLoc = new Locale("zh", "CN");
        } else {
            lang = langPreference.substring(0, 2).toLowerCase();
            appLoc = new Locale(lang);
        }

        //Log.d(TAG, "langPreference=" + langPreference);
        if (!langPreference.equals("NA")) {
            Locale.setDefault(appLoc);
            Configuration appConfig = new Configuration();
            appConfig.locale = appLoc;
            getBaseContext().getResources().updateConfiguration(appConfig,
                    getBaseContext().getResources().getDisplayMetrics());
        }
    }
}
