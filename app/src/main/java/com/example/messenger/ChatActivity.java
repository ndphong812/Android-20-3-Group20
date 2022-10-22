package com.example.messenger;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.messenger.model.Contact;

public class ChatActivity extends Activity {

    Intent intent;
    Bundle bundle;
    ImageButton imageButtonBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        intent = getIntent();
        bundle = intent.getExtras();
        Contact currentContact = (Contact)bundle.getSerializable("contact");

        imageButtonBack = (ImageButton) findViewById(R.id.back_btn);

        imageButtonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent backMainScreenIntent = new Intent(getApplication(), MainActivity.class);

                startActivity(backMainScreenIntent);

            }
        });
    }
}