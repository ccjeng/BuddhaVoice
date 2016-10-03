package com.oddsoft.buddhavoice2.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.oddsoft.buddhavoice2.BuddhaVoice;
import com.oddsoft.buddhavoice2.R;
import com.oddsoft.buddhavoice2.utils.Analytics;
import com.oddsoft.buddhavoice2.utils.Constant;
import com.oddsoft.buddhavoice2.view.base.BaseActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

public class VoiceListener extends BaseActivity {
    private static final String TAG = "VoiceListener";
    private MediaPlayer mp;

    @Bind(R.id.songcontent)
    TextView songcontentTextView;

    @Bind(R.id.songinfo)
    TextView songinfoTextView;

    @Bind(R.id.main)
    NestedScrollView mainScollView;

    private String[] songInfo, songInfo1;
    private String[] songContent, songContent1;
    private String tabName;
    private String PATH;
    //private String onlinePreference;

    private AdView adView;
    private Analytics ga;

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.progress_wheel)
    ProgressBar progressWheel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listener);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

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
        getSupportActionBar().setTitle(itemName);
        if (tabName.equals("TAB1")) {
            songinfoTextView.setText(songInfo[itemNumber]);
            songcontentTextView.setText(songContent[itemNumber]);
        } else {
            songinfoTextView.setText(songInfo1[itemNumber]);
            songcontentTextView.setText(songContent1[itemNumber]);
        }

        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        playMusic(itemNumber, itemName);


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

    @Override
    protected void onStart() {
        super.onStart();
        getPrefs();
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

                    progressWheel.setVisibility(View.VISIBLE);
                    mainScollView.setVisibility(View.GONE);
                    //play music
                    new Thread() {
                        public void run() {
                            try {
                                //play music
                                mp.setDataSource(PATH);
                                mp.prepare();
                                mp.setLooping(true);
                                mp.start();

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();

                    mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            progressWheel.setVisibility(View.GONE);
                            mainScollView.setVisibility(View.VISIBLE);
                        }
                    });


                } else {
                    //network is not available
                    showError();
                }
            } catch (IllegalStateException e) {
                showError();
                e.printStackTrace();
            } /*catch (IOException e) {
                showError();
                e.printStackTrace();
            }*/
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

        LinearLayout adBannerLayout = (LinearLayout) findViewById(R.id.footerLayout);

        adView = new AdView(this);
        adView.setAdUnitId(Constant.ADMob_BuddhaVoice);
        adView.setAdSize(AdSize.SMART_BANNER);
        adBannerLayout.addView(adView);

        AdRequest adRequest;

        if (BuddhaVoice.APPDEBUG) {
            //Test Mode
            adRequest = new AdRequest.Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .addTestDevice(Constant.ADMob_TestDeviceID)
                    .build();
        } else {

            adRequest = new AdRequest.Builder().build();

        }
        adView.loadAd(adRequest);

    }

    private boolean isNetworkAvailable() {

        ConnectivityManager connMgr = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    private void showError() {
        progressWheel.setVisibility(View.GONE);
        mainScollView.setVisibility(View.VISIBLE);

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


}

