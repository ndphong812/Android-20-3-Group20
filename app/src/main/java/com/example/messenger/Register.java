package com.example.messenger;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.messenger.Database.DataContext;
import com.example.messenger.Services.PreferenceManager;
import com.example.messenger.model.User;

import java.util.ArrayList;

public class Register extends Activity {
    private PreferenceManager preferenceManager;
    EditText fullName, email, password, rt_password;
    Button register;
    DataContext DB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        fullName = (EditText) findViewById(R.id.fullname);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.pass);
        rt_password = (EditText) findViewById(R.id.rt_password);
        register = (Button) findViewById(R.id.require_btn);
        preferenceManager = new PreferenceManager(getApplicationContext());
        DB = new DataContext(this);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = fullName.getText().toString();
                String em = email.getText().toString();
                String pass = password.getText().toString();
                String repass = rt_password.getText().toString();

                if(name.equals("")||em.equals("")||pass.equals("")||repass.equals("")) {
                    Toast.makeText(Register.this, "Please do not leave any fields bank", Toast.LENGTH_SHORT).show();
                } else {
                    if (pass.equals(repass)) {
                        if (!DB.checkEmailFormat(em)) {
                            if (DB.isValidPassword(pass)) {
                                Boolean insert = DB.insertDataLogin(em, name, pass);
                                if (insert) {
                                    Toast.makeText(Register.this, "Make account successfully", Toast.LENGTH_SHORT).show();
                                    preferenceManager.putBoolean("isLogin", false);
                                    preferenceManager.putString("userEmail", em);
                                    preferenceManager.putString("userName", name);
                                    Intent intent = new Intent(getApplicationContext(), login.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(Register.this, "Make account failed", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(Register.this, "Password is very low", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(Register.this, "User already exists or email format wrong", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(Register.this, "Passwords not matching", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    public void onClick(View v) {
        preferenceManager.putBoolean("isLogin", false);
        Intent intent = new Intent(getApplicationContext(), login.class);
        startActivity(intent);
    }

}
