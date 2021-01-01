package com.dip.squadsecurity.ui;


import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.dip.squadsecurity.CheckConnectivity;
import com.dip.squadsecurity.ImageAdapter;
import com.dip.squadsecurity.R;
import com.google.android.material.tabs.TabLayout;


public class Home extends Fragment{
    TextView retry;
    LinearLayout layoutconn;
    ViewPager viewPager;
    Handler wait;
    Runnable delay;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_home, container, false);
        retry = v.findViewById(R.id.textViewRetry);
        layoutconn=v.findViewById(R.id.linearlayoutconnection);
        TabLayout TL=v.findViewById(R.id.tab_layout);
        viewPager=v.findViewById(R.id.viewPager);
        ImageAdapter adapterView = new ImageAdapter(getContext());
        viewPager.setAdapter(adapterView);
        TL.setupWithViewPager(viewPager,true);

       wait = new Handler();
        delay= new Runnable() {
            @Override
            public void run() {
                int i=viewPager.getCurrentItem();
                do {
                    viewPager.setCurrentItem(i++);
                }while (i<5);

            }
        };
        wait.postDelayed(delay, 2000);

        if (new CheckConnectivity().isNetworkAvailable(getContext())) {
            layoutconn.setVisibility(View.GONE);
        }

        retry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Reload current fragment
                    Fragment frg;
                    getFragmentManager().beginTransaction().detach(Home.this).attach(Home.this).commit();
                }
        });
        return v;
    }

}
