package com.example.messenger;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import com.example.messenger.Database.DataContext;
import com.example.messenger.adapter.NewUserAdapter;
import com.example.messenger.model.Contact;
import com.example.messenger.model.User;

import java.util.ArrayList;

public class SearchActivity extends Activity {

    private ImageButton imageButtonBack;
    private ListView listViewResultSearch;
    private Button addFriend;
    private ArrayList<Contact> listResult;
    private ArrayList<User> listFriend;
    private Intent intent;
    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        imageButtonBack = (ImageButton) findViewById(R.id.back_btn);
        listViewResultSearch = (ListView) findViewById(R.id.result_search);
        addFriend = (Button) findViewById(R.id.addFriend);
        listViewResultSearch.setDivider(null);

        //search
        searchResult();

        NewUserAdapter customNewUserAdapter = new NewUserAdapter(this, R.layout.frame_new_user, listResult);
        listViewResultSearch.setAdapter(customNewUserAdapter);


        imageButtonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent backIntent = new Intent(getApplication(), MainActivity.class);
                startActivity(backIntent);
                finish();
            }
        });


        addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }

    public void searchResult() {
        listResult = new ArrayList<>();
        intent = getIntent();
        bundle = intent.getExtras();
        listFriend = (ArrayList<User>) bundle.getSerializable("ListFriend");
        for(int i = 0 ; i< listFriend.size(); i++) {
            listResult.add(new Contact(listFriend.get(i).getName(), R.drawable.ic_launcher_background, "Quan Nguyen", "Hello world"));
        }
    }
}