package com.vogo;

import android.content.Intent;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;

public class SocketImpl {

    private static volatile SocketImpl sSocketInstance;


    private SocketImpl() {
        //Prevent form the reflection api.
        if (sSocketInstance != null)
            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");

    }

    public static SocketImpl getInstance() {
        //Double check locking pattern
        if (sSocketInstance == null) { //Check for the first time

            synchronized (SocketImpl.class) {   //Check for the second time.
                //if there is no instance available... create new one
                if (sSocketInstance == null) sSocketInstance = new SocketImpl();
            }
        }

        return sSocketInstance;
    }

    public void ConnecttoSocket(){

        if(App.getInstance().getSocket().connected())
            LocalBroadcastManager.getInstance(App.getInstance()).sendBroadcast(new Intent("com.socketupdate").putExtra("status", Socket.EVENT_CONNECT));
        else {
            App.getInstance().getSocket().connect().on(Socket.EVENT_CONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.d("SocketIO","Connected");
                    LocalBroadcastManager.getInstance(App.getInstance()).sendBroadcast(new Intent("com.socketupdate").putExtra("status", Socket.EVENT_CONNECT));
                }
            }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.d("SocketIO","Socket Disconnected");
                    LocalBroadcastManager.getInstance(App.getInstance()).sendBroadcast(new Intent("com.socketupdate").putExtra("status", Socket.EVENT_DISCONNECT));
                }
            }).on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    LocalBroadcastManager.getInstance(App.getInstance()).sendBroadcast(new Intent("com.socketupdate").putExtra("status", Socket.EVENT_CONNECT_ERROR));
                }
            });
        }
    }






}