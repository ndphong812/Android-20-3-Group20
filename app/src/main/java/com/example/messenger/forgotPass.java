package com.example.messenger;

import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;

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
    DBLogin DB;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_password);
        DB = new DBLogin(this);

        email = (EditText) findViewById(R.id.email);
        Require_email = (Button) findViewById(R.id.require_btn);

        Require_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                String email_text = email.getText().toString();
//                sendRequireEmail(email_text);

//                final String username="tsnemailsndr@gmail.com";
//                final String password="Vx~4]4P6s#>z-~UT";

                try {
                    String username="ngphut47@gmail.com";
                    String password="0399158711";
                    String messageToSend = "Hello you";

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

                    mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(email.getText().toString()));

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

                } catch (AddressException e) {
                    e.printStackTrace();
                } catch (MessagingException e) {
                    e.printStackTrace();
                }

            }
        });
    }
}


//    protected void sendRequireEmail(String email) {
//        String TAG = "RESETPWRSLT";
//        if (DB.forgotPassword(email)) {
//            Log.d(TAG,"Password was reset and an email sent to " + email);
//        } else {
//            Log.d(TAG,"WARNING an attempt to reset a password failed for the email " + email);
//        }
//    }

