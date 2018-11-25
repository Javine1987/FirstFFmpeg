package com.example.javine.firstffmpeg;

public class JniHelper {
    static {
        System.loadLibrary("native-lib");
    }

    public native static int decodeVideo(String inputUrl, String outUrl);
}
