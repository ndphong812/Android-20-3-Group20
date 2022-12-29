package com.example.messenger;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.messenger.Services.LoadImageFromURL;
import com.example.messenger.Services.PreferenceManager;
import com.example.messenger.adapter.ContactAdapter;
import com.example.messenger.model.Contact;
import com.example.messenger.model.User;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;


public class ChatsFragment extends Fragment {
    //Database
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://messenger-50d65-default-rtdb.firebaseio.com/");
    private PreferenceManager preferenceManager;

    //Attributes
    EditText editTextSearch;
    ShapeableImageView shapeableImageViewAvatar;
    ContactAdapter customContactAdapter;
    PreferenceManager shp;

    //List users
    List<User> onlineUsers = new ArrayList<>();
    List<Contact> contacts = new ArrayList<>();
    private User currentUser;

    public ChatsFragment() {
        // Required empty public constructor
    }

    public static ChatsFragment newInstance(String param1, String param2, String k) {
        ChatsFragment fragment = new ChatsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        shp = new PreferenceManager(getContext());
        preferenceManager = new PreferenceManager(getContext());
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chats, container, false);

        //Binding view
        shapeableImageViewAvatar = view.findViewById(R.id.Mainavatar);
        editTextSearch =  view.findViewById(R.id.search_input);
        ViewGroup scrollViewOnlineUsers = view.findViewById(R.id.view_group);

        //Handle click avatar to get setting
        shapeableImageViewAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent settingIntent = new Intent(getActivity(), SettingsActivity.class);
                startActivity(settingIntent);
            }
        });

        //Handle search box
        editTextSearch.setFocusableInTouchMode(false);
        editTextSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent searchIntent = new Intent(getActivity(), SearchActivity.class);
                startActivity(searchIntent);
            }
        });

        //Render online users
        readUser(new FirebaseCallback() {
            @Override
            public void onCallback(List<User> list) {
                if(isAdded()) {
                    for (int i = 0; i < onlineUsers.size(); i++) {
                        final View singleFrame = getLayoutInflater().inflate(R.layout.frame_online_contact, null);
                        final User currentUser = onlineUsers.get(i);
                        singleFrame.setId(i);

                        ShapeableImageView avatarOnlineUser = singleFrame.findViewById(R.id.avatar);
                        TextView captionOnlineUser = singleFrame.findViewById(R.id.caption);

                        LoadImageFromURL loadImageFromURL = new LoadImageFromURL(avatarOnlineUser);
                        loadImageFromURL.execute(currentUser.image);

                        captionOnlineUser.setText(currentUser.getName());
                        scrollViewOnlineUsers.addView(singleFrame);
                    }
                }
            }
        });

        //Render current friends
        RecyclerView recyclerViewContacts = view.findViewById(R.id.rcv_contacts);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerViewContacts.setLayoutManager(linearLayoutManager);

        databaseReference.child("User").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);
                    if(user != null ) {
                        if(currentUser.getFriends().contains(user.id)) {
                            contacts.add(new Contact(user.id, user.getName(), user.getImage(), "", ""));
                        }
                    }
                }

                customContactAdapter = new ContactAdapter(getActivity());
                customContactAdapter.setData(contacts);
                recyclerViewContacts.setAdapter(customContactAdapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
        return view;
    }

    //2 function for handling asynchronous when call API to firebase
    private void readUser(FirebaseCallback firebaseCallback) {
        databaseReference.child("User").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String currentUserID = preferenceManager.getString("userID");
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);
                    if(user.isLogined) {
                        if(!currentUserID.equals(user.id)) {
                            onlineUsers.add(user);
                        }else{
                            currentUser = user;
                        }
                    }
                }

                firebaseCallback.onCallback(onlineUsers);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private interface FirebaseCallback {
        void onCallback(List<User> list);
    }
}