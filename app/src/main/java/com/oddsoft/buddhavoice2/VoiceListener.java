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
import android.graphics.drawable.Drawable;
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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.google.android.gms.ads.*;

public class VoiceListener extends Activity
        implements OnClickListener {
    private static final String TAG = "VoiceListener";
    private MediaPlayer mp;
    private TextView statusTextView;
    private TextView songnameTextView;
    private TextView songcontentTextView;
    private TextView songinfoTextView;
    private Button stopButton;
    private Button pauseButton;
    private String[] songInfo;
    private String[] songContent;
    private String tabName;
    private String PATH;
    private String onlinePreference;
    private ProgressDialog dialog = null;

    private RelativeLayout layout;
    private AdView adView;


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
        ADView();

        //show song name
        songnameTextView.setText(itemName);
        songinfoTextView.setText(songInfo[itemNumber]);
        songcontentTextView.setText(songContent[itemNumber]);

        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        stopButton.setOnClickListener(this);
        pauseButton.setOnClickListener(this);

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
        stopButton = (Button) findViewById(R.id.stop_button);
        pauseButton = (Button) findViewById(R.id.pause_button);

        songInfo = getResources().getStringArray(R.array.itemSongsInfo);
        songContent = getResources().getStringArray(R.array.itemSongsCont);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.stop_button:
                stopMusic();
                //back to main
                goIntent();
                break;
            case R.id.pause_button:
                if (mp.isPlaying())
                    pauseMusic();
                else
                    resumeMusic();
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

                int maxVolume = 50;
                int currVolume = 10;
                float log=(float)(Math.log(maxVolume-currVolume)/Math.log(maxVolume));

                mp = MediaPlayer.create(getBaseContext(), resourceID);
                mp.setLooping(true);
                mp.setVolume(1-log, 1-log);
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
            pauseButton.setText(R.string.start_title);
            Drawable icon= this.getResources().getDrawable( R.drawable.media_play);
            pauseButton.setCompoundDrawablesWithIntrinsicBounds( icon, null, null, null );
        }
    }

    private void resumeMusic() {
        if (mp != null) {
            mp.setLooping(true);
            mp.start();
            pauseButton.setText(R.string.pause_title);
            Drawable icon= this.getResources().getDrawable( R.drawable.media_pause);
            pauseButton.setCompoundDrawablesWithIntrinsicBounds( icon, null, null, null );
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

        AdRequest adRequest = new AdRequest.Builder().build();
        //AdRequest adRequest = new AdRequest.Builder()
        //        .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)       // 仿真器
        //        .addTestDevice("7710C21FF2537758BF3F80963477D68E") // 我的 Galaxy Nexus 測試手機
        //        .build();
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

