package com.example.messenger;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.messenger.databinding.ActivityMainBinding;


public class MainActivity extends FragmentActivity {

    ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        replaceFragment(new ChatsFragment());
        setContentView(binding.getRoot());

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.chats:
                    replaceFragment(new ChatsFragment());
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
        fragmentTransaction.replace(R.id.frameLayout,  fragment);
        fragmentTransaction.commit();
    }
}