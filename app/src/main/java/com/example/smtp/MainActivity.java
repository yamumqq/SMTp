package com.example.smtp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MainActivity extends AppCompatActivity {

    private EditText etEmail, etSubject, etMessage;
    private Button btnSend;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etEmail = findViewById(R.id.etEmail);
        etSubject = findViewById(R.id.etSubject);
        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);
        progressBar = findViewById(R.id.progressBar);

        btnSend.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String subject = etSubject.getText().toString().trim();
            String message = etMessage.getText().toString().trim();

            if (email.isEmpty() || subject.isEmpty() || message.isEmpty()) {
                Toast.makeText(MainActivity.this, "Заполните все поля", Toast.LENGTH_SHORT).show();
            } else {
                new SendMailTask().execute(email, subject, message);
            }
        });
    }

    private class SendMailTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            btnSend.setEnabled(false);
        }

        @Override
        protected String doInBackground(String... params) {
            String recipientEmail = params[0];
            String subject = params[1];
            String message = params[2];

            // Настройки SMTP сервера (пример для Gmail)
            String host = "smtp.gmail.com";
            String port = "587";
            String username = "isip_d.a.filimonov@mpt.ru"; // Замените на ваш email
            String password = "3186723df"; // Замените на ваш пароль или ключ приложения

            Properties properties = new Properties();
            properties.put("mail.smtp.auth", "true");
            properties.put("mail.smtp.starttls.enable", "true");
            properties.put("mail.smtp.host", host);
            properties.put("mail.smtp.port", port);

            Session session = Session.getInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });

            try {
                Message mimeMessage = new MimeMessage(session);
                mimeMessage.setFrom(new InternetAddress(username));
                mimeMessage.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
                mimeMessage.setSubject(subject);
                mimeMessage.setText(message);

                Transport.send(mimeMessage);
                return "success";
            } catch (MessagingException e) {
                e.printStackTrace();
                return "error: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressBar.setVisibility(View.GONE);
            btnSend.setEnabled(true);

            if (result.equals("success")) {
                Toast.makeText(MainActivity.this, "Письмо отправлено", Toast.LENGTH_SHORT).show();
                etEmail.setText("");
                etSubject.setText("");
                etMessage.setText("");
            } else {
                Toast.makeText(MainActivity.this, "Ошибка: " + result, Toast.LENGTH_LONG).show();
            }
        }
    }
}