package com.squad.betakua.tap_neko;

import android.app.Application;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // AndroidAudioConverter.load(this, new ILoadCallback() {
        //     @Override
        //     public void onSuccess() {
        //         // Great!
        //     }
        //     @Override
        //     public void onFailure(Exception error) {
        //         // FFmpeg is not supported by device
        //     }
        // });
    }
}
