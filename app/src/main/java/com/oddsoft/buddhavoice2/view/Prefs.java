package com.oddsoft.buddhavoice2.view;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.oddsoft.buddhavoice2.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class Prefs extends AppCompatActivity {

	@Bind(R.id.toolbar)
	Toolbar toolbar;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.preference);
		ButterKnife.bind(this);


		setSupportActionBar(toolbar);
		if (getSupportActionBar() != null) {
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		}

		toolbar.setNavigationIcon(R.drawable.ic_keyboard_backspace_white_24px);
		toolbar.setTitleTextColor(Color.WHITE);

		FragmentManager manager = getFragmentManager();
		FragmentTransaction transaction = manager.beginTransaction();
		PrefsFragment myPreference = new PrefsFragment(); //宣告剛剛做好的PreferenceFragment
		transaction.replace(R.id.content_wrapper, myPreference); //將content內容取代為myPreference
		transaction.commit(); //送出交易
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
