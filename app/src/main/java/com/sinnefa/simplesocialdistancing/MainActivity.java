package com.sinnefa.simplesocialdistancing;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Date;
import androidx.core.app.ActivityCompat;
import java.util.ArrayList;
import java.util.List;
import java.util.Calendar;
import android.app.ActivityManager.RunningServiceInfo;


public class MainActivity extends Activity {

    private BluetoothAdapter mBluetoothAdapter;
    private SQLiteDatabase devicesdatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                1);

        devicesdatabase = openOrCreateDatabase("SSD devices history",MODE_PRIVATE,null);
        devicesdatabase.execSQL("CREATE TABLE IF NOT EXISTS devices(name VARCHAR, date DATETIME);");

        //listView = (ListView) findViewById(R.id.lvDevices);

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mReceiver, filter);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(isMyServiceRunning(MyScanService.class)) {
            if(mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()) {
                mBluetoothAdapter.startDiscovery();
                ((TextView)findViewById(R.id.status)).setText("Discovering...");
            }
            else {
                ((TextView)findViewById(R.id.status)).setText("Bluetooth disabled or not available\nTurn it on and start the app again");
            }
        }
        else {
            ((TextView)findViewById(R.id.status)).setText("Scanning inactive");
        }

        final Intent serviceIntent= new Intent(this, MyScanService.class);
        Button startButton = (Button) findViewById(R.id.startScan);
        startButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()) {
                    mBluetoothAdapter.startDiscovery();
                    startService(serviceIntent);
                    updateCound();
                    ((TextView)findViewById(R.id.status)).setText("Discovering...");
                }
                else {
                    ((TextView)findViewById(R.id.status)).setText("Bluetooth disabled or not available\nTurn it on and start the app again");
                }
            }
        });
        Button stopButton = (Button) findViewById(R.id.stopScan);
        stopButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBluetoothAdapter.cancelDiscovery();
                stopService(serviceIntent);
                ((TextView)findViewById(R.id.status)).setText("Scanning inactive");
            }
        });
        updateCound();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBluetoothAdapter != null) {
            mBluetoothAdapter.cancelDiscovery();
        }
        unregisterReceiver(mReceiver);
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private static double round (double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                //BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //devicesdatabase.execSQL("INSERT INTO devices VALUES('"+device.getAddress()+"',datetime());");

                updateCound();


            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                mBluetoothAdapter.startDiscovery();
            }
        }
    };

    private void updateCound() {

        Cursor c = devicesdatabase.rawQuery("SELECT distinct(name) FROM devices where date >=datetime('now', '-1 minutes')",null);
        ((TextView)findViewById(R.id.now)).setText(c.getCount()+"");


        // Updating last hour
        c = devicesdatabase.rawQuery("SELECT distinct(name) FROM devices where date >=datetime('now', '-1 Hour')",null);
        ((TextView)findViewById(R.id.lasth)).setText(c.getCount()+"");

        // Updating today

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date(); // Or where ever you get it from
        String start = sdf.format(date)+" 00:00:00";
        String end = sdf.format(date)+" 23:59:59";
        System.out.println(start+" "+end);

        c = devicesdatabase.rawQuery("SELECT distinct(name) FROM devices where date >=datetime('"+start+"') and date <=datetime('"+end+"') ",null);
        System.out.println(c.getCount());
        ((TextView)findViewById(R.id.today)).setText(round(c.getCount()/16.0)+"");

        c = devicesdatabase.rawQuery("SELECT distinct(name) FROM devices where date >=datetime('"+start+"') and date <=datetime('"+end+"') ",null);
        ((TextView)findViewById(R.id.todaytot)).setText(c.getCount()+"");


        // Updating yesterday

        sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, -1);
        String yesterday = sdf.format(cal.getTime());

        start = yesterday+" 00:00:00";
        end = yesterday+" 23:59:59";
        System.out.println(start+" "+end);

        c = devicesdatabase.rawQuery("SELECT distinct(name) FROM devices where date >=datetime('"+start+"') and date <=datetime('"+end+"') ",null);
        ((TextView)findViewById(R.id.lastd)).setText(round(c.getCount()/16.0)+"");

        c = devicesdatabase.rawQuery("SELECT distinct(name) FROM devices where date >=datetime('"+start+"') and date <=datetime('"+end+"') ",null);
        ((TextView)findViewById(R.id.lastdtot)).setText(c.getCount()+"");


        // Updating last 7 days

        cal.setTime(date);
        cal.add(Calendar.DATE, -7);
        String sevendays = sdf.format(cal.getTime());
        start = sevendays+" 00:00:00";

        sdf = new SimpleDateFormat("yyyy-MM-dd");
        date = new Date(); // Or where ever you get it from
        end = sdf.format(date)+" 23:59:59";

        System.out.println(start+" "+end);

        c = devicesdatabase.rawQuery("SELECT distinct(name) FROM devices where date >=datetime('"+start+"') and date <=datetime('"+end+"') ",null);
        ((TextView)findViewById(R.id.lasts)).setText(round(c.getCount()/(16.0*7))+"");

        c = devicesdatabase.rawQuery("SELECT distinct(name) FROM devices where date >=datetime('"+start+"') and date <=datetime('"+end+"') ",null);
        ((TextView)findViewById(R.id.laststot)).setText(c.getCount()+"");


        // Updating last 7 days

        cal.setTime(date);
        cal.add(Calendar.DATE, -15);
        String fifteendays = sdf.format(cal.getTime());
        start = fifteendays+" 00:00:00";

        sdf = new SimpleDateFormat("yyyy-MM-dd");
        date = new Date(); // Or where ever you get it from
        end = sdf.format(date)+" 23:59:59";

        System.out.println(start+" "+end);

        c = devicesdatabase.rawQuery("SELECT distinct(name) FROM devices where date >=datetime('"+start+"') and date <=datetime('"+end+"') ",null);
        ((TextView)findViewById(R.id.lastf)).setText(round(c.getCount()/(16.0*15))+"");

        c = devicesdatabase.rawQuery("SELECT distinct(name) FROM devices where date >=datetime('"+start+"') and date <=datetime('"+end+"') ",null);
        ((TextView)findViewById(R.id.lastftot)).setText(c.getCount()+"");
    }
}