package com.oddsoft.buddhavoice2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;

import com.oddsoft.buddhavoice2.adapter.RecyclerAdapter;
import com.oddsoft.buddhavoice2.adapter.RecyclerItemClickListener;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by andycheng on 2015/10/27.
 */
public class TabFragment extends Fragment {

    private static final String TAG = "TabFragment";
    private static final String ARG_POSITION = "position";

    private int position;
    private String[] song = null;
    private String tabName;

    public static TabFragment newInstance(int position) {
        TabFragment f = new TabFragment();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        position = getArguments().getInt(ARG_POSITION);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

        FrameLayout fl = new FrameLayout(getActivity());
        fl.setLayoutParams(params);

        RecyclerView v = new RecyclerView(getActivity());

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        v.setLayoutManager(llm);
        v.setHasFixedSize(true);


        switch (position) {
            case 0:
                song = getResources().getStringArray(R.array.itemSongs);
                tabName = "TAB1";
                break;
            case 1:
                song = getResources().getStringArray(R.array.itemSongs1);
                tabName = "TAB2";
                break;
        }

        RecyclerAdapter adapter = new RecyclerAdapter(getActivity(), song);
        v.setAdapter(adapter);

        v.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        goIntent(tabName, position, song[position]);
                    }
                })
        );

        fl.addView(v);
        return fl;
    }


    private void goIntent(String tabName, int itemnumber, String itemname) {
        Intent intent = new Intent();
        intent.setClass(getActivity(), VoiceListener.class);

        Bundle bundle = new Bundle();
        bundle.putString("TAB", tabName);
        bundle.putString("KEY_NBR", Integer.toString(itemnumber));
        bundle.putString("KEY_NAME", itemname);
        intent.putExtras(bundle);

        Map<String,String> parameters = new HashMap<String,String>();
        parameters.put("Song", itemname);

        startActivityForResult(intent, 0);
    }
}