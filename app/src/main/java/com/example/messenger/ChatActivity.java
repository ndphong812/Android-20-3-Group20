package com.example.messenger;


import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.messenger.adapter.ChatAdapter;
import com.example.messenger.databinding.ActivityChatBinding;
import com.example.messenger.model.ChatItem;
import com.example.messenger.model.Contact;

import java.util.ArrayList;
import java.util.List;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;

public class ChatActivity extends Activity {

    private ActivityChatBinding binding;
    private Intent intent;
    private Bundle bundle;
    private ImageButton imageButtonBack;
    private LinearLayout linearLayoutActions;
    private ImageButton imageButtonShowMore;
    private ImageButton imageButtonSendMessage;
    private ImageView imageButtonEmoji;

    private Contact currentContact;
    private List<ChatItem> listChat;
    private ChatAdapter customChatAdapter;
    private EmojiconEditText editTextInputChat;
    private EmojIconActions emojIconActions;
    private ConstraintLayout activityChatLayout;

    private ChatActivity _this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        _this = this;
        getChatContact();

        // Init attributes
        imageButtonBack = (ImageButton) findViewById(R.id.back_btn);
        linearLayoutActions = (LinearLayout) findViewById(R.id.layout_actions);
        editTextInputChat = (EmojiconEditText) findViewById(R.id.chat_input);
        imageButtonShowMore = (ImageButton) findViewById(R.id.show_more_btn);
        imageButtonSendMessage = (ImageButton) findViewById(R.id.send_btn);

        activityChatLayout = (ConstraintLayout) findViewById(R.id.activity_main_layout);
        imageButtonEmoji = (ImageView) findViewById(R.id.emoji_btn);
        RecyclerView recyclerViewMessages = (RecyclerView) findViewById(R.id.rcv_messages);
        emojIconActions = new EmojIconActions(this, binding.getRoot(), imageButtonEmoji, editTextInputChat);

        //Handle events
        renderMessages(recyclerViewMessages);
        emojIconActions.ShowEmojicon();

        imageButtonBack.setOnClickListener(view -> {
            Intent backMainScreenIntent = new Intent(getApplication(), MainActivity.class);
            startActivity(backMainScreenIntent, ActivityOptions.makeSceneTransitionAnimation(_this).toBundle());
        });

        editTextInputChat.setOnClickListener(view -> {
            linearLayoutActions.setVisibility(View.GONE);
            imageButtonShowMore.setVisibility(View.VISIBLE);
        });

        editTextInputChat.setOnFocusChangeListener((view, isFocus) -> {
            if(isFocus) {
                linearLayoutActions.setVisibility(View.GONE);
                imageButtonShowMore.setVisibility(View.VISIBLE);
                recyclerViewMessages.smoothScrollToPosition(listChat.size() - 1);
            }
        });


        editTextInputChat.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                linearLayoutActions.setVisibility(View.GONE);
                imageButtonShowMore.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        imageButtonShowMore.setOnClickListener(view -> {
            linearLayoutActions.setVisibility(View.VISIBLE);
            imageButtonShowMore.setVisibility(View.GONE);
        });
        final MediaPlayer mediaPlayerSendMsg = MediaPlayer.create(this, R.raw.click);

        imageButtonSendMessage.setOnClickListener(view -> {
            String msg = editTextInputChat.getText().toString();
            mediaPlayerSendMsg.setVolume(0, 0.2F);
            mediaPlayerSendMsg.start();

            if(!msg.equals("")) {
                //Store in database and send real-time to the other
                customChatAdapter.addChatItem(new ChatItem("Quan Nguyen ", "XXX", msg, true));

                //smooth to bottom
                recyclerViewMessages.smoothScrollToPosition(listChat.size() - 1);
                editTextInputChat.setText("");
                editTextInputChat.requestFocus();
            }else{
                Toast.makeText(ChatActivity.this, "Bạn không thể gửi tin nhắn trống", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void renderMessages(RecyclerView recyclerView) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        listChat = getListMessage();
        customChatAdapter = new ChatAdapter(this, listChat);
        recyclerView.setAdapter(customChatAdapter);
        recyclerView.smoothScrollToPosition(listChat.size() - 1);
    }

    private void getChatContact() {
        intent = getIntent();
        bundle = intent.getExtras();
        currentContact = (Contact)bundle.getSerializable("contact");
    }
    private List<ChatItem> getListMessage() {
        List<ChatItem> list = new ArrayList<>();
        for(int i = 1; i<= 10; i++) {
            if(i % 2 == 0) {
                list.add(new ChatItem("Quan Nguyen ", "XXX", "Hello", true));
            }else{
                list.add(new ChatItem("Quan Nguyen ", "XXX", "Hello", false));
            }
        }
        return list;
    }
}