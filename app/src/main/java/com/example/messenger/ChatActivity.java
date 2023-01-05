package com.example.messenger;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.messenger.AsyncTasks.SendMessageClient;
import com.example.messenger.AsyncTasks.SendMessageServer;
import com.example.messenger.Database.DataContext;
import com.example.messenger.Entities.Message;
import com.example.messenger.Receivers.WifiDirectBroadcastReceiver;
import com.example.messenger.Services.LoadImageFromURL;
import com.example.messenger.Services.PreferenceManager;
import com.example.messenger.adapter.ChatAdapter;
import com.example.messenger.databinding.ActivityChatBinding;
import com.example.messenger.model.Contact;
import com.example.messenger.model.FireMessage;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;

public class ChatActivity extends Activity {
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://messenger-50d65-default-rtdb.firebaseio.com/");
    ActivityChatBinding binding;
    Intent intent;
    Bundle bundle;
    TextView chatName;
    ImageButton imageButtonBack;
    LinearLayout linearLayoutActions;
    ShapeableImageView shapeableImageViewAvatar;

    ImageButton imageButtonShowMore, imageButtonSendMessage, imageButtonSendImage, imageButtonSendCamera;
    ImageView imageButtonEmoji, imgPreview;

    Contact  selfContact;
    Contact currentContact;
    private static List<FireMessage> listChat = new ArrayList<>();
    private static ChatAdapter customChatAdapter;
    private EmojiconEditText editTextInputChat;
    EmojIconActions emojIconActions;
    ConstraintLayout activityChatLayout;
    PreferenceManager shp;
    public static final int MY_RESULT_LOAD_IMAGE = 7172;
    public static final int MY_CAMERA_REQUEST_CODE = 7171;
    String senderEmail;
    DataContext DB;

    private static RecyclerView recyclerViewMessages;

    private static final String TAG = "ChatActivity";
    private static final int PICK_IMAGE = 1;
    private static final int TAKE_PHOTO = 2;
    private static final int RECORD_AUDIO = 3;
    private static final int RECORD_VIDEO = 4;
    private static final int CHOOSE_FILE = 5;
    private static final int DRAWING = 6;
    private static final int DOWNLOAD_IMAGE = 100;
    private static final int DELETE_MESSAGE = 101;
    private static final int DOWNLOAD_FILE = 102;
    private static final int COPY_TEXT = 103;
    private static final int SHARE_TEXT = 104;
    private static final int REQUEST_PERMISSIONS_REQUIRED = 7;

    WifiDirectBroadcastReceiver mReceiver;
    IntentFilter mIntentFilter;
    @SuppressLint("StaticFieldLeak")
    private static ListView listView;
    private static List<Message> listMessage;
    @SuppressLint("StaticFieldLeak")
    private static ChatAdapter chatAdapter;
    private Uri fileUri;
    private String fileURL;
    private ArrayList<Uri> tmpFilesUri;
    private Uri mPhotoUri;

    private Register register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Binding layout
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        DB = new DataContext(this);
        shp = new PreferenceManager(getApplicationContext());

        //get data from intent
        getChatContact();

        //Restart receiver
        mReceiver = WifiDirectBroadcastReceiver.createInstance();
        mReceiver.setmActivity(this);

        String[] PERMISSIONS = {
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO
        };

        if(!hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST_PERMISSIONS_REQUIRED);
        }

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        startService(new Intent(this, MessageService.class));

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
        shapeableImageViewAvatar = (ShapeableImageView) findViewById(R.id.avatar);
        senderEmail = shp.getString("userEmail");

        activityChatLayout = (ConstraintLayout) findViewById(R.id.activity_main_layout);
        imageButtonEmoji = (ImageView) findViewById(R.id.emoji_btn);
        emojIconActions = new EmojIconActions(this, binding.getRoot(), imageButtonEmoji, editTextInputChat);

        //Set up data
        chatAdapter = new ChatAdapter(this, listChat);
        chatName.setText(currentContact.getUsername());
        LoadImageFromURL loadImageFromURL = new LoadImageFromURL(shapeableImageViewAvatar);
        loadImageFromURL.execute(currentContact.getAvatarPath());

        //Handle events
        emojIconActions.ShowEmojicon();
        recyclerViewMessages = (RecyclerView) findViewById(R.id.rcv_messages);
        recyclerViewMessages.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if ( bottom < oldBottom) {
                    recyclerViewMessages.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if(listChat.size() > 0) {
                                recyclerViewMessages.smoothScrollToPosition(listChat.size() - 1);
                            }
                        }
                    }, 100);
                }
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ChatActivity.this);
        recyclerViewMessages.setLayoutManager(linearLayoutManager);

        listChat.clear();
        if(listChat.isEmpty()) {
            databaseReference.child("Messages").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        FireMessage tempFireMessage = dataSnapshot.getValue(FireMessage.class);
                        if(tempFireMessage != null) {
                            if( (tempFireMessage.getFromMail().equals(selfContact.getId())
                                    && tempFireMessage.getToMail().equals(currentContact.getId()) )
                                     || (tempFireMessage.getFromMail().equals(currentContact.getId())
                                    && tempFireMessage.getToMail().equals(selfContact.getId())) ) {

                                listChat.add(tempFireMessage);
                            }
                        }
                    }
                    customChatAdapter = new ChatAdapter(ChatActivity.this, listChat);
                    customChatAdapter.setSelfContact(selfContact);
                    customChatAdapter.setCurrentContact(currentContact);
                    customChatAdapter.setData(listChat);
                    recyclerViewMessages.setAdapter(customChatAdapter);

                    if(listChat.size() > 0) {
                        recyclerViewMessages.smoothScrollToPosition(listChat.size() - 1);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
        listener();
    }

    //Handle Event in ChatActivity
    private void listener () {
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
                if(listChat.size() > 0) {
                    recyclerViewMessages.smoothScrollToPosition(listChat.size());
                }
            }
        });

        editTextInputChat.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean isFocus) {
                if(isFocus) {
                    linearLayoutActions.setVisibility(View.GONE);
                    imageButtonShowMore.setVisibility(View.VISIBLE);
                }
                if(listChat.size() > 0) {
                    recyclerViewMessages.smoothScrollToPosition(listChat.size());
                }
            }
        });

        editTextInputChat.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                linearLayoutActions.setVisibility(View.GONE);
                imageButtonShowMore.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable editable) {}
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
                    sendMessage(Message.TEXT_MESSAGE, msg);                    
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

    @SuppressLint("MissingPermission")
    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(mReceiver, mIntentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            unregisterReceiver(mReceiver);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }


    private void sendMessage(int type, String msg) {
        String sentDate = null;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            sentDate = CurrentDateTimeChat();
        }

        Message message = new Message(Message.TEXT_MESSAGE, selfContact.getId(), currentContact.getId(), msg, sentDate, true);
        FireMessage fireMessage = new FireMessage(Message.TEXT_MESSAGE, selfContact.getId(),  currentContact.getId(), msg, sentDate, true);

        databaseReference.child("Messages").child("message-" + new Date().getTime()).setValue(fireMessage);

        if(mReceiver.isGroupOwner() == WifiDirectBroadcastReceiver.IS_OWNER){
            new SendMessageServer(ChatActivity.this, true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, message);
        }
        else if(mReceiver.isGroupOwner() == WifiDirectBroadcastReceiver.IS_CLIENT){
            new SendMessageClient(ChatActivity.this, mReceiver.getOwnerAddr()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, message);
        }

        editTextInputChat.setText("");
        editTextInputChat.requestFocus();
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        super.onContextItemSelected(item);

        switch (item.getItemId()) {
            case 201:
                customChatAdapter.deleteMessage(item.getGroupId());
                //CAll API delete message

                return true;
            case 202:
                //Call API for copy message
                customChatAdapter.copyMessage(item.getGroupId());
                return true;
            case 203:
                //Call API for download message content
                customChatAdapter.downloadMessage(item.getGroupId());
                return true;
        }
        return true;
    }

    private void getChatContact() {
        intent = getIntent();
        bundle = intent.getExtras();
        currentContact = (Contact) bundle.getSerializable("contact");
        selfContact = (Contact) bundle.getSerializable("selfContact");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String CurrentDateTimeChat() {
        LocalDateTime myDateObj = LocalDateTime.now();
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        String formattedDate = myDateObj.format(myFormatObj);
        return formattedDate;
    }


    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public static void refreshList(FireMessage message, boolean isMine){
        message.setMine(isMine);
        customChatAdapter.addChatItem(message);
        int sizeList = listChat.size();
        customChatAdapter.notifyItemInserted(sizeList - 1);

        //Scroll to the last element of the list
        recyclerViewMessages.smoothScrollToPosition(sizeList);
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