package com.oddsoft.buddhavoice2;

import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

public class Prefs extends PreferenceActivity{
	private Toolbar mActionBar;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
		mActionBar.setTitle(getTitle());
		mActionBar.setTitleTextColor(Color.WHITE);
	}

	@Override
	public void setContentView(int layoutResID) {
		ViewGroup contentView = (ViewGroup) LayoutInflater.from(this).inflate(
				R.layout.preference, new LinearLayout(this), false);

		mActionBar = (Toolbar) contentView.findViewById(R.id.tool_bar);
		mActionBar.setNavigationIcon(new IconicsDrawable(this)
				.icon(GoogleMaterial.Icon.gmd_arrow_back)
				.color(Color.WHITE)
				.actionBar());

		mActionBar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		ViewGroup contentWrapper = (ViewGroup) contentView.findViewById(R.id.content_wrapper);
		LayoutInflater.from(this).inflate(layoutResID, contentWrapper, true);

		getWindow().setContentView(contentView);
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
			case android.R.id.home:
				finish();
				return true;
		}

		return super.onOptionsItemSelected(item);
	}



}
