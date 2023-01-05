package com.example.messenger;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.example.messenger.Services.PreferenceManager;
import com.google.firebase.database.ValueEventListener;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;


public class forgotPass extends Activity {
    EditText email;
    Button Require_email;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://messenger-50d65-default-rtdb.firebaseio.com/");
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_password);
        preferenceManager = new PreferenceManager(getApplicationContext());

        email = (EditText) findViewById(R.id.email);
        Require_email = (Button) findViewById(R.id.require_btn);

        Require_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String username="pt071102@gmail.com";
                String password="arzweuxbcfebpdgc";
                String newPassword = String.valueOf(Random_Code());
                String messageToSend = newPassword;

                String stringHost = "smtp.gmail.com";

                Properties properties = System.getProperties();

                properties.put("mail.smtp.host", stringHost);
                properties.put("mail.smtp.port", "465");
                properties.put("mail.smtp.ssl.enable", "true");
                properties.put("mail.smtp.auth", "true");

                javax.mail.Session session = Session.getInstance(properties, new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

                MimeMessage mimeMessage = new MimeMessage(session);

                String emailReceiver = email.getText().toString();

                databaseReference.child("User").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String userID = emailReceiver.split("@", 2)[0];
                        if (snapshot.hasChild(userID)) {
                            databaseReference.child("User").child(userID.toString()).child("password").setValue(newPassword.toString());
                            try {
                                mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(emailReceiver));

                                mimeMessage.setSubject("Subject: Android App email");
                                mimeMessage.setText(messageToSend);

                                Thread thread = new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            Transport.send(mimeMessage);
                                        } catch (MessagingException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });

                                thread.start();
                                Toast.makeText(forgotPass.this, "Please check email", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), login.class);
                                startActivity(intent);
                            } catch (AddressException e) {
                                e.printStackTrace();
                            } catch (MessagingException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(forgotPass.this, "User doesn't exists or email format wrong", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });
    }


    public int Random_Code()
    {
        int min = 100000;
        int max = 999999;
        int random_int = (int)Math.floor(Math.random()*(max-min+1)+min);
        return random_int;
    }
}
