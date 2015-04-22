package com.oddsoft.buddhavoice2;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabContentFactory;

public class BuddhaVoice extends TabActivity 
	implements OnTabChangeListener  {	
	private static final String TAG = "BuddhaVoice";
	private static final int PREF_ID = Menu.FIRST;
	private static final int ABOUT_ID = Menu.FIRST + 1; 
	private static final int EXIT_ID = Menu.FIRST + 2; 
	private ListView listView;
	private String[] song;
	private ProgressDialog dialog = null;
	TabHost tabHost;
	private String tabSourceIntent="tabOff";
	
	final Handler updateHandler = new Handler();
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.main);
        listView = (ListView) findViewById(R.id.ListView01);
        getTag();
    	song = getResources().getStringArray(R.array.itemSongs);
   	
    	listView.setAdapter(new SongListAdapter(this, R.layout.list_item, song));

    	listView.setOnItemClickListener(new OnItemClickListener() {
        	public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                dialog = ProgressDialog.show(BuddhaVoice.this, "", getString(R.string.loading), true, true);
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

    //	getPrefs();
   }
   
    @Override 
    public boolean onCreateOptionsMenu(Menu menu) { 
       super.onCreateOptionsMenu(menu); 
       menu.add(0, PREF_ID, 0, R.string.preference).setIcon(R.drawable.configuration); 
       menu.add(0, ABOUT_ID, 1, R.string.about_title).setIcon(R.drawable.help_browser); 
       menu.add(0, EXIT_ID, 2, R.string.exit_title).setIcon(R.drawable.process_stop); 
       return true;  
    }
    
    @Override 
    public boolean onOptionsItemSelected(MenuItem item) { 
        switch (item.getItemId()) { 
        	case PREF_ID:
        		startActivity(new Intent(BuddhaVoice.this, Prefs.class));
        		break;
            case ABOUT_ID:  
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
               	break; 
            case EXIT_ID:
            	finish();  
            	break; 
           } 
        return super.onOptionsItemSelected(item); 
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
    protected void onDestroy() {
       super.onDestroy();
    }
    
    protected void onStop() {
       	super.onStop();
        // Stop the tracker when it is no longer needed.
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

    private void goIntent(int itemnumber, String itemname) {
        Intent intent = new Intent();
        intent.setClass(BuddhaVoice.this, VoiceListener.class);
        Bundle bundle = new Bundle();
        Log.d(TAG, "goIntent-itemnumber: " + Integer.toString(itemnumber));
        Log.d(TAG, "goIntent-itemname: " + itemname.toString());
               
        bundle.putString("KEY_NBR", Integer.toString(itemnumber));
        bundle.putString("KEY_NAME", itemname.toString());
        bundle.putString("SourceTab", tabSourceIntent.toString());
        intent.putExtras(bundle);
        
        Map<String,String> parameters = new HashMap<String,String>();
		parameters.put("Song", itemname.toString());
        startActivityForResult(intent,0);
    }
    

	private void getTag() {
	    tabHost = getTabHost();
	    tabHost.setOnTabChangedListener(this);
	     
	    tabHost.addTab(tabHost.newTabSpec("tabOff") 
	    	.setIndicator(getResources().getText(R.string.tab_offline), getResources().getDrawable(R.drawable.offline)) 
	    	.setContent(new TabContentFactory() {
	    		public View createTabContent(String arg0) {
	    			return listView;
	    		}
	    	}));
	    tabHost.addTab(tabHost.newTabSpec("tabOn") 
	    	.setIndicator(getResources().getText(R.string.tab_online), getResources().getDrawable(R.drawable.online))
	   		.setContent(new TabContentFactory() {
	   			public View createTabContent(String arg0) {
	   				return listView;
	   			}
	   		}));
	}

	@Override
	public void onTabChanged(String tabName) {
		if(tabName.equals("tabOff")) {
			song = getResources().getStringArray(R.array.itemSongs);
		 }
		 else if (tabName.equals("tabOn")) {
			song = getResources().getStringArray(R.array.itemSongs1);
		 }
		tabSourceIntent = tabName;
    	listView.setAdapter(new SongListAdapter(this, R.layout.list_item, song));

    	listView.setOnItemClickListener(new OnItemClickListener() {
        	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        		goIntent(position, song[position]);
        	}
        });
    	listView.setSelection(0);
		
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


			
}