package com.example.messenger;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.messenger.Database.DataContext;
import com.example.messenger.Receivers.WifiDirectBroadcastReceiver;
import com.example.messenger.Services.PreferenceManager;
import com.example.messenger.model.User;

import java.util.ArrayList;
import java.util.List;

public class Register extends Activity {
    private PreferenceManager preferenceManager;
    EditText fullName, email, password, rt_password;
    Button register;
    Button discoverPeer;
    DataContext DB;

    public static final String TAG = "MainActivity";
    public static final String DEFAULT_CHAT_NAME = "";
    private static final int PERMISSIONS_REQUEST_CODE_ACCESS_FINE_LOCATION = 1001;


    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private WifiDirectBroadcastReceiver mReceiver;
    private IntentFilter mIntentFilter;

    private ImageView goToSettings;
    private TextView goToSettingsText;

    private LinearLayout registerView;
    private LinearLayout connectView;

    List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();
    String[] deviceNameArray;
    WifiP2pDevice[] deviceArray;


    public LinearLayout getRegisterView() {
        return registerView;
    }
    public LinearLayout getConnectView() {
        return connectView;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE_ACCESS_FINE_LOCATION:
                if  (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Log.e(TAG, "Fine location permission is not granted!");
                    finish();
                }
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        fullName = (EditText) findViewById(R.id.fullname);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.pass);
        rt_password = (EditText) findViewById(R.id.rt_password);
        register = (Button) findViewById(R.id.require_btn);
        discoverPeer = (Button) findViewById(R.id.button);
        preferenceManager = new PreferenceManager(getApplicationContext());
        DB = new DataContext(this);

        init();

        //Button Go to Settings
        goToSettings = findViewById(R.id.goToSettings);
        goToSettings();

        registerView = findViewById(R.id.mainRegister);
        connectView = findViewById(R.id.connectMain);

        //Go to Settings text
        goToSettingsText = findViewById(R.id.textGoToSettings);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    Register.PERMISSIONS_REQUEST_CODE_ACCESS_FINE_LOCATION);
        }

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = fullName.getText().toString();
                String em = email.getText().toString();
                String pass = password.getText().toString();
                String repass = rt_password.getText().toString();

                if (name.equals("") || em.equals("") || pass.equals("") || repass.equals("")) {
                    Toast.makeText(Register.this, "Please do not leave any fields bank", Toast.LENGTH_SHORT).show();
                } else {
                    if (pass.equals(repass)) {
                        if (!DB.checkEmailFormat(em)) {
                            if (DB.isValidPassword(pass)) {
                                Boolean insert = DB.insertDataLogin(em, name, pass, null);
                                if (insert) {
                                    Toast.makeText(Register.this, "Make account successfully", Toast.LENGTH_SHORT).show();
                                    preferenceManager.putBoolean("isLogin", false);
                                    preferenceManager.putString("userEmail", em);
                                    preferenceManager.putString("userName", name);
                                    Intent intent = new Intent(getApplicationContext(), login.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(Register.this, "Make account failed", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(Register.this, "Password is very low", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(Register.this, "User already exists or email format wrong", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(Register.this, "Passwords not matching", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        discoverPeer.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View view) {
                mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        Log.e(TAG, "Discovery process succeeded");
                    }

                    @Override
                    public void onFailure(int reason) {
                        Log.e(TAG, "Discovery process failed");
                    }
                });
            }
        });
    }


    public WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList peerList) {
            if(!peerList.getDeviceList().equals(peers)) {
                peers.clear();
                peers.addAll(peerList.getDeviceList());
                deviceNameArray = new String[peerList.getDeviceList().size()];
                deviceArray = new WifiP2pDevice[peerList.getDeviceList().size()];
                int index = 0;

                for(WifiP2pDevice device:peerList.getDeviceList()) {
                    deviceNameArray[index] = device.deviceName;
                    deviceArray[index]=device;
                    index++;
                }
                Toast.makeText(getApplicationContext(), (CharSequence) deviceArray[1], Toast.LENGTH_SHORT).show();
            }
            if(peers.size() == 0) {
                Toast.makeText(getApplicationContext(), "No device found", Toast.LENGTH_SHORT).show();
                return;
            }
        }
    };

    @SuppressLint("MissingPermission")
    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(mReceiver, mIntentFilter);

        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.e(TAG, "Discovery process succeeded");
            }

            @Override
            public void onFailure(int reason) {
                Log.e(TAG, "Discovery process failed");
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }

    public void init(){
        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        if (mManager == null) {
            Log.e(TAG, "Cannot get Wi-Fi Direct system service.");
        }
        mChannel = mManager.initialize(this, getMainLooper(), null);
        if (mChannel == null) {
            Log.e(TAG, "Cannot initialize Wi-Fi Direct.");
        }
        mReceiver = new WifiDirectBroadcastReceiver(mManager, mChannel, this);

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
    }

    public void goToSettings(){
        goToSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                //Open Wifi settings
                startActivityForResult(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS), 0);
            }
        });
    }


    public void onClick(View v) {
        preferenceManager.putBoolean("isLogin", false);
        Intent intent = new Intent(getApplicationContext(), login.class);
        startActivity(intent);
    }

}
