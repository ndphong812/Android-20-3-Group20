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
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.messenger.Database.DataContext;
import com.example.messenger.Entities.Message;
import com.example.messenger.Receivers.WifiDirectBroadcastReceiver;
import com.example.messenger.Services.PreferenceManager;
import com.example.messenger.adapter.ChatAdapter;
import com.example.messenger.databinding.ActivityChatBinding;
import com.example.messenger.model.Contact;
import com.example.messenger.model.User;

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

public class ChatActivity extends Activity {

    ActivityChatBinding binding;
    Intent intent;
    Bundle bundle;
    TextView chatName;
    ImageButton imageButtonBack;
    LinearLayout linearLayoutActions;
    ImageButton imageButtonShowMore;
    ImageButton imageButtonSendMessage;
    ImageButton imageButtonSendImage;
    ImageButton imageButtonSendCamera;
    ImageView imageButtonEmoji;
    ImageView imgPreview;

    Contact currentContact;
    private User receiverUser;
    private List<Message> listChat;
    private ChatAdapter customChatAdapter;
    private EmojiconEditText editTextInputChat;
    EmojIconActions emojIconActions;
    ConstraintLayout activityChatLayout;
    PreferenceManager shp;
    public static final int MY_RESULT_LOAD_IMAGE = 7172;
    public static final int MY_CAMERA_REQUEST_CODE = 7171;
    String senderEmail;
    DataContext DB;

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

    WifiP2pManager mManager;
    WifiP2pManager.Channel mChannel;
    WifiDirectBroadcastReceiver mReceiver;
    IntentFilter mIntentFilter;
    private EditText edit;
    @SuppressLint("StaticFieldLeak")
    private static ListView listView;
    private static List<Message> listMessage;
    @SuppressLint("StaticFieldLeak")
    private static ChatAdapter chatAdapter;
    private Uri fileUri;
    private String fileURL;
    private ArrayList<Uri> tmpFilesUri;
    private Uri mPhotoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        DB = new DataContext(this);
        shp = new PreferenceManager(getApplicationContext());
        setContentView(binding.getRoot());
        getChatContact();

        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
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
                    sendMessage(Message.TEXT_MESSAGE);
//                    customChatAdapter.addChatItem(new Message(senderEmail,receiverUser.getEmail(), msg,sentDate, true));
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

    private void sendMessage(int type) {
        Message message = new Message();
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
        Collections.sort(list, new Comparator<Message>() {
            @Override
            public int compare(Message message, Message t1) {
                return message.getSentDate().compareTo(t1.getSentDate());
            }
        });
//        for(int i=0;i<list.size();i++) {
//            if(!list.get(i).getFromMail().equals(senderEmail)) {
//                list.get(i).setFromSelf(false);
//            }
//        }
        return list;
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

    public static void refreshList(Message message, boolean isMine){
//		Log.v(TAG, "Refresh message list starts");

        message.setMine(isMine);
//		Log.e(TAG, "refreshList: message is from :"+message.getSenderAddress().getHostAddress() );
//		Log.e(TAG, "refreshList: message is from :"+isMine );
        listMessage.add(message);
        chatAdapter.notifyDataSetChanged();

//    	Log.v(TAG, "Chat Adapter notified of the changes");

        //Scroll to the last element of the list
        listView.setSelection(listMessage.size() - 1);
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