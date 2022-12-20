package com.example.messenger;


import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ShareActionProvider;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.messenger.Database.DataContext;
import com.example.messenger.Services.PreferenceManager;
import com.example.messenger.adapter.ChatAdapter;
import com.example.messenger.databinding.ActivityChatBinding;
import com.example.messenger.model.Contact;
import com.example.messenger.model.Message;
import com.example.messenger.model.User;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;

public class ChatActivity extends AppCompatActivity {

    private ActivityChatBinding binding;

    private Intent intent;
    private Bundle bundle;
    private TextView chatName;
    private ImageButton imageButtonBack;
    private LinearLayout linearLayoutActions;
    private ImageButton imageButtonShowMore;
    private ImageButton imageButtonSendMessage;
    private ImageButton imageButtonSendImage;
    private ImageButton imageButtonSendCamera;
    private ImageView imageButtonEmoji;
    private ImageView imgPreview;

    private Contact currentContact;
    private User receiverUser;
    private List<Message> listChat;
    private ChatAdapter customChatAdapter;
    private EmojiconEditText editTextInputChat;
    private EmojIconActions emojIconActions;
    private ConstraintLayout activityChatLayout;
    private PreferenceManager shp;
    Uri fileUri;
    public static final int MY_RESULT_LOAD_IMAGE = 7172;
    public static final int MY_CAMERA_REQUEST_CODE = 7171;
    String senderEmail;
    DataContext DB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        DB = new DataContext(this);
        shp = new PreferenceManager(getApplicationContext());
        setContentView(binding.getRoot());
        getChatContact();

        // Init attributes
        imageButtonBack = (ImageButton) findViewById(R.id.back_btn);
        imageButtonSendImage = (ImageButton) findViewById(R.id.imageButton2);
        imageButtonSendCamera = (ImageButton) findViewById(R.id.imageButton);
        linearLayoutActions = (LinearLayout) findViewById(R.id.layout_actions);
        editTextInputChat = (EmojiconEditText) findViewById(R.id.chat_input);
        imageButtonShowMore = (ImageButton) findViewById(R.id.show_more_btn);
        imageButtonSendMessage = (ImageButton) findViewById(R.id.send_btn);
        imgPreview = (ImageView) findViewById(R.id.imagePreview);
        chatName = (TextView) findViewById(R.id.chatName);
        senderEmail = shp.getString("userEmail");

        activityChatLayout = (ConstraintLayout) findViewById(R.id.activity_main_layout);
        imageButtonEmoji = (ImageView) findViewById(R.id.emoji_btn);
        RecyclerView recyclerViewMessages = (RecyclerView) findViewById(R.id.rcv_messages);
        emojIconActions = new EmojIconActions(this, binding.getRoot(), imageButtonEmoji, editTextInputChat);

        chatName.setText(receiverUser.getName());
        //Handle events
        setUpMessages(recyclerViewMessages);
        emojIconActions.ShowEmojicon();

        imageButtonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent backMainScreenIntent = new Intent(getApplication(), MainActivity.class);
                startActivity(backMainScreenIntent);

            }
        });

        editTextInputChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                linearLayoutActions.setVisibility(View.GONE);
                imageButtonShowMore.setVisibility(View.VISIBLE);
            }
        });

        editTextInputChat.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean isFocus) {
                if(isFocus) {
                    linearLayoutActions.setVisibility(View.GONE);
                    imageButtonShowMore.setVisibility(View.VISIBLE);
//                    recyclerViewMessages.smoothScrollToPosition(listChat.size() - 1);
                }
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

        imageButtonShowMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                linearLayoutActions.setVisibility(View.VISIBLE);
                imageButtonShowMore.setVisibility(View.GONE);
            }
        });
        imageButtonSendMessage.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                String msg = editTextInputChat.getText().toString();
                if(!msg.equals("")) {
                    String sentDate = CurrentDateTimeChat();
                    DB.insertDataMessage(senderEmail, receiverUser.getEmail(), msg, sentDate);
                    customChatAdapter.addChatItem(new Message(1,senderEmail,receiverUser.getEmail(), msg,sentDate, true));
                    recyclerViewMessages.smoothScrollToPosition(listChat.size() - 1);
                    editTextInputChat.setText("");
                    editTextInputChat.requestFocus();
                }else {
                    Toast.makeText(ChatActivity.this, "Bạn không thể gửi tin nhắn trống", Toast.LENGTH_SHORT).show();
                }
            }
        });

        imageButtonSendImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/+");
                startActivityForResult(intent, MY_RESULT_LOAD_IMAGE);
            }
        });
        imageButtonSendCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.TITLE,"New Picture");
                values.put(MediaStore.Images.Media.DESCRIPTION,"From your Camera");
                fileUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                startActivityForResult(intent,MY_CAMERA_REQUEST_CODE);
            }
        });
        emojIconActions.setUseSystemEmoji(true);
    }


    private void setUpMessages(RecyclerView recyclerView) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        customChatAdapter = new ChatAdapter(this, listChat);
        recyclerView.setAdapter(customChatAdapter);
        setFirstData();
//        recyclerView.smoothScrollToPosition(listChat.size() - 1);
    }

    private void setFirstData() {
        listChat = getListMessage();
        customChatAdapter.setData(listChat);
    }

    private void getChatContact() {
        intent = getIntent();
        bundle = intent.getExtras();
        currentContact = (Contact)bundle.getSerializable("contact");
        receiverUser = (User)bundle.getSerializable("DBReceiver");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String CurrentDateTimeChat() {
        LocalDateTime myDateObj = LocalDateTime.now();
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        String formattedDate = myDateObj.format(myFormatObj);
        return formattedDate;
    }

    private List<Message> getListMessage() {
        List<Message> list = new ArrayList<>();
        list = DB.getChat(senderEmail, receiverUser.getEmail());
        Collections.sort(list, new Comparator<Message>() {
            @Override
            public int compare(Message message, Message t1) {
                return message.getSentDate().compareTo(t1.getSentDate());
            }
        });
        for(int i=0;i<list.size();i++) {
            if(!list.get(i).getFromMail().equals(senderEmail)) {
                list.get(i).setFromSelf(false);
            }
        }
        return list;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == MY_CAMERA_REQUEST_CODE) {
            if(requestCode == RESULT_OK) {
                try {
                    Bitmap thumbnail = MediaStore.Images.Media.getBitmap(getContentResolver(), fileUri);
                    imgPreview.setImageBitmap(thumbnail);
                    imgPreview.setVisibility(View.VISIBLE);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else if(requestCode == MY_RESULT_LOAD_IMAGE) {
                if(requestCode == RESULT_OK) {
                    try {
                        final Uri imageUri = data.getData();
                        InputStream inputStream = getContentResolver().openInputStream(imageUri);
                        Bitmap selectImage = BitmapFactory.decodeStream(inputStream);
                        imgPreview.setImageBitmap(selectImage);
                        imgPreview.setVisibility(View.VISIBLE);
                        fileUri = imageUri;
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
            else {
                Toast.makeText(this, "Please choose image", Toast.LENGTH_SHORT);
            }
        }
    }


}