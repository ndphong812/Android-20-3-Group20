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
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.messenger.Database.DataContext;
import com.example.messenger.Services.PreferenceManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import android.util.Base64;

public class SettingsActivity extends AppCompatActivity{

    PreferenceManager shp;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://messenger-50d65-default-rtdb.firebaseio.com/");
    private String encodedImage;
    ImageView avatarUser;
    DataContext DB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_settings);

        ListView optionsListView = findViewById(R.id.firstListView);

        shp = new PreferenceManager(getApplicationContext());
        DB = new DataContext(this);

        TextView UserName = findViewById(R.id.usernameTxt);
        databaseReference.child("User").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserName.setText(snapshot.child(shp.getString("userID")).child("name").getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        avatarUser = findViewById(R.id.avatarImg);

        System.out.println(shp.getString("userEmail"));

        if(DB.getImage(shp.getString("userEmail")) == null) {
            avatarUser.setImageResource(R.drawable.ic_launcher_background);
        } else {
            byte[] bytes = Base64.decode(DB.getImage(shp.getString("userEmail")), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            avatarUser.setImageBitmap(bitmap);
        }

        String[] sectionLabels = {"Chế độ tối", "Đăng xuất", "Đổi mật khẩu"};

        Integer[] sectionIcons = {R.drawable.ic_dark_mode, R.drawable.ic_switch_account, R.drawable.ic_account_settings};

        AdapterSettings firstAdapter;

        firstAdapter = new AdapterSettings(SettingsActivity.this, R.layout.layout_settings_item, sectionLabels, sectionIcons);

        avatarUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                pickImage.launch(intent);
                DB.updateImageUser(shp.getString("userEmail"), shp.getString("imageUser"));
            }
        });

        optionsListView.setAdapter(firstAdapter);
        optionsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0:
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
                        break;
                    default:
                        Toast.makeText(SettingsActivity.this, "This part is not available for now!", Toast.LENGTH_LONG).show();
                        break;
                }
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

