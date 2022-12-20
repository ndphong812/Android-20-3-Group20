package com.example.messenger;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.util.Log;

import com.example.messenger.Database.DataContext;
import com.example.messenger.Services.PreferenceManager;
import com.example.messenger.databinding.ActivityMainBinding;
import com.example.messenger.model.User;
import com.google.android.material.navigation.NavigationBarView;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class MainActivity extends FragmentActivity {

    ActivityMainBinding binding;
    ArrayList<User> listUsers;
    DataContext DB;
    ChatsFragment chatFragment = new ChatsFragment();
    private Intent intent;
    private Bundle bundle;
    private PreferenceManager shp;
    String userLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        replaceFragment(new ChatsFragment());
        setContentView(binding.getRoot());
        DB = new DataContext(this);
        shp =  new PreferenceManager(getApplicationContext());
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        chatFragment = ChatsFragment.newInstance("a", "b", "c");
        userLogin = shp.getString("userEmail");
        listUsers = DB.getListUsers();
        for(int i=0;i<listUsers.size();i++) {
            if(listUsers.get(i).getEmail().equals(userLogin)) {
                listUsers.remove(i);
                break;
            }
        }
        chatFragment.UsersData(listUsers);
        ft.replace(R.id.frame_layout, chatFragment);
        ft.commit();
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.chats:
                    FragmentTransaction ft2 = getSupportFragmentManager().beginTransaction();
                    chatFragment = ChatsFragment.newInstance("a", "b", "c");
                    userLogin = shp.getString("userEmail");
                    listUsers = DB.getListUsers();
                    for(int i=0;i<listUsers.size();i++) {
                        if(listUsers.get(i).getEmail().equals(userLogin)) {
                            listUsers.remove(i);
                            break;
                        }
                    }
                    chatFragment.UsersData(listUsers);
                    ft2.replace(R.id.frame_layout, chatFragment);
                    ft2.commit();
                    break;
                case R.id.calls:
                    replaceFragment(new CallsFragment());
                    break;
                case R.id.people:
                    replaceFragment(new PeopleFragment());
                    break;
                case R.id.notification:
                    replaceFragment(new NotificationFragment());
                    break;
            }

            return true;
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager =  getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout,  fragment);
        fragmentTransaction.commit();

    }
}