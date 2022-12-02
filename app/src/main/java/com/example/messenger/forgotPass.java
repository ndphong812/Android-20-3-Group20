package com.example.messenger;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.messenger.Database.DataContext;

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
    DataContext DB;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_password);
        DB = new DataContext(this);

        email = (EditText) findViewById(R.id.email);
        Require_email = (Button) findViewById(R.id.require_btn);

        Require_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
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
                    if(DB.checkEmailFormatForgot(emailReceiver)) {
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

                        DB.updatePassword(emailReceiver, newPassword);

                        thread.start();
                        Toast.makeText(forgotPass.this, "Please check email", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), login.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(forgotPass.this, "User doesn't exists or email format wrong", Toast.LENGTH_SHORT).show();
                    }

                } catch (AddressException e) {
                    e.printStackTrace();
                } catch (MessagingException e) {
                    e.printStackTrace();
                }

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
