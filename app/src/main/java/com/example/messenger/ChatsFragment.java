package com.example.messenger;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.messenger.adapter.CustomContactAdapter;
import com.example.messenger.model.Contact;
import com.google.android.material.imageview.ShapeableImageView;


public class ChatsFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    //Attributes
    private ViewGroup scrollViewOnlineUsers;

    //Data here
    private String [] captionOnlineUsers = {"Nguyen Van A", "Nguyen Van B", "Nguyen Van C", "Nguyen Van D", "Nguyen Van B","Nguyen Van B","Nguyen Van B","Nguyen Van B","Nguyen Van B","Nguyen Van B"};
    private Integer [] thumbnailOnlineUsers = {R.drawable.ic_launcher_background, R.drawable.ic_launcher_background, R.drawable.ic_launcher_background, R.drawable.ic_launcher_background, R.drawable.ic_launcher_background, R.drawable.ic_launcher_background, R.drawable.ic_launcher_background, R.drawable.ic_launcher_background, R.drawable.ic_launcher_background, R.drawable.ic_launcher_background};

    private ListView listViewContacts;
    private Contact [] contacts;

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
        listViewContacts = view.findViewById(R.id.listViewContacts);

        //Render online users
        scrollViewOnlineUsers = (ViewGroup) view.findViewById(R.id.viewGroup);
        for (int i = 0; i < captionOnlineUsers.length; i++) {
            final View singleFrame = getLayoutInflater().inflate(R.layout.frame_icon_caption, null);
            singleFrame.setId(i);
            ShapeableImageView iVAvatarOnlineUsers = (ShapeableImageView) singleFrame.findViewById(R.id.avatar);
            TextView tVCaptionOnlineUser = (TextView) singleFrame.findViewById(R.id.caption);

            iVAvatarOnlineUsers.setImageResource(thumbnailOnlineUsers[i]);
            tVCaptionOnlineUser.setText(captionOnlineUsers[i]);
            scrollViewOnlineUsers.addView(singleFrame);

            singleFrame.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(view.getContext(), "Click item", Toast.LENGTH_SHORT).show();
                }});
        }

        //Render contacts
        contacts = new Contact[] {
                new Contact("Quan Nguyen", R.drawable.ic_launcher_background, "Quan Nguyen", "Hello world"),
                new Contact("Quan Nguyen", R.drawable.ic_launcher_background, "Quan Nguyen", "Hello world"),
                new Contact("Quan Nguyen", R.drawable.ic_launcher_background, "Quan Nguyen", "Hello world"),
                new Contact("Quan Nguyen", R.drawable.ic_launcher_background, "Quan Nguyen", "Hello world"),
                new Contact("Quan Nguyen", R.drawable.ic_launcher_background, "Quan Nguyen", "Hello world"),
                new Contact("Quan Nguyen", R.drawable.ic_launcher_background, "Quan Nguyen", "Hello world"),
        };


        CustomContactAdapter contactAdapter = new CustomContactAdapter(getActivity(), R.layout.frame_contact, contacts);
        listViewContacts.setAdapter(contactAdapter);

        listViewContacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getContext().getApplicationContext(), "Click contact", Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }
}