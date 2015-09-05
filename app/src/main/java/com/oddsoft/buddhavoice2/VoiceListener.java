package com.oddsoft.buddhavoice2;

import java.io.IOException;
import java.util.Locale;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import com.google.android.gms.ads.*;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.oddsoft.buddhavoice2.app.Analytics;
import com.oddsoft.buddhavoice2.app.BuddhaVoice;

import butterknife.Bind;
import butterknife.ButterKnife;

public class VoiceListener extends ActionBarActivity {
    private static final String TAG = "VoiceListener";
    private MediaPlayer mp;

    @Bind(R.id.songname)
    TextView songnameTextView;

    @Bind(R.id.songcontent)
    TextView songcontentTextView;

    @Bind(R.id.songinfo)
    TextView songinfoTextView;

    private String[] songInfo, songInfo1;
    private String[] songContent, songContent1;
    private String tabName;
    private String PATH;
    private String onlinePreference;
    private ProgressDialog dialog = null;

    private AdView adView;
    private Analytics ga;

    @Bind(R.id.tool_bar)
    Toolbar toolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listener);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        toolbar.setNavigationIcon(new IconicsDrawable(this)
                .icon(GoogleMaterial.Icon.gmd_arrow_back)
                .color(Color.WHITE)
                .actionBarSize());

        // Menu item click 的監聽事件一樣要設定在 setSupportActionBar 才有作用
        toolbar.setOnMenuItemClickListener(onMenuItemClick);

        ga = new Analytics();
        ga.trackerPage(this);

        //get intent values
        Bundle bunde = this.getIntent().getExtras();
        final int itemNumber = Integer.parseInt(bunde.getString("KEY_NBR"));
        final String itemName = bunde.getString("KEY_NAME");
        tabName = bunde.getString("TAB");

        findViews();
        ADView();

        //show song name
        songnameTextView.setText(itemName);
        if (tabName.equals("TAB1")) {
            songinfoTextView.setText(songInfo[itemNumber]);
            songcontentTextView.setText(songContent[itemNumber]);
        } else {
            songinfoTextView.setText(songInfo1[itemNumber]);
            songcontentTextView.setText(songContent1[itemNumber]);
        }

        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        dialog = ProgressDialog.show(VoiceListener.this, "", getString(R.string.loading), true, true);

        new Thread() {
            public void run() {
                try {
                    //play music
                    playMusic(itemNumber, itemName);

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    dialog.dismiss();
                }
            }
        }.start();


    }

    private void findViews() {
        songInfo = getResources().getStringArray(R.array.itemSongsInfo);
        songContent = getResources().getStringArray(R.array.itemSongsCont);
        songInfo1 = getResources().getStringArray(R.array.itemSongsInfo1);
        songContent1 = getResources().getStringArray(R.array.itemSongsCont1);
    }

    private Toolbar.OnMenuItemClickListener onMenuItemClick = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {

            switch (menuItem.getItemId()) {
                case android.R.id.home:
                    finish();
                    break;
                case R.id.stop_button:
                    ga.trackEvent(VoiceListener.this, "Click", "Button", "Stop", 0);
                    stopMusic();
                    //back to main
                    goIntent();
                    break;
                case R.id.pause_button:
                    ga.trackEvent(VoiceListener.this, "Click", "Button", "Pause", 0);
                    if (mp.isPlaying())
                        pauseMusic();
                    else
                        resumeMusic();
                    break;
            }

            return true;
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_listener, menu);

        MenuItem menuItem1 = menu.findItem(R.id.stop_button);
        menuItem1.setIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_stop).actionBarSize().color(Color.WHITE));

        MenuItem menuItem2 = menu.findItem(R.id.pause_button);
        menuItem2.setIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_pause).actionBarSize().color(Color.WHITE));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(menuItem);
    }
    @Override
    protected void onPause() {
        if (adView != null)
            adView.pause();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adView != null)
            adView.resume();
    }

    @Override
    protected void onDestroy() {
        // Terminate extra threads here
        stopMusic();
        if (adView != null)
            adView.destroy();
        super.onDestroy();
    }

    protected void onStop() {
        super.onStop();
        GoogleAnalytics.getInstance(this).reportActivityStop(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        getPrefs();
        GoogleAnalytics.getInstance(this).reportActivityStart(this);
    }

    private void playMusic(int itemnumber, String itemname) {
        int resId = 0;
        if (tabName.equals("TAB1")) {
            switch (itemnumber) {
                case 0:
                    resId = R.raw.song01;
                    break;
                case 1:
                    resId = R.raw.song02;
                    break;
                case 2:
                    resId = R.raw.song03;
                    break;
                case 3:
                    resId = R.raw.song04;
                    break;
                case 4:
                    resId = R.raw.song05;
                    break;
                case 5:
                    resId = R.raw.song06;
                    break;
                case 6:
                    resId = R.raw.song07;
                    break;
                case 7:
                    resId = R.raw.song08;
                    break;
                case 8:
                    resId = R.raw.song09;
                    break;
                case 9:
                    resId = R.raw.song10;
                    break;
                case 10:
                    resId = R.raw.song11;
                    break;
                case 11:
                    resId = R.raw.song12;
                    break;
                default:
                    break;
            }
        } else if (tabName.equals("TAB2")) {
            PATH = null;
            String mp3 = "";

            switch (itemnumber) {
                case 0:
                    mp3 = "Prajnaparamita.mp3";
                    //	PATH="http://sites.google.com/site/androidbuddhavoice/Prajnaparamita.mp3?attredirects=0&d=1";
                    //  PATH="http://dl.dropbox.com/u/128583/Prajnaparamita.mp3";
                    break;
                case 1:
                    mp3 = "XJ0502.mp3";
                    //	PATH="http://sites.google.com/site/androidbuddhavoice/XJ0502.mp3?attredirects=0&d=1";
                    //  PATH="http://dl.dropbox.com/u/128583/XJ0502.mp3";
                    break;
                case 2:
                    mp3 = "great1.mp3";
                    //	PATH="http://sites.google.com/site/androidbuddhavoice/great1.mp3?attredirects=0&d=1";
                    //	PATH="http://dl.dropbox.com/u/128583/great1.mp3";
                    break;
                case 3:
                    mp3 = "great2.mp3";
                    //	PATH="http://sites.google.com/site/androidbuddhavoice/great2.mp3?attredirects=0&d=1";
                    //	PATH="http://dl.dropbox.com/u/128583/great2.mp3";
                    break;
                case 4:
                    mp3 = "Menla_Mantra.mp3";
                    //	PATH="http://sites.google.com/site/androidbuddhavoice/Menla_Mantra.mp3?attredirects=0&d=1";
                    //	PATH="http://dl.dropbox.com/u/128583/Menla_Mantra.mp3";
                    break;
                case 5:
                    mp3 = "LJM093-02-Nian.Mp3";
                    //	PATH="http://sites.google.com/site/androidbuddhavoice/LJM093-02-Nian.Mp3?attredirects=0&d=1";
                    //	PATH="http://dl.dropbox.com/u/128583/LJM093-02-Nian.Mp3";
                    break;
            }
            //if (onlinePreference.equals("google"))
            //    PATH = "http://sites.google.com/site/androidbuddhavoice/" + mp3 + "?attredirects=0&d=1";
            //if (onlinePreference.equals("dropbox"))
                PATH = "http://dl.dropbox.com/u/128583/" + mp3;
        }
        stopMusic();
        startMusic(resId);
    }

    private void startMusic(int resourceID) {
        if (tabName.equals("TAB2")) {
            //online version
            Log.d(TAG, "online version=" + PATH);

            mp = new MediaPlayer();
            try {
                if (isNetworkAvailable()) {
                    mp.setDataSource(PATH);
                    mp.prepare();
                    mp.setLooping(true);
                    mp.start();
                } else {
                    //network is not available
                    showError();
                }
            } catch (IllegalStateException e) {
                showError();
                e.printStackTrace();
            } catch (IOException e) {
                showError();
                e.printStackTrace();
            }
        } else {
            //offline version
            try {
                mp = MediaPlayer.create(getBaseContext(), resourceID);
                mp.setLooping(true);
                mp.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void stopMusic() {
        if (mp != null) {
            mp.stop();
            mp.release();
            mp = null;
        }
    }

    private void pauseMusic() {
        if (mp != null) {
            mp.pause();
        }
    }

    private void resumeMusic() {
        if (mp != null) {
            mp.setLooping(true);
            mp.start();
        }
    }
    //back to main menu
    private void goIntent() {
        Intent intent;
        intent = this.getIntent();
        // back to main menu
        VoiceListener.this.setResult(RESULT_OK, intent);
        // end this activity
        VoiceListener.this.finish();
    }

    private void ADView() {
        adView = (AdView) findViewById(R.id.adView);

        AdRequest adRequest;

        if (BuddhaVoice.APPDEBUG) {
            adRequest = new AdRequest.Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)       // 仿真器
                    .addTestDevice("7710C21FF2537758BF3F80963477D68E") // 我的 Galaxy Nexus 測試手機
                    .build();
        } else {
            adRequest = new AdRequest.Builder().build();
        }

        adView.loadAd(adRequest);
    }

    private boolean isNetworkAvailable() {
        final ConnectivityManager connMgr = (ConnectivityManager)
                this.getSystemService(Context.CONNECTIVITY_SERVICE);

        final android.net.NetworkInfo wifi =
                connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        final android.net.NetworkInfo mobile =
                connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if (wifi.isAvailable()) {
            return true;
        } else if (mobile.isAvailable()) {
            return true;
        } else {
            return false;
        }
    }

    private void showError() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.app_name)
                .setMessage(R.string.no_connection)
                .setPositiveButton(R.string.ok_label,
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    DialogInterface dialoginterface, int i) {
                                //empty
                            }
                        })
                .show();
    }

    private void getPrefs() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String langPreference = prefs.getString("lang", "NA");
        onlinePreference = prefs.getString("online", "google");
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

        Log.d(TAG, "langPreference=" + langPreference);
        if (!langPreference.equals("NA")) {
            Locale.setDefault(appLoc);
            Configuration appConfig = new Configuration();
            appConfig.locale = appLoc;
            getBaseContext().getResources().updateConfiguration(appConfig,
                    getBaseContext().getResources().getDisplayMetrics());
        }
    }

}

