package com.oddsoft.buddhavoice2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.oddsoft.buddhavoice2.app.Analytics;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by hp1 on 21-01-2015.
 */
public class Tab2 extends Fragment {

    private static final String TAG = "Tab2";

    private ListView listView;
    private String[] song;
    private ProgressDialog dialog = null;
    private Analytics ga;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.tab2,container,false);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ga = new Analytics();
        ga.trackerPage(getActivity());

        listView = (ListView) getView().findViewById(R.id.ListView02);
        song = getResources().getStringArray(R.array.itemSongs1);

        listView.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, song));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                //dialog = ProgressDialog.show(MainActivity.this, "", getString(R.string.loading), true, true);
                new Thread() {
                    public void run() {
                        try {
                            goIntent(position, song[position]);
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            //dialog.dismiss();
                        }
                    }
                }.start();

            }
        });
        //listView.setSelection(0);

    }


    public void goIntent(int itemnumber, String itemname) {

        ga.trackEvent(getActivity(), "Listen", "Song", itemname.toString(), 0);

        Intent intent = new Intent();
        intent.setClass(getActivity(), VoiceListener.class);
        Bundle bundle = new Bundle();
        Log.d(TAG, "goIntent-itemnumber: " + Integer.toString(itemnumber));
        Log.d(TAG, "goIntent-itemname: " + itemname.toString());

        bundle.putString("KEY_NBR", Integer.toString(itemnumber));
        bundle.putString("KEY_NAME", itemname.toString());
        intent.putExtras(bundle);

        Map<String,String> parameters = new HashMap<String,String>();
        parameters.put("Song", itemname.toString());
        startActivityForResult(intent, 0);
    }

}