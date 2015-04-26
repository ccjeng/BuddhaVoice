package com.oddsoft.buddhavoice2;

import java.io.IOException;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
/*
import com.adsdk.sdk.Ad;
import com.adsdk.sdk.AdListener;
import com.adsdk.sdk.AdManager;
import com.adsdk.sdk.banner.AdView;
*/
public class VoiceListener extends Activity
        implements OnClickListener/*, AdListener*/ {
    private static final String TAG = "VoiceListener";
    private MediaPlayer mp;
    private TextView statusTextView;
    private TextView songnameTextView;
    private TextView songcontentTextView;
    private TextView songinfoTextView;
    private View stopButton;
    private String[] songInfo;
    private String[] songContent;
    private String tabName;
    private String PATH;
    private String onlinePreference;
    private ProgressDialog dialog = null;

    private RelativeLayout layout;
    //private AdView mAdView;
    //private AdManager mManager;

    final Handler updateHandler = new Handler();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.listener);
        //get intent values
        Bundle bunde = this.getIntent().getExtras();
        final int itemNumber = Integer.parseInt(bunde.getString("KEY_NBR"));
        final String itemName = bunde.getString("KEY_NAME");
        tabName = "tabOff"; //bunde.getString("SourceTab");

        findViews();

        //show song name
        songnameTextView.setText(itemName);
        songinfoTextView.setText(songInfo[itemNumber]);
        songcontentTextView.setText(songContent[itemNumber]);
//        ADView();

        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        stopButton.setOnClickListener(this);

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
        layout = (RelativeLayout) findViewById(R.id.RelativeLayout);

        statusTextView = (TextView) findViewById(R.id.status);
        songnameTextView = (TextView) findViewById(R.id.songname);
        songcontentTextView = (TextView) findViewById(R.id.songcontent);
        songinfoTextView = (TextView) findViewById(R.id.songinfo);
        stopButton = (View) findViewById(R.id.stop_button);

        songInfo = getResources().getStringArray(R.array.itemSongsInfo);
        songContent = getResources().getStringArray(R.array.itemSongsCont);
    }
/*
    private void ADView() {
        if (mAdView != null) {
            removeBanner();
        }
        mAdView = new AdView(this, "http://my.mobfox.com/request.php",
                "fe96717d9875b9da4339ea5367eff1ec", true, true);
        //mAdView.setAdspaceWidth(320); // Optional, used to set the custom size of banner placement. Without setting it, the SDK will use default size of 320x50 or 300x50 depending on device type.
        //mAdView.setAdspaceHeight(50);
        //mAdView.setAdspaceStrict(false); // Optional, tells the server to only supply banner ads that are exactly of the desired size. Without setting it, the server could also supply smaller Ads when no ad of desired size is available.
        mAdView.setAdListener(this);
        layout.addView(mAdView);
    }

    private void removeBanner(){
        if(mAdView!=null){
            layout.removeView(mAdView);
            mAdView = null;
        }
    }
*/
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.stop_button:
                stopMusic();
                //back to main
                goIntent();
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(this, Prefs.class));
                return true;
            case R.id.about_settings:
                //show about message
                new AlertDialog.Builder(this)
                        .setTitle(R.string.about_title)
                        .setMessage(R.string.about_text)
                        .setPositiveButton(R.string.ok_label,
                                new DialogInterface.OnClickListener(){
                                    public void onClick(
                                            DialogInterface dialoginterface, int i){
                                        //empty
                                    }
                                })
                        .show();
                return true;
            case R.id.exit_settings:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onPause() {
        //stopMusic();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        // Terminate extra threads here
        stopMusic();
   //     if(mAdView!=null)
   //         mAdView.release();
        super.onDestroy();
    }

    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        getPrefs();
    }

    private void playMusic(int itemnumber, String itemname) {
        int resId = 0;
        if (tabName.equals("tabOff")) {
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
        } else if (tabName.equals("tabOn")) {
            PATH = null;
            String mp3 = "";

            switch (itemnumber) {
                case 0:
                    mp3 = "Prajnaparamita.mp3";
                    //	PATH="http://sites.google.com/site/androidbuddhavoice/Prajnaparamita.mp3?attredirects=0&d=1";
                    //	PATH="http://dl.dropbox.com/u/128583/Prajnaparamita.mp3";
                    break;
                case 1:
                    mp3 = "XJ0502.mp3";
                    //	PATH="http://sites.google.com/site/androidbuddhavoice/XJ0502.mp3?attredirects=0&d=1";
                    //	PATH="http://dl.dropbox.com/u/128583/XJ0502.mp3";
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
            if (onlinePreference.equals("google"))
                PATH = "http://sites.google.com/site/androidbuddhavoice/" + mp3 + "?attredirects=0&d=1";
            if (onlinePreference.equals("dropbox"))
                PATH = "http://dl.dropbox.com/u/128583/" + mp3;
        }
        stopMusic();
        startMusic(resId);
    }

    private void startMusic(int resourceID) {
        if (tabName.equals("tabOn")) {
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

    //back to main menu
    private void goIntent() {
        Intent intent;
        intent = this.getIntent();
        // back to main menu
        VoiceListener.this.setResult(RESULT_OK, intent);
        // end this activity
        VoiceListener.this.finish();
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

/*
    @Override
    public void adClicked() {

    }

    @Override
    public void adClosed(Ad ad, boolean b) {

    }

    @Override
    public void adLoadSucceeded(Ad ad) {

    }

    @Override
    public void adShown(Ad ad, boolean b) {

    }

    @Override
    public void noAdFound() {

    }
    */
}

