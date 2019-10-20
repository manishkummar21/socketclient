package com.vogo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;
import com.vogo.databinding.ActivityProgressBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class ProgressActivity extends AppCompatActivity {

    ActivityProgressBinding databinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databinding = DataBindingUtil.setContentView(this, R.layout.activity_progress);
        databinding.pulsator.start();
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver, new IntentFilter("com.socketupdate"));
        SocketImpl.getInstance().ConnecttoSocket();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        databinding.pulsator.stop();
        DisConnectToSocket();
    }

    private void DisConnectToSocket() {
        if (App.getInstance().getSocket() != null)
            App.getInstance().getSocket().off("onNewData");

    }

    public Emitter.Listener onUpdate = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            System.out.println("Data is Reached or Not");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if (args.length > 0) {
                        ObjectMapper mapper = new ObjectMapper();
                        try {
                            Result result = mapper.readValue(String.valueOf(args[0]), Result.class);
                            System.out.println("Data received from server is " + args[0]);
                            setResult(result.isHelmetdata() ? RESULT_OK : RESULT_CANCELED);
                            finish();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }
            });
        }
    };

    BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getStringExtra("status").equalsIgnoreCase(Socket.EVENT_CONNECT)) {
                App.getInstance().getSocket().on("onNewData", onUpdate);
                App.getInstance().getSocket().emit("subscribetovehicle", "KA02HN3532");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("vehicleID", "KA02HN3532");
                            jsonObject.put("helmetdata", true);
                            App.getInstance().getSocket().emit("onVehicleMonitor", jsonObject);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, 3000);
            } else if (intent.getStringExtra("status").equalsIgnoreCase(Socket.EVENT_DISCONNECT)) {
                DisConnectToSocket();
            }
        }
    };
}
