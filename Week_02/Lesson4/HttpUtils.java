package com.mousewheel.study;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;


public class HttpUtils {

    public static void main(String[] args){
        try {
            System.out.println(doGet("http://localhost:8801/"));
        }catch (Exception ex){
            System.out.println(ex);
        }
    }

    public static String doGet(String url) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                return response.body().string();
            } else {
                throw new IOException("Unexpected code " + response);
            }
        } catch (IOException ex) {
            throw ex;
        }
    }
}
