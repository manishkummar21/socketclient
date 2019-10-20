package com.vogo;

import android.app.Application;

import com.github.nkzawa.engineio.client.transports.Polling;
import com.github.nkzawa.engineio.client.transports.WebSocket;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;

public class App extends Application {

    private static App mInstance;
    private Socket mSocket;

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;
        try {
            IO.Options opts = new IO.Options();
//            opts.forceNew = true;
//            opts.reconnection = true;
//            opts.secure = false;
            opts.transports = new String[]{WebSocket.NAME};
//            opts.timeout = (60 * 60 * 1000);
//            opts.path = "http://192.168.12.40:3000";
            mSocket = IO.socket("http://192.168.0.104:3000", opts);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public Socket getSocket() {
        return mSocket;
    }

    public static synchronized App getInstance() {
        return mInstance;
    }

}
