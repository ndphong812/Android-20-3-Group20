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

import com.example.messenger.DeviceDetailFragment;
import com.example.messenger.DeviceListFragment;
import com.example.messenger.R;
import com.example.messenger.Register;

import java.net.InetAddress;

public class WifiDirectBroadcastReceiver extends BroadcastReceiver {
    public static final int IS_OWNER = 1;
    public static final int IS_CLIENT = 2;

    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;
    private Register activity;
    private InetAddress ownerAddr;
    private int isGroupeOwner;

    private WifiDirectBroadcastReceiver(){
        super();
    }


    public int isGroupeOwner() { return isGroupeOwner; }
    public InetAddress getOwnerAddr() { return ownerAddr; }
    public void setmManager(WifiP2pManager mManager) { this.manager = mManager; }
    public void setmChannel(WifiP2pManager.Channel mChannel) { this.channel = mChannel; }
    public void setmActivity(Activity mActivity) { this.activity = (Register) mActivity; }

    private static WifiDirectBroadcastReceiver instance;

    public static WifiDirectBroadcastReceiver createInstance(){
        if(instance == null){
            instance = new WifiDirectBroadcastReceiver();
        }
        return instance;
    }

    /**
     * @param manager WifiP2pManager system service
     * @param channel Wifi p2p channel
     * @param activity activity associated with the receiver
     */
    public WifiDirectBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel,
                                       Register activity) {
        super();
        this.manager = manager;
        this.channel = channel;
        this.activity = activity;
    }

    /*
     * (non-Javadoc)
     * @see android.content.BroadcastReceiver#onReceive(android.content.Context,
     * android.content.Intent)
     */
    @SuppressLint("MissingPermission")
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {

            // UI update to indicate wifi p2p status.
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                // Wifi Direct mode is enabled
                activity.setIsWifiP2pEnabled(true);
            } else {
                activity.setIsWifiP2pEnabled(false);
                activity.resetData();

            }
            Log.d(Register.TAG, "P2P state changed - " + state);
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {

            // request available peers from the wifi p2p manager. This is an
            // asynchronous call and the calling activity is notified with a
            // callback on PeerListListener.onPeersAvailable()
            if (manager != null) {
                manager.requestPeers(channel, (WifiP2pManager.PeerListListener) activity.getFragmentManager()
                        .findFragmentById(R.id.frag_list));
            }
            Log.d(Register.TAG, "P2P peers changed");
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {

            if (manager == null) {
                return;
            }

            NetworkInfo networkInfo = (NetworkInfo) intent
                    .getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

            if (networkInfo.isConnected()) {
                // we are connected with the other device, request connection
                // info to find group owner IP

                DeviceDetailFragment fragment = (DeviceDetailFragment) activity
                        .getFragmentManager().findFragmentById(R.id.frag_detail);
                manager.requestConnectionInfo(channel, fragment);
                manager.requestConnectionInfo(channel, new WifiP2pManager.ConnectionInfoListener() {
                    @Override
                    public void onConnectionInfoAvailable(WifiP2pInfo info) {
                        ownerAddr = info.groupOwnerAddress;

                        if(info.groupFormed && info.isGroupOwner) {
                            isGroupeOwner = IS_OWNER;
                            connectToRegister();
                        } else if(info.groupFormed) {
                            isGroupeOwner = IS_CLIENT;
                            connectToRegister();
                        }
                    }
                });

            } else {
                // It's a disconnect
                activity.resetData();
            }
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            DeviceListFragment fragment = (DeviceListFragment) activity.getFragmentManager()
                    .findFragmentById(R.id.frag_list);
            fragment.updateThisDevice((WifiP2pDevice) intent.getParcelableExtra(
                    WifiP2pManager.EXTRA_WIFI_P2P_DEVICE));

        }
    }

    private void connectToRegister() {
        Log.e("Class name", activity.getClass().toString());
        if(activity.getClass() == Register.class) {
            (activity).getRegisterView().setVisibility(View.VISIBLE);
            (activity).getConnectView().setVisibility(View.GONE);
        }
    }
}
