package com.example.jiangliu.gps;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by jiangliu on 15-3-28.
 */
public class HttpUtil {

    // 根据URL下载文件,前提是这个文件当中的内容是文本,函数的返回值就是文本当中的内容
    // 1.创建一个URL对象
    // 2.通过URL对象,创建一个HttpURLConnection对象
    // 3.得到InputStream
    // 4.从InputStream当中读取数据
    private static URL url;
    static BufferedReader buffer = null;

    public static String download(String urlStr) {
        StringBuffer sb = new StringBuffer();
        String line = null;
        int i = 0;
        try {
            url = new URL(urlStr);
            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
            buffer = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
            while ((line = buffer.readLine()) != null) {
                sb.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (buffer != null) {

                    buffer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (sb.charAt(i) != '{') {
            while (sb.charAt(++i) != '{') {
            }
            sb.delete(0, i);
            i = sb.length() - 1;
            while (sb.charAt(--i) != '}') {
            }
            sb.delete(i + 1, sb.length());
        }
        return sb.toString();
    }
}
