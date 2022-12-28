package com.example.messenger;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
    private EditText editTextSearch;
    private ShapeableImageView shapeableImageViewAvatar;
    private ContactAdapter customContactAdapter;
    private PreferenceManager shp;
    private ArrayList<User> listUsers = new ArrayList<User>();

    //List users
    private List<User> onlineUsers = new ArrayList<>();
    private List<Contact> contacts;


    //Variable for loading
    private boolean isLoading;
    private boolean isLastPage;
    private final int totalPage = 2;
    private int currentPage = 1;

    public ChatsFragment() {
        // Required empty public constructor
    }

    public static ChatsFragment newInstance(String param1, String param2, String k) {
        ChatsFragment fragment = new ChatsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public void UsersData(ArrayList<User> Users) {
        this.listUsers = Users;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        shp = new PreferenceManager(getContext());
        preferenceManager = new PreferenceManager(getContext());
    }

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
        editTextSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent searchIntent = new Intent(getActivity(), SearchActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("ListFriend", (Serializable) listUsers);
                searchIntent.putExtras(bundle);
                startActivity(searchIntent);
                getActivity().finish();
            }
        });

        editTextSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                Intent searchIntent = new Intent(getActivity(), SearchActivity.class);
                startActivity(searchIntent);
                getActivity().finish();
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

        //Render contacts Attribute for contacts
//        RecyclerView recyclerViewContacts = view.findViewById(R.id.rcv_contacts);
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
//        recyclerViewContacts.setLayoutManager(linearLayoutManager);
//        customContactAdapter = new ContactAdapter(getActivity());
//
//        recyclerViewContacts.setAdapter(customContactAdapter);
//        setFirstData();
//        recyclerViewContacts.addOnScrollListener(new PaginationScrollListener(linearLayoutManager) {
//            @Override
//            public void loadMoreItems() {
//                isLoading = true;
//                currentPage += 1;
//                loadNextPage();
//            }
//
//            @Override
//            public boolean isLoading() {
//                return isLoading;
//            }
//
//            @Override
//            public boolean isLastPage() {
//                return isLastPage;
//            }
//        });
//

        return view;
    }

    /**
     * Function for change pagination
     */

    private void setFirstData() {
        contacts = getListContact();
        customContactAdapter.setData(contacts);

//        if(currentPage < totalPage) {
//            customContactAdapter.addFooterLoading();
//        }else {
//            isLastPage = true;
//        }
    }
    private List<Contact> getListContact() {
        List<Contact> list = new ArrayList<>();
        for(int i = 0; i< listUsers.size(); i++) {
            list.add(new Contact(listUsers.get(i).getName(), listUsers.get(i).getImage(), "Quan Nguyen", "Hello world"));
        }
        return list;
    }
    private void loadNextPage() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                List<Contact> list = getListContact();
                customContactAdapter.removeFooterLoading();
                contacts.addAll(list);
                customContactAdapter.notifyDataSetChanged();

                isLoading = false;
                if(currentPage < totalPage) {
                    customContactAdapter.addFooterLoading();
                }else {
                    isLastPage = true;
                }
            }
        }, 3000);
    }

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