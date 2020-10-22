package com.sinnefa.simplesocialdistancing;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import androidx.annotation.Nullable;


public class MyScanService extends Service {

    private BluetoothAdapter mBluetoothAdapter;
    private SQLiteDatabase devicesdatabase;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        devicesdatabase = openOrCreateDatabase("SSD devices history",MODE_PRIVATE,null);
        devicesdatabase.execSQL("CREATE TABLE IF NOT EXISTS devices(name VARCHAR, date DATETIME);");

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mReceiver, filter);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if(mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.startDiscovery();
        }

        return super.onStartCommand(intent, flags, startId);
    }


    private static double round (double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                devicesdatabase.execSQL("INSERT INTO devices VALUES('"+device.getAddress()+"',datetime());");
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                mBluetoothAdapter.startDiscovery();
            }
        }
    };
}