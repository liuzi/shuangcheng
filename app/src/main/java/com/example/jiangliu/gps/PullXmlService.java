package com.example.jiangliu.gps;

import android.app.Activity;
import android.content.res.XmlResourceParser;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;

/**
 * Created by jiangliu on 15-2-7.
 */
public class PullXmlService {

    //解析数据以输入流形式传入
    public String getWeathercode(Activity activity, String city)throws Throwable{
        String cityname="";
        String weathercode="";
        //创建解析工厂
        XmlResourceParser parser=activity.getResources().getXml(R.xml.weather_code);
        //产生第一个事件
        int eventType=parser.getEventType();
        while (eventType!=XmlPullParser.END_DOCUMENT&&weathercode==""){//文件结束停止解析
//            cityname=parser.getAttributeCount();
            switch (eventType){
                case XmlPullParser.START_TAG://标签开始判断
                    if("county".equals(parser.getName())&&city.equals(parser.getAttributeValue(1))){
                        Log.d("find", "city武汉");
                        weathercode=parser.getAttributeValue(2);
                    }
                    break;
                default:
                    break;
            }
            eventType=parser.next();
        }
        return weathercode;
    }
}
