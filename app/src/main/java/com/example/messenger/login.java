package com.example.messenger;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import androidx.annotation.NonNull;

import com.example.messenger.Database.DataContext;
import com.example.messenger.Services.PreferenceManager;
import com.example.messenger.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class login extends Activity {
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://messenger-50d65-default-rtdb.firebaseio.com/");
    private PreferenceManager preferenceManager;
    EditText username, password;
    Button signin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        signin = (Button) findViewById(R.id.login_btn);
        preferenceManager = new PreferenceManager(getApplicationContext());

//        if(preferenceManager.getBoolean("isLogin")) {
//            Intent intent  = new Intent(getApplicationContext(), MainActivity.class);
//            startActivity(intent);
//            finish();
//        }

        
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user = username.getText().toString();
                String pass = password.getText().toString();

                if(user.equals("") || pass.equals("")) {
                    Toast.makeText(login.this, "Please enter all the fields", Toast.LENGTH_SHORT).show();
                }
                else {
                    databaseReference.child("User").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String temp = user.split("@", 2)[0];
                            if(snapshot.hasChild(temp)){
                                final String password = snapshot.child(temp).child("password").getValue(String.class);

                                boolean matched = false;
                                try {
                                    matched = validatePassword(pass, password);
                                } catch (NoSuchAlgorithmException e) {
                                    e.printStackTrace();
                                } catch (InvalidKeySpecException e) {
                                    e.printStackTrace();
                                }
                                if(matched) {
                                    preferenceManager.putString("username",user);
                                    Toast.makeText(login.this, "OK", Toast.LENGTH_SHORT).show();
                                    databaseReference.child("User").child(temp.toString()).child("isLogined").setValue(true);
                                    preferenceManager.putString("userID", temp);
                                    Intent intent  = new Intent(getApplicationContext(), MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();
                                }else{
                                    Toast.makeText(login.this, "Failed", Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                Toast.makeText(login.this, "Wrong email", Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });
    }

    public void onClick(View v) {
        Intent intent = new Intent(getApplicationContext(), forgotPass.class);
        startActivity(intent);
    }


    private static boolean validatePassword(String originalPassword, String storedPassword) 
    throws NoSuchAlgorithmException, InvalidKeySpecException
{
    String[] parts = storedPassword.split(":");
    int iterations = Integer.parseInt(parts[0]);

    byte[] salt = fromHex(parts[1]);
    byte[] hash = fromHex(parts[2]);

    PBEKeySpec spec = new PBEKeySpec(originalPassword.toCharArray(),
        salt, iterations, hash.length * 8);
    SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
    byte[] testHash = skf.generateSecret(spec).getEncoded();

    int diff = hash.length ^ testHash.length;
    for(int i = 0; i < hash.length && i < testHash.length; i++)
    {
        diff |= hash[i] ^ testHash[i];
    }
    return diff == 0;
}
private static byte[] fromHex(String hex) throws NoSuchAlgorithmException
{
    byte[] bytes = new byte[hex.length() / 2];
    for(int i = 0; i < bytes.length ;i++)
    {
        bytes[i] = (byte)Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
    }
    return bytes;
}
}