package com.smoner.rpc.demo1.http.post;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class HttpUtils {
    public HttpUtils() {
    }
    /*
     * params 填写的URL的参数 encode 字节编码
     */
    public static String sendPostMessage(Map<String, String> params,
                                         String encode,String path) {
        // 表示服务器端的url
        //String PATH = "http://192.168.0.100:8080/myhttp/servlet/LoginAction";
        URL url2 = null;
        try {
            url2 = new URL(path);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        StringBuffer stringBuffer = new StringBuffer();

        if (params != null && !params.isEmpty()) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                try {
                    stringBuffer
                            .append(entry.getKey())
                            .append("=")
                            .append(URLEncoder.encode(entry.getValue(), encode))
                            .append("&");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            // 删掉最后一个 & 字符
            stringBuffer.deleteCharAt(stringBuffer.length() - 1);
            System.out.println("-->>" + stringBuffer.toString());

            try {
                HttpURLConnection httpURLConnection = (HttpURLConnection) url2.openConnection();
                httpURLConnection.setConnectTimeout(3000);
                httpURLConnection.setDoInput(true);// 从服务器获取数据
                httpURLConnection.setDoOutput(true);// 向服务器写入数据

                // 获得上传信息的字节大小及长度
                byte[] mydata = stringBuffer.toString().getBytes();
                // 设置请求体的类型
                httpURLConnection.setRequestProperty("Content-Type",
                        "application/x-www-form-urlencoded");
                httpURLConnection.setRequestProperty("Content-Lenth",
                        String.valueOf(mydata.length));

                // 获得输出流，向服务器输出数据
                OutputStream outputStream = (OutputStream) httpURLConnection.getOutputStream();
                outputStream.write(mydata);

                // 获得服务器响应的结果和状态码
                int responseCode = httpURLConnection.getResponseCode();
                if (responseCode == 200) {
                    // 获得输入流，从服务器端获得数据
                    InputStream inputStream = (InputStream) httpURLConnection
                            .getInputStream();
                    return (changeInputStream(inputStream, encode));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    /*
     * // 把从输入流InputStream按指定编码格式encode变成字符串String
     */
    public static String changeInputStream(InputStream inputStream,
                                           String encode) {
        // ByteArrayOutputStream 一般叫做内存流
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int len = 0;
        String result = "";
        if (inputStream != null) {
            try {
                while ((len = inputStream.read(data)) != -1) {
                    byteArrayOutputStream.write(data, 0, len);
                }
                result = new String(byteArrayOutputStream.toByteArray(), encode);

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return result;
    }

    public static void main(String[] args) {
        String path = "http://127.0.0.1:18080/webrpctest/servlet/LoginAction";
        Map<String, String> params = new HashMap<String, String>();
        params.put("username", "admin");
        params.put("password", "123");
        String result = sendPostMessage(params, "utf-8",path);
        System.out.println("-result->>" + result);
    }
}
