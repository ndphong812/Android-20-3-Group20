package com.example.messenger.Receivers;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.messenger.Register;

import java.net.InetAddress;

public class WifiDirectBroadcastReceiver extends BroadcastReceiver {
    public static final int IS_OWNER = 1;
    public static final int IS_CLIENT = 2;
    public static final String TAG = "Check wifi-direct";


    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;
    private Register mActivity;
    private InetAddress ownerAddr;
    private int isGroupeOwner;

    public WifiDirectBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel, Register activity) {
        this.manager = manager;
        this.channel = channel;
        this.mActivity = activity;
    }

    public int isGroupeOwner() { return isGroupeOwner; }
    public InetAddress getOwnerAddr() { return ownerAddr; }
    public void setmManager(WifiP2pManager mManager) { this.manager = mManager; }
    public void setmChannel(WifiP2pManager.Channel mChannel) { this.channel = mChannel; }
    public void setmActivity(Activity mActivity) { this.mActivity = (Register) mActivity; }


    @SuppressLint("MissingPermission")
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            Log.v(TAG, "WIFI_P2P_STATE_CHANGED_ACTION");

            //check if Wifi P2P is supported
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if(state == WifiP2pManager.WIFI_P2P_STATE_ENABLED){
                Toast.makeText(mActivity, "Wifi P2P is supported by this device", Toast.LENGTH_SHORT).show();
            } else{
                Toast.makeText(mActivity, "Wifi P2P is not supported by this device", Toast.LENGTH_SHORT).show();
            }
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {

            if(manager!=null) {
                manager.requestPeers(channel,mActivity.peerListListener);
            }
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            Log.e(TAG, "connected");
            if(manager == null) {
                return;
            }
            NetworkInfo networkInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
            if(networkInfo.isConnected()){
                manager.requestConnectionInfo(channel, new WifiP2pManager.ConnectionInfoListener() {

                    @Override
                    public void onConnectionInfoAvailable(WifiP2pInfo info) {
                        ownerAddr= info.groupOwnerAddress;

                        if (info.groupFormed && info.isGroupOwner) {
                            isGroupeOwner = IS_OWNER;
                            connectToRegister();
                        }

                        else if (info.groupFormed) {
                            isGroupeOwner = IS_CLIENT;
                            connectToRegister();
                        }
                    }
                });
            }

        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            // do something
        }
    }

    private void connectToRegister() {
        if(mActivity.getClass() == Register.class) {
            ((Register)mActivity).getRegisterView().setVisibility(View.VISIBLE);
            ((Register)mActivity).getConnectView().setVisibility(View.GONE);
        }
    }
}
