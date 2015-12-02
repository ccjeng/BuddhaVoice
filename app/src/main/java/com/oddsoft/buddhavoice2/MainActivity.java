package com.oddsoft.buddhavoice2;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.LibsBuilder;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.oddsoft.buddhavoice2.app.Analytics;
import com.oddsoft.buddhavoice2.app.BuddhaVoice;
import com.oddsoft.buddhavoice2.app.DrawerItem;
import com.oddsoft.buddhavoice2.app.DrawerItemAdapter;
import com.oddsoft.buddhavoice2.app.SlidingTabLayout;
import com.oddsoft.buddhavoice2.app.ViewPagerAdapter;

import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;


public class MainActivity extends ActionBarActivity {
    private static final String TAG = "BuddhaVoice";

    @Bind(R.id.drw_layout)
    DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    @Bind(R.id.llv_left_drawer)
    LinearLayout mLlvDrawerContent;

    @Bind(R.id.lsv_drawer_menu)
    ListView mLsvDrawerMenu;

    // 記錄被選擇的選單指標用
    private int mCurrentMenuItemPosition = -1;

    private Analytics ga;

    private ActionBar actionbar;

    @Bind(R.id.tool_bar)
    Toolbar toolbar;

    @Bind(R.id.pager)
    ViewPager pager;

    private ViewPagerAdapter adapter;

    @Bind(R.id.tabs)
    SlidingTabLayout tabs;
    int Numboftabs =2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        ga = new Analytics();
        ga.trackerPage(this);

        initActionBar();
        initDrawer();
        initDrawerList();

        CharSequence Titles[]={getString(R.string.tab1),getString(R.string.tab2)};

        // Creating The ViewPagerAdapter and Passing Fragment Manager, Titles fot the Tabs and Number Of Tabs.
        adapter =  new ViewPagerAdapter(getSupportFragmentManager(),Titles,Numboftabs);

        // Assigning ViewPager View and setting the adapter
        pager.setAdapter(adapter);

        // Assiging the Sliding Tab Layout View
        tabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width


        // Setting Custom Color for the Scroll bar indicator of the Tab View
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.tabsScrollColor);
            }
        });

        // Setting the ViewPager For the SlidingTabsLayout
        tabs.setViewPager(pager);

    }

    private void getPrefs() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String langPreference = prefs.getString("lang", "NA");
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

        //appLoc = new Locale(lang);
        if (!langPreference.equals("NA")) {
            Locale.setDefault(appLoc);
            Configuration appConfig = new Configuration();
            appConfig.locale = appLoc;
            getBaseContext().getResources().updateConfiguration(appConfig,
                    getBaseContext().getResources().getDisplayMetrics());
        }
    }

    private void initActionBar(){
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(new IconicsDrawable(this)
                .icon(GoogleMaterial.Icon.gmd_menu)
                .color(Color.WHITE)
                .actionBar());

        actionbar = getSupportActionBar();
    }

    private void initDrawer() {
        // 設定 Drawer 的影子
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,    // 讓 Drawer Toggle 知道母體介面是誰
                R.drawable.ic_drawer, // Drawer 的 Icon
                R.string.app_name, // Drawer 被打開時的描述
                R.string.app_name // Drawer 被關閉時的描述
        ) {
            //被打開後要做的事情
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                actionbar.setTitle(R.string.app_name);
            }

            //被關上後要做的事情
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                actionbar.setTitle(R.string.app_name);
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);

    }

    private void initDrawerList() {

        String[] drawer_menu = this.getResources().getStringArray(R.array.drawer_menu);

        DrawerItem[] drawerItem = new DrawerItem[2];

        drawerItem[0] = new DrawerItem(new IconicsDrawable(this)
                .icon(GoogleMaterial.Icon.gmd_build)
                .color(Color.GRAY)
                .sizeDp(24),
                drawer_menu[0]);
        drawerItem[1] = new DrawerItem(new IconicsDrawable(this)
                .icon(GoogleMaterial.Icon.gmd_info)
                .color(Color.GRAY)
                .sizeDp(24),
                drawer_menu[1]);

        DrawerItemAdapter adapter = new DrawerItemAdapter(this, R.layout.drawer_item, drawerItem);
        mLsvDrawerMenu.setAdapter(adapter);
        // 當清單選項的子物件被點擊時要做的動作
        mLsvDrawerMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                selectMenuItem(position);
            }
        });

    }


    private void selectMenuItem(int position) {
        mCurrentMenuItemPosition = position;

        switch (mCurrentMenuItemPosition) {
            case 0:
                startActivity(new Intent(this, Prefs.class));
                break;
            case 1:
                //show about message
                new LibsBuilder()
                        //provide a style (optional) (LIGHT, DARK, LIGHT_DARK_TOOLBAR)
                        .withActivityStyle(Libs.ActivityStyle.LIGHT_DARK_TOOLBAR)
                        .withAboutIconShown(true)
                        .withAboutVersionShown(true)
                        .withAboutAppName(getString(R.string.app_name))
                        .withActivityTitle(getString(R.string.about_title))
                        .withAboutDescription(getString(R.string.license))
                                //start the activity
                        .start(this);

                break;
        }

        // 將選單的子物件設定為被選擇的狀態
        mLsvDrawerMenu.setItemChecked(position, true);

        // 關掉 Drawer
        mDrawerLayout.closeDrawer(mLlvDrawerContent);
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
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

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
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
    protected void onStart() {
        super.onStart();
        getPrefs();
        if (!BuddhaVoice.APPDEBUG)
            GoogleAnalytics.getInstance(this).reportActivityStart(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (!BuddhaVoice.APPDEBUG)
            GoogleAnalytics.getInstance(this).reportActivityStop(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        getPrefs();
    }

}
