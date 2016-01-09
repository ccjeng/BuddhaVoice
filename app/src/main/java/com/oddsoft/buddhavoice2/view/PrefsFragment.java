package com.oddsoft.buddhavoice2.view;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.oddsoft.buddhavoice2.R;

/**
 * Created by andycheng on 2016/1/9.
 */
public class PrefsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
    }
}
