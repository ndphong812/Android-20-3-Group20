package com.example.messenger;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.messenger.adapter.CustomContactAdapter;
import com.example.messenger.model.Contact;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;
import java.util.List;


public class ChatsFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    //Attributes
    private EditText editTextSearch;
    private ViewGroup scrollViewOnlineUsers;

    private List<Contact> onlineContacts; //Group of online users

    /**
     * Attribute for contacts
     */
    private RecyclerView recyclerViewContacts;
    private CustomContactAdapter customContactAdapter;
    private List<Contact> contacts;

    private boolean isLoading;
    private boolean isLastPage;
    private int totalPage = 5;
    private int currentPage = 1;


    public ChatsFragment() {
        // Required empty public constructor
    }

    public static ChatsFragment newInstance(String param1, String param2) {
        ChatsFragment fragment = new ChatsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chats, container, false);

        onlineContacts = getListOnlineContact();

        //Handle search box
        editTextSearch = (EditText) view.findViewById(R.id.searchInput);
        editTextSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean isFocus) {
                Intent searchIntent = new Intent(getActivity(), SearchActivity.class);
                editTextSearch.clearFocus();
                startActivity(searchIntent);
            }
        });

        //Render online users
        scrollViewOnlineUsers = (ViewGroup) view.findViewById(R.id.viewGroup);
        for (int i = 0; i < onlineContacts.size(); i++) {
            final View singleFrame = getLayoutInflater().inflate(R.layout.frame_online_contact, null);
            final Contact currentContact = onlineContacts.get(i);
            singleFrame.setId(i);

            ShapeableImageView iVAvatarOnlineUsers = (ShapeableImageView) singleFrame.findViewById(R.id.avatar);
            TextView tVCaptionOnlineUser = (TextView) singleFrame.findViewById(R.id.caption);

            //Set data
            iVAvatarOnlineUsers.setImageResource(currentContact.getAvatarPath());
            tVCaptionOnlineUser.setText(currentContact.getUsername());
            scrollViewOnlineUsers.addView(singleFrame);

            singleFrame.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), ChatActivity.class);
                    //Pass data from ChatsFragment to Chat Activity
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("contact", currentContact);
                    intent.putExtras(bundle);

                    startActivity(intent);
                }});
        }

        //Render contacts
        recyclerViewContacts = view.findViewById(R.id.rcvContacts);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerViewContacts.setLayoutManager(linearLayoutManager);
        customContactAdapter = new CustomContactAdapter(getActivity());

        recyclerViewContacts.setAdapter(customContactAdapter);
        setFirstData();
        recyclerViewContacts.addOnScrollListener(new PaginationScrollListener(linearLayoutManager) {
            @Override
            public void loadMoreItems() {
                isLoading = true;
                currentPage += 1;
                loadNextPage();
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
     * Function for change paginations
     */
    private void setFirstData() {
        contacts = getListContact();
        customContactAdapter.setData(contacts);

        if(currentPage < totalPage) {
            customContactAdapter.addFooterLoading();
        }else {
            isLastPage = true;
        }
    }
    private List<Contact> getListContact() {
        List<Contact> list = new ArrayList<Contact>();
        for(int i = 1; i<= 10; i++) {
            list.add(new Contact("Quan Nguyen " + i, R.drawable.ic_launcher_background, "Quan Nguyen", "Hello world"));
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
        List<Contact> list = new ArrayList<Contact>();
        for(int i = 1; i<= 10; i++) {
            list.add(new Contact("Quan Nguyen " + i, R.drawable.ic_launcher_background, "Quan Nguyen", "Hello world"));
        }
        return list;
    }
}