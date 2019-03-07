package com.squad.betakua.tap_neko.azure;

public interface AppServicesListener {
    void onWriteComplete(String response);
    void oReadComplete(String response);
}
