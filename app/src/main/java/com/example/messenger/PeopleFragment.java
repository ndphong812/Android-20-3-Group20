package com.example.messenger;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.messenger.Database.DataContext;
import com.example.messenger.Services.PreferenceManager;
import com.example.messenger.adapter.ContactAdapter;
import com.example.messenger.adapter.FriendAdapter;
import com.example.messenger.model.Contact;

import java.util.ArrayList;
import java.util.List;

public class PeopleFragment extends Fragment {
    private FriendAdapter customContactAdapter;
    private boolean isLoading;
    private boolean isLastPage;
    private final int totalPage = 2;
    private int currentPage = 1;
    private PreferenceManager shp;
    private List<Contact> contacts;

    public PeopleFragment() {
    }


    public static PeopleFragment newInstance(String param1, String param2) {
        PeopleFragment fragment = new PeopleFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        shp = new PreferenceManager(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_people, container, false);
        RecyclerView recyclerViewContacts = view.findViewById(R.id.friend);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerViewContacts.setLayoutManager(linearLayoutManager);
        customContactAdapter = new FriendAdapter(getActivity());

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
            list.add(new Contact("Friend " + i, shp.getString("imageUser")));
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
}