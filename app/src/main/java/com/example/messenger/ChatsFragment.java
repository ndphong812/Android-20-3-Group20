package com.example.messenger;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.messenger.adapter.ContactAdapter;
import com.example.messenger.model.Contact;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;
import java.util.List;


public class ChatsFragment extends Fragment {
    //Attributes
    private EditText editTextSearch;

    private ContactAdapter customContactAdapter;
    private List<Contact> contacts;

    private boolean isLoading;
    private boolean isLastPage;
    private final int totalPage = 2;
    private int currentPage = 1;


    public ChatsFragment() {
        // Required empty public constructor
    }

    public static ChatsFragment newInstance(String param1, String param2) {
        ChatsFragment fragment = new ChatsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chats, container, false);
        //Group of online users
        List<Contact> onlineContacts = getListOnlineContact();

        //Handle search box
        editTextSearch =  view.findViewById(R.id.search_input);
        editTextSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean isFocus) {
                Intent searchIntent = new Intent(getActivity(), SearchActivity.class);
                editTextSearch.clearFocus();
                startActivity(searchIntent);
            }
        });

        //Render online users
        ViewGroup scrollViewOnlineUsers = view.findViewById(R.id.view_group);
        for (int i = 0; i < onlineContacts.size(); i++) {
            final View singleFrame = getLayoutInflater().inflate(R.layout.frame_online_contact, null);
            final Contact currentContact = onlineContacts.get(i);
            singleFrame.setId(i);

            ShapeableImageView iVAvatarOnlineUsers = singleFrame.findViewById(R.id.avatar);
            TextView tVCaptionOnlineUser = singleFrame.findViewById(R.id.caption);

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
     * Function for change pagination
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
        List<Contact> list = new ArrayList<>();
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
        }, 3000);

    }

    private List<Contact> getListOnlineContact() {
        List<Contact> list = new ArrayList<>();
        for(int i = 1; i<= 10; i++) {
            list.add(new Contact("Quan Nguyen " + i, R.drawable.ic_launcher_background, "Quan Nguyen", "Hello world"));
        }
        return list;
    }
}