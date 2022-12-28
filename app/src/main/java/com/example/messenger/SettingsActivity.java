package com.example.messenger;

import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO;
import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_UNSPECIFIED;
import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES;

import android.app.Activity;
import android.app.ProgressDialog;
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
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.messenger.Database.DataContext;
import com.example.messenger.Services.PreferenceManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import android.util.Base64;

public class SettingsActivity extends AppCompatActivity{

    PreferenceManager shp;
    private String encodedImage;
    ImageView avatarUser;
    DataContext DB;
    private final int PICK_IMAGE_REQUEST = 22;
    private Uri filePath;

    FirebaseStorage storage;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_settings);

        int itemHeight = 165;


        ListView firstListView = findViewById(R.id.firstListView);
        ListView secondListView = findViewById(R.id.secondListView);
        ListView thirdListView = findViewById(R.id.thirdListView);
        ListView fourthListView = findViewById(R.id.fourthListView);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

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
//                Toast.makeText(SettingsActivity.this, "Click avatar", Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                pickImage.launch(intent);
//                DB.updateImageUser(shp.getString("userEmail"), shp.getString("imageUser"));

                SelectImage();
//                uploadImage();
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

    private void SelectImage()
    {

        // Defining Implicit Intent to mobile gallery

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(
                Intent.createChooser(
                        intent,
                        "Select Image from here..."),
                PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    Intent data)
    {

        super.onActivityResult(requestCode,
                resultCode,
                data);

        // checking request code and result code
        // if request code is PICK_IMAGE_REQUEST and
        // resultCode is RESULT_OK
        // then set image in the image view

        if (requestCode == PICK_IMAGE_REQUEST
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null) {

            // Get the Uri of data
            filePath = data.getData();
            try {

                // Setting image on image view using Bitmap
                Bitmap bitmap = MediaStore
                        .Images
                        .Media
                        .getBitmap(
                                getContentResolver(),
                                filePath);
                avatarUser.setImageBitmap(bitmap);
                uploadImage();
            }

            catch (IOException e) {
                // Log the exception
                e.printStackTrace();
            }
        }
    }

    private void uploadImage() {
        if (filePath != null) {
            Toast
                    .makeText(SettingsActivity.this,
                            "Image Uploaded!!",
                            Toast.LENGTH_SHORT)
                    .show();

            // Code for showing progressDialog while uploading
            ProgressDialog progressDialog
                    = new ProgressDialog(SettingsActivity.this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            // Defining the child of storageReference
            StorageReference ref
                    = storageReference
                    .child(
                            "images/"
                                    + UUID.randomUUID().toString());

            // adding listeners on upload
            // or failure of image
            ref.putFile(filePath)
                    .addOnSuccessListener(
                            new OnSuccessListener<UploadTask.TaskSnapshot>() {

                                @Override
                                public void onSuccess(
                                        UploadTask.TaskSnapshot taskSnapshot) {

                                    // Image uploaded successfully
                                    // Dismiss dialog
                                    progressDialog.dismiss();
                                    Toast
                                            .makeText(SettingsActivity.this,
                                                    "Image Uploaded!!",
                                                    Toast.LENGTH_SHORT)
                                            .show();
                                }
                            })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            // Error, Image not uploaded
                            progressDialog.dismiss();

                            Toast
                                    .makeText(SettingsActivity.this,
                                            "Failed " + e.getMessage(),
                                            Toast.LENGTH_SHORT)
                                    .show();
                        }
                    })
                    .addOnProgressListener(
                            new OnProgressListener<UploadTask.TaskSnapshot>() {

                                // Progress Listener for loading
                                // percentage on the dialog box
                                @Override
                                public void onProgress(
                                        UploadTask.TaskSnapshot taskSnapshot) {
                                    double progress
                                            = (100.0
                                            * taskSnapshot.getBytesTransferred()
                                            / taskSnapshot.getTotalByteCount());
                                    progressDialog.setMessage(
                                            "Uploaded "
                                                    + (int) progress + "%");
                                }
                            });
        }
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

