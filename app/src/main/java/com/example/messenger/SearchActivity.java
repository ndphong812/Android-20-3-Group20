package com.example.messenger;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.annotation.NonNull;

import com.example.messenger.Services.PreferenceManager;
import com.example.messenger.adapter.NewUserAdapter;
import com.example.messenger.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SearchActivity extends Activity {
    //Reference DB
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://messenger-50d65-default-rtdb.firebaseio.com/");
    private PreferenceManager preferenceManager;

    ImageButton imageButtonBack;
    ImageButton imageButtonClear;
    EditText editTextSearch;

    User currentUser;
    ListView listViewResultSearch;
    ArrayList<User> listResult = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        //Binding variable
        imageButtonBack =  findViewById(R.id.back_btn);
        imageButtonClear = findViewById(R.id.clear_btn);
        editTextSearch =  findViewById(R.id.search_input);

        preferenceManager = new PreferenceManager(getApplicationContext());
        listViewResultSearch =  findViewById(R.id.result_search);
        listViewResultSearch.setDivider(null);

        databaseReference.child("User").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listResult.clear();
                String currentUserID = preferenceManager.getString("userID");
                //Get current User
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);
                    if(user != null && currentUserID.equals(user.id)) {
                        currentUser = user;
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(editTextSearch.getText().length() > 0) {
                    imageButtonClear.setVisibility(View.VISIBLE);
                }else{
                    imageButtonClear.setVisibility(View.INVISIBLE);
                }
                listResult.clear();
            }
            @Override
            public void afterTextChanged(Editable editable) {
                String keyword = editable.toString();
                if(keyword.length() > 0) {
                    databaseReference.child("User").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            //Get result search from firebase
                            listResult.clear();
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                User user = dataSnapshot.getValue(User.class);
                                if (user != null && !currentUser.getID().equals(user.id)) {
                                    if (user.getName().contains(keyword)) {
                                        listResult.add(user);
                                    }
                                }
                            }
                            Log.d("Count", listResult.size() + "");
                            NewUserAdapter customNewUserAdapter = new NewUserAdapter(SearchActivity.this, R.layout.frame_new_user, listResult, currentUser);
                            listViewResultSearch.setAdapter(customNewUserAdapter);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                }
            }
        });

        imageButtonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent backIntent = new Intent(getApplication(), MainActivity.class);
                startActivity(backIntent);
                finish();
                listResult.clear();
            }
        });

        imageButtonClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editTextSearch.setText("");
                editTextSearch.requestFocus();
                listResult.clear();
            }
        });
    }
}