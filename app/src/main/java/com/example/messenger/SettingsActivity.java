package com.example.messenger;

import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO;
import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_UNSPECIFIED;
import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.messenger.Database.DataContext;
import com.example.messenger.Services.PreferenceManager;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import android.util.Base64;

public class SettingsActivity extends AppCompatActivity{

    PreferenceManager shp;
    private String encodedImage;
    ImageView avatarUser;
    DataContext DB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_settings);

        int itemHeight = 165;

        ListView firstListView = findViewById(R.id.firstListView);
        ListView secondListView = findViewById(R.id.secondListView);
        ListView thirdListView = findViewById(R.id.thirdListView);
        ListView fourthListView = findViewById(R.id.fourthListView);
        shp = new PreferenceManager(getApplicationContext());
        DB = new DataContext(this);


        TextView UserName = findViewById(R.id.usernameTxt);
        UserName.setText(shp.getString("userName"));

        avatarUser = findViewById(R.id.avatarImg);

        System.out.println(shp.getString("userEmail"));

        if(DB.getImage(shp.getString("userEmail")) == null) {
            avatarUser.setImageResource(R.drawable.ic_launcher_background);
        } else {
            byte[] bytes = Base64.decode(DB.getImage(shp.getString("userEmail")), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            avatarUser.setImageBitmap(bitmap);
        }


        String[] firstSectionLabels = {"Chế độ tối", "Tin nhắn đang chờ", "Đoạn chat đã lưu trữ"};
        String[] secondSectionLabels = {"Trạng thái hoạt động", "Tên người dùng"};
        String[] thirdSectionLabels = {"Quyền riêng tư", "Avatar", "Thông báo và âm thanh", "SMS", "Danh bạ điện thoại", "Ảnh và file phương tiện", "Bong bóng"};
        String[] fourthSectionLabels = {"Chuyển tài khoản", "Cài đặt tài khoản", "Trợ giúp", "Chính sách và quyền lợi"};

        Integer[] firstSectionIcons = {R.drawable.ic_dark_mode, R.drawable.ic_request_message, R.drawable.ic_archived_chat};
        Integer[] secondSectionIcons = {R.drawable.ic_active_status, R.drawable.ic_username};
        Integer[] thirdSectionIcons = {R.drawable.ic_privacy, R.drawable.ic_avatar, R.drawable.ic_notification, R.drawable.ic_sms, R.drawable.ic_contacts, R.drawable.ic_image_media, R.drawable.ic_bubbles};
        Integer[] fourthSectionIcons = {R.drawable.ic_switch_account, R.drawable.ic_account_settings, R.drawable.ic_help, R.drawable.ic_policies};

        AdapterSettings firstAdapter, secondAdapter, thirdAdapter, fourthAdapter;

        firstListView.getLayoutParams().height = firstSectionLabels.length * itemHeight;
        secondListView.getLayoutParams().height = secondSectionLabels.length * itemHeight;
        thirdListView.getLayoutParams().height = thirdSectionLabels.length * itemHeight;
        fourthListView.getLayoutParams().height = fourthSectionLabels.length * itemHeight;

        firstAdapter = new AdapterSettings(SettingsActivity.this, R.layout.layout_settings_item, firstSectionLabels, firstSectionIcons);
        secondAdapter = new AdapterSettings(SettingsActivity.this, R.layout.layout_settings_item, secondSectionLabels, secondSectionIcons);
        thirdAdapter = new AdapterSettings(SettingsActivity.this, R.layout.layout_settings_item, thirdSectionLabels, thirdSectionIcons);
        fourthAdapter = new AdapterSettings(SettingsActivity.this, R.layout.layout_settings_item, fourthSectionLabels, fourthSectionIcons);


        avatarUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                pickImage.launch(intent);
                DB.updateImageUser(shp.getString("userEmail"), shp.getString("imageUser"));
            }
        });

        firstListView.setAdapter(firstAdapter);
        firstListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    if (AppCompatDelegate.getDefaultNightMode() == MODE_NIGHT_UNSPECIFIED) {
                        AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES);
                        Toast.makeText(SettingsActivity.this, "Chế độ tối đang bật", Toast.LENGTH_LONG).show();
                    } else if (AppCompatDelegate.getDefaultNightMode() == MODE_NIGHT_NO) {
                        AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES);
                        Toast.makeText(SettingsActivity.this, "Chế độ tối đang bật", Toast.LENGTH_LONG).show();

                    } else {
                        AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO);
                        Toast.makeText(SettingsActivity.this, "Chế độ tối đang tắt", Toast.LENGTH_LONG).show();

                    }
                    ;
                } else {
                    Toast.makeText(SettingsActivity.this, "This part is not available for now!", Toast.LENGTH_LONG).show();
                }
            }
        });

        secondListView.setAdapter(secondAdapter);
        secondListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(SettingsActivity.this, "This part is not available for now!", Toast.LENGTH_LONG).show();
            }
        });

        thirdListView.setAdapter(thirdAdapter);
        thirdListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(SettingsActivity.this, "This part is not available for now!", Toast.LENGTH_LONG).show();
            }
        });

        fourthListView.setAdapter(fourthAdapter);
        fourthListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(SettingsActivity.this, "This part is not available for now!", Toast.LENGTH_LONG).show();
            }
        });
    }

    private String encodeImage(Bitmap bitmap) {
        int previewWidth = 150;
        int previewHeight = bitmap.getHeight() * previewWidth / bitmap.getWidth();
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult (
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if(result.getResultCode() == RESULT_OK) {
                    if(result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        try {
                            InputStream inputStream = getContentResolver().openInputStream(imageUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            avatarUser.setImageBitmap(bitmap);
                            encodedImage = encodeImage(bitmap);
                            shp.putString("imageUser", encodedImage);
                            Log.e("Image", encodedImage);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
    );
}

