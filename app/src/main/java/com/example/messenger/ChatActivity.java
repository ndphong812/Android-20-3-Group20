package com.example.messenger;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.messenger.model.Contact;

public class ChatActivity extends AppCompatActivity {

    Intent intent;
    Bundle bundle;
    private TextView tvChatName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        intent = getIntent();
        bundle = intent.getExtras();
        Contact currentContact = (Contact)bundle.getSerializable("contact");

        tvChatName = (TextView) findViewById(R.id.chatName);
        tvChatName.setText(currentContact.getUsername());

    }
}