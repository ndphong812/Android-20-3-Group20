package com.example.messenger;

import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO;
import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_UNSPECIFIED;
import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.messenger.Database.DataContext;
import com.example.messenger.Services.LoadImageFromURL;
import com.example.messenger.Services.PreferenceManager;
import com.example.messenger.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import com.squareup.picasso.Picasso;
import android.util.Base64;

public class SettingsActivity extends AppCompatActivity{

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://messenger-50d65-default-rtdb.firebaseio.com/");
    private String encodedImage;
    private PreferenceManager preferenceManager;
    ShapeableImageView avatarUser;
    DataContext DB;
    private final int PICK_IMAGE_REQUEST = 22;
    private Uri filePath;
    Button logoutButton;
    ImageButton backButton;
    FirebaseStorage storage;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.layout_settings);

        ListView optionsListView = findViewById(R.id.firstListView);
        preferenceManager = new PreferenceManager(getApplicationContext());

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        StorageReference imageStorage = storageReference.child("images/"+preferenceManager.getString("username"));
        DB = new DataContext(this);

        backButton = (ImageButton) findViewById(R.id.btnBack);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

        TextView UserName = findViewById(R.id.usernameTxt);
        databaseReference.child("User").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserName.setText(snapshot.child(preferenceManager.getString("userID")).child("name").getValue(String.class));
                LoadImageFromURL loadImageFromURL = new LoadImageFromURL(avatarUser);
                loadImageFromURL.execute(snapshot.child(preferenceManager.getString("userID")).child("image").getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        avatarUser = findViewById(R.id.avatarImg);
//        try{
//            final File localFile = File.createTempFile(preferenceManager.getString("username"),"png");
//            imageStorage.getFile(localFile)
//                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
//                        @Override
//                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
//                            Toast.makeText(SettingsActivity.this, "Oke", Toast.LENGTH_SHORT).show();
//                            Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
//                            avatarUser.setImageBitmap(bitmap);
//                        }
//                    })
//                    .addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            Toast.makeText(SettingsActivity.this, "Error loading images", Toast.LENGTH_SHORT).show();
//                        }
//                    });
//        }
//        catch (IOException e){
//            e.printStackTrace();
//        }

        System.out.println(preferenceManager.getString("userEmail"));

        String[] sectionLabels = {"Chế độ tối", "Đăng xuất", "Đổi mật khẩu"};

        Integer[] sectionIcons = {R.drawable.ic_dark_mode, R.drawable.ic_switch_account, R.drawable.ic_account_settings};

        AdapterSettings firstAdapter;

        firstAdapter = new AdapterSettings(SettingsActivity.this, R.layout.layout_settings_item, sectionLabels, sectionIcons);

        avatarUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelectImage();
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
                    case 1:
                        databaseReference.child("User").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String user = preferenceManager.getString("username");
                                String temp = user.split("@", 2)[0];
                                Toast.makeText(SettingsActivity.this, "Logout successfully", Toast.LENGTH_SHORT).show();
                                databaseReference.child("User").child(temp.toString()).child("isLogined").setValue(false);

                                Intent intent  = new Intent(getApplicationContext(), login.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        break;
                    case 2:
                        LayoutInflater inflater = (LayoutInflater)
                                getSystemService(LAYOUT_INFLATER_SERVICE);
                        View popupView = inflater.inflate(R.layout.update_password_popup, null);

                        int width = LinearLayout.LayoutParams.MATCH_PARENT;
                        int height = LinearLayout.LayoutParams.MATCH_PARENT;
                        boolean focusable = true;
                        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

                        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

                        popupView.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
                                popupWindow.dismiss();
                                return true;
                            }
                        });

                        EditText oldPassword = (EditText) popupWindow.getContentView().findViewById(R.id.old_password);
                        EditText newPassword = (EditText) popupWindow.getContentView().findViewById(R.id.new_password);
                        EditText retypeNewPassword = (EditText) popupWindow.getContentView().findViewById(R.id.retype_new_password);
                        Button changePassword = (Button) popupWindow.getContentView().findViewById(R.id.update_btn);

                        changePassword.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String oldPass = oldPassword.getText().toString();
                                String newPass = newPassword.getText().toString();
                                String retypePass = retypeNewPassword.getText().toString();

                                if (oldPass.equals("") || newPass.equals("") || retypePass.equals("")) {
                                    Toast.makeText(getApplicationContext(), "Please do not leave any fields bank", Toast.LENGTH_SHORT).show();
                                } else {
                                    databaseReference.child("User").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (oldPass.equals(snapshot.child(preferenceManager.getString("userID")).child("password").getValue(String.class))) {
                                                if (newPass.equals(retypePass) && newPass.length() >= 6) {
                                                    databaseReference.child("User").child(preferenceManager.getString("userID")).child("password").setValue(newPass);
                                                    Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                                                } else if (newPass.length() < 6) {
                                                    Toast.makeText(getApplicationContext(), "Password length must be longer than 6 characters", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(getApplicationContext(), "New password doesn't match", Toast.LENGTH_SHORT).show();
                                                }
                                            } else {
                                                Toast.makeText(getApplicationContext(), "Wrong old password", Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }
                            }
                        });
                        break;
                    default:
                        Toast.makeText(SettingsActivity.this, "This part is not available for now!", Toast.LENGTH_LONG).show();
                        break;
                }
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
                                    + preferenceManager.getString("username"));

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
                                    String user = preferenceManager.getString("username");
                                    String temp = user.split("@", 2)[0];
                                    databaseReference.child("User").child(temp.toString()).child("image").setValue("https://firebasestorage.googleapis.com/v0/b/messenger-50d65.appspot.com/o/images%2F"+user.toString()+"?alt=media");
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
                            preferenceManager.putString("imageUser", encodedImage);
                            Log.e("Image", encodedImage);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
    );
}

