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


import com.example.messenger.Database.DataContext;
import com.example.messenger.Services.PreferenceManager;
import com.example.messenger.model.User;

import java.util.ArrayList;

public class login extends Activity {
    private PreferenceManager preferenceManager;
    EditText username, password;
    Button signin;
    DataContext DB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        signin = (Button) findViewById(R.id.login_btn);
        preferenceManager = new PreferenceManager(getApplicationContext());
        DB = new DataContext(this);
        if(preferenceManager.getBoolean("isLogin")) {
            Intent intent  = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }


        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user = username.getText().toString();
                String pass = password.getText().toString();

                if(user.equals("")||pass.equals(""))
                    Toast.makeText(login.this, "Please enter all the fields", Toast.LENGTH_SHORT).show();
                else{
                    Boolean checkuserpass = DB.checkusernamepassword(user, pass);
                    if(checkuserpass==true){
                        Toast.makeText(login.this, "Sign in successfull", Toast.LENGTH_SHORT).show();
                        preferenceManager.putBoolean("isLogin", true);
                        preferenceManager.putString("userEmail", user);
                        preferenceManager.putString("userName", DB.getName(user));
                        Intent intent  = new Intent(getApplicationContext(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }else{
                        Toast.makeText(login.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    public void onClick(View v) {
        Intent intent = new Intent(getApplicationContext(), forgotPass.class);
        startActivity(intent);
    }
}
