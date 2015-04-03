package com.example.jiangliu.gps;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by jiangliu on 15-4-2.
 */
public class Fragment_left extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view=inflater.inflate(R.layout.fragmentl_left,null);
        TextView textView=(TextView)view.findViewById(R.id.weather);
        Bundle bundle=this.getArguments();
        Log.i("温度",bundle.getString("temp"));
        textView.setText(bundle.getString("temp"));
        return view;
//        return inflater.inflate(R.layout.fragmentl_left, container, false);
    }

    @Override
    public void onPause(){
        super.onPause();
    }
}
