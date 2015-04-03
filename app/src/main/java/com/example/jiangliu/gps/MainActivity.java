package com.example.jiangliu.gps;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.BDNotifyListener;

import com.baidu.location.BDGeofence;
import com.baidu.location.BDLocationStatusCodes;
import com.baidu.location.GeofenceClient;
import com.baidu.location.GeofenceClient.OnAddBDGeofencesResultListener;
import com.baidu.location.GeofenceClient.OnGeofenceTriggerListener;
import com.baidu.location.GeofenceClient.OnRemoveBDGeofencesResultListener;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.XmlResourceParser;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;


import java.io.IOException;
import java.util.List;


public class MainActivity extends Activity {

    public LocationClient mLocationCLient=null;
    public MyLocationListener myLocationListener=new MyLocationListener();
    private TextView weatherTextView=null;
    private TextView textView=null;



    final static StringBuffer city=new StringBuffer(10);
    private String weathercode="";
//    private String citycode="";
    private String weather_api="http://weather.123.duba.net/static/weather_info/";


    public final static String RECI_COAST = "com.example.jiangliu.HttpUtil";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Baidu Client服务开启
        ClientonCreate();
        LocationClientOption option = new LocationClientOption();
        setLocOption(option);
        mLocationCLient.setLocOption(option);
        mLocationCLient.start();

        //获取manager
        FragmentManager fragmentManager=getFragmentManager();
        //创建事物
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        //创建leftFragment
        Fragment_left fragment_left=new Fragment_left();
        //创建Bundle存储数据传递给Fragment
        Bundle leftbundle=new Bundle();
        SharedPreferences sharedata=getSharedPreferences("data",0);
        setBundle(sharedata,leftbundle);

        fragment_left.setArguments(leftbundle);
        fragmentTransaction.add(R.id.relativeLayout1, fragment_left, "fragment_left");
        fragmentTransaction.add(R.id.relativeLayout2, new Fragment_right(), "fragment_right");
        fragmentTransaction.commit();


    }


    public void setBundle(SharedPreferences sharedata, Bundle bundle){
        bundle.putString("city",sharedata.getString("city",null));
        bundle.putString("temp",sharedata.getString("temp",null));
    }

    //申明LocationClient注册监听函数
    public void ClientonCreate(){
        mLocationCLient = new LocationClient(getApplicationContext());
        mLocationCLient.registerLocationListener(myLocationListener);
    }

    //设置参数
    public void setLocOption(LocationClientOption option){
        option.setOpenGps(true);
//        option.setScanSpan(2000);
        option.setAddrType("all");
        option.setCoorType("bgcj02");
        option.setProdName("GPS");
    }

    private void getCityCode(){
        PullXmlService pullXmlService=new PullXmlService();
        try{

            weathercode=pullXmlService.getWeathercode(this,city.toString());
//            textView.setText(weathercode);

            //注册广播
            IntentFilter filter = new IntentFilter(RECI_COAST);
            BroadcastReceiver myreceiver = new JsonReceiver();
            registerReceiver(myreceiver, filter);

            new Thread(runnable).start();


        }catch (Throwable e){
            //TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    Runnable runnable=new Runnable() {
        @Override
        public void run() {
            Intent intent = new Intent(RECI_COAST);
            //获取服务器返回的信息
//                    String result=HttpUtil.download("http://m.weather.com.cn/atad/101010100.html");
//            String result = HttpUtil.download("http://weather.123.duba.net/static/weather_info/101121301.html");
            String result = HttpUtil.download(weather_api+weathercode+".html");
            intent.putExtra("weatherinfo", result);
            //发送广播
            sendBroadcast(intent);
        }
    };


    public class JsonReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            try {

                JSONObject jsonobject = new JSONObject(
                        intent.getStringExtra("weatherinfo"));
                JSONObject jsoncity = new JSONObject(
                        jsonobject.getString("weatherinfo"));
                SharedPreferences.Editor sharedata=getSharedPreferences("data",0).edit();
                sharedata.putString("city",jsoncity.getString("city"));
                Log.d("城镇",jsoncity.getString("city"));
                sharedata.putString("temp",jsoncity.getString("temp1"));
                sharedata.commit();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    //实现BDLocationListener接口
    private class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            Log.d("返回",":"+location.getLocType());
            if(mLocationCLient!=null&&mLocationCLient.isStarted()){
                Toast.makeText(MainActivity.this,"正在定位",Toast.LENGTH_SHORT).show();
            }

            if (location == null || location.getLocType() != 161) {
                Toast.makeText(MainActivity.this, "定位失败,请检查你的网络设置", Toast.LENGTH_SHORT).show();
            } else {
                Log.i("location", "获取了地理位置");
                city.append(location.getCity());
                if(city.charAt(city.length()-1)=='市'){
                    city.deleteCharAt(city.length()-1);
                }
                getCityCode();
            }
        }
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
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
