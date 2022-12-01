package com.example.messenger;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.example.messenger.Services.PreferenceManager;
import com.example.messenger.adapter.ContactAdapter;
import com.example.messenger.model.Contact;
import com.example.messenger.model.User;
import com.google.android.material.imageview.ShapeableImageView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class ChatsFragment extends Fragment {
    //Attributes
    private EditText editTextSearch;
    private ShapeableImageView shapeableImageViewAvatar;
    private ContactAdapter customContactAdapter;
    private List<Contact> contacts;
    private PreferenceManager shp;
    private ArrayList<User> listUsers = new ArrayList<User>();

    private boolean isLoading;
    private boolean isLastPage;
    private final int totalPage = 5;
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chats, container, false);


        shapeableImageViewAvatar = view.findViewById(R.id.avatar);

        shapeableImageViewAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent settingIntent = new Intent(getActivity(), SettingsActivity.class);

                startActivity(settingIntent);
            }
        });

        //Group of online users
        List<Contact> onlineContacts = getListOnlineContact();

        //Handle search box
        editTextSearch =  view.findViewById(R.id.search_input);
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
//        editTextSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View view, boolean b) {
//                Intent searchIntent = new Intent(getActivity(), SearchActivity.class);
//                startActivity(searchIntent);
//                getActivity().finish();
//            }
//        });

        //Render online users
        ViewGroup scrollViewOnlineUsers = view.findViewById(R.id.view_group);
        for (int i = 0; i < onlineContacts.size(); i++) {
            final View singleFrame = getLayoutInflater().inflate(R.layout.frame_online_contact, null);
            final Contact currentContact = onlineContacts.get(i);
            final User UserSelected = listUsers.get(i);
            singleFrame.setId(i);

            ShapeableImageView iVAvatarOnlineUsers = singleFrame.findViewById(R.id.avatar);
            TextView tVCaptionOnlineUser = singleFrame.findViewById(R.id.caption);

            //Set data
            if(loadUserDetails() == null) {
                iVAvatarOnlineUsers.setImageResource(R.drawable.ic_launcher_background);
            } else {
                iVAvatarOnlineUsers.setImageBitmap(loadUserDetails());
            }
            tVCaptionOnlineUser.setText(currentContact.getUsername());
            scrollViewOnlineUsers.addView(singleFrame);

            singleFrame.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), ChatActivity.class);
                    //Pass data from ChatsFragment to Chat Activity
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("contact", currentContact);
                    bundle.putSerializable("DBReceiver", (Serializable) UserSelected);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    getActivity().finish();
                }});
        }

        //Render contacts
        //Attribute for contacts
        RecyclerView recyclerViewContacts = view.findViewById(R.id.rcv_contacts);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerViewContacts.setLayoutManager(linearLayoutManager);
        customContactAdapter = new ContactAdapter(getActivity());

        recyclerViewContacts.setAdapter(customContactAdapter);
        setFirstData();

        recyclerViewContacts.addOnScrollListener(new PaginationScrollListener(linearLayoutManager) {
            @Override
            public void loadMoreItems() {
//                isLoading = true;
//                currentPage += 1;
//                loadNextPage();
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }
        });
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
        }, 4000);

    }

    private List<Contact> getListOnlineContact() {
        List<Contact> list = new ArrayList<>();
        for(int i = 0; i< listUsers.size(); i++) {
            list.add(new Contact(listUsers.get(i).getName(), listUsers.get(i).getImage(), "Quan Nguyen", "Hello world"));
        }
        return list;
    }

    private Bitmap loadUserDetails() {
        if(shp.getString("imageUser") != null) {
            byte[] bytes = Base64.decode(shp.getString("imageUser"), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            return bitmap;
        }
        return null;
    }
}