package com.oddsoft.buddhavoice2;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class MainActivity extends Activity {
    private static final String TAG = "BuddhaVoice";
    private ListView listView;
    private String[] song;
    private ProgressDialog dialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getActionBar().setDisplayHomeAsUpEnabled(false);

        listView = (ListView) findViewById(R.id.ListView01);

        song = getResources().getStringArray(R.array.itemSongs);

        //listView.setAdapter(new SongListAdapter(this, R.layout.list_item, song));
        listView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1 , song));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                dialog = ProgressDialog.show(MainActivity.this, "", getString(R.string.loading), true, true);
                new Thread() {
                    public void run() {
                        try {
                            goIntent(position, song[position]);
                        } catch(Exception e) {
                            e.printStackTrace();
                        } finally {
                            dialog.dismiss();
                        }
                    }
                }.start();

            }
        });
        listView.setSelection(0);
    }

    private void goIntent(int itemnumber, String itemname) {
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, VoiceListener.class);
        Bundle bundle = new Bundle();
        Log.d(TAG, "goIntent-itemnumber: " + Integer.toString(itemnumber));
        Log.d(TAG, "goIntent-itemname: " + itemname.toString());

        bundle.putString("KEY_NBR", Integer.toString(itemnumber));
        bundle.putString("KEY_NAME", itemname.toString());
       // bundle.putString("SourceTab", tabSourceIntent.toString());
        intent.putExtras(bundle);

        Map<String,String> parameters = new HashMap<String,String>();
        parameters.put("Song", itemname.toString());
        startActivityForResult(intent,0);
    }

    private void getPrefs() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String langPreference = prefs.getString("lang", "");
        String lang = null;
        Locale appLoc;
        if (langPreference.equals("")) {
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

        //appLoc = new Locale(lang);
        Locale.setDefault(appLoc);
        Configuration appConfig = new Configuration();
        appConfig.locale = appLoc;
        getBaseContext().getResources().updateConfiguration(appConfig,
                getBaseContext().getResources().getDisplayMetrics());
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


    /*get activity intent back from VoiceListener*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        switch (resultCode) {
            case RESULT_OK:
                //blank
                break;
            default:
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        getPrefs();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (dialog != null)
            dialog.dismiss();
    }

}
