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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends FragmentActivity {

    ActivityMainBinding binding;
    ArrayList<User> listUsers;
    DataContext DB;
    ChatsFragment chatFragment = new ChatsFragment();
    private Intent intent;
    private Bundle bundle;
    private PreferenceManager shp;
    private DatabaseReference databaseReference;
    String userLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        replaceFragment(new ChatsFragment());
        setContentView(binding.getRoot());
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("User");

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

    public ArrayList<HashMap<String, Object>> recArrayList(DataSnapshot snapshot){
        ArrayList<HashMap<String, Object>> list = new ArrayList<>();
        if (snapshot == null){
            return list;
        }
        // This is awesome! You don't have to know the data structure of the database.
        Object fieldsObj = new Object();
        HashMap fldObj;

        for (DataSnapshot shot : snapshot.getChildren()){
            try{
                fldObj = (HashMap)shot.getValue(fieldsObj.getClass());
            }catch (Exception ex){
                // My custom error handler. See 'ErrorHandler' in Gist
//                ErrorHandler.logError(ex);
                continue;
            }
            // Include the primary key of this Firebase data record. Named it 'recKeyID'
            fldObj.put("recKeyID", shot.getKey());
            list.add(fldObj);
        }
        return list;
    }
}