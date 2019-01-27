package com.android.citygroom;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class SignupSection extends AppCompatActivity {

    Button signup;
    DatabaseReference rootref;
    ProgressDialog p;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_section);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        signup = findViewById(R.id.signup_btn);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                p = new ProgressDialog(SignupSection.this);
                if(isNetworkAvailable())
                {
                    addUser();
                }
                else Toast.makeText(SignupSection.this, "Please ensure network connectivity!", Toast.LENGTH_LONG).show();


            }
        });

    }

    private void addUser() {


        final EditText editTextName = findViewById(R.id.name_txtbx);
        final EditText editTextEmail = findViewById(R.id.email_txtbx);
        final EditText editTextPwd = findViewById(R.id.pwd_txtbx);
        final EditText editTextConfpwd = findViewById(R.id.confpwd_txtbx);

        String name = editTextName.getText().toString().trim();
        final String email = editTextEmail.getText().toString().trim();
        String pwd = editTextPwd.getText().toString().trim();
        String confpwd = editTextConfpwd.getText().toString().trim();


        int verify = verify(name, email, pwd, confpwd);

        if (verify == 1) {
            Toast.makeText(this, "Please fill in all the details!", Toast.LENGTH_LONG).show();
        }
        else if (verify == 2)

        {
            Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_LONG).show();
        }
        else if (verify == 3) {

                p.setMessage("We are registering you! Please wait...");
                p.show();

                rootref = FirebaseDatabase.getInstance().getReference("USERS");

                final User user = new User(name, email, hashPassword(pwd));

                rootref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if(dataSnapshot.child(email).exists())
                        {
                            Toast.makeText(SignupSection.this, "Email Id already exists!", Toast.LENGTH_LONG).show();
                            editTextName.setText("");
                            editTextEmail.setText("");
                            editTextPwd.setText("");
                            editTextConfpwd.setText("");

                            p.dismiss();
                        }
                        else
                        {
                            rootref.child(email).setValue(user);
                            Toast.makeText(SignupSection.this, "Signed Up successfully!", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(SignupSection.this, SigninSection.class));

                            p.dismiss();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        }
    }

    private int verify(String username, String email, String pwd, String confPwd)
    {

        // Checking for empty fields since all fields must be filled.
        if(username.isEmpty() || email.isEmpty() || pwd.isEmpty() || confPwd.isEmpty())
        {
            return 1;
        }

        // Matching password and confirm password fields
        if(!pwd.equals(confPwd))
        {
            return 2;
        }
        return 3;
    }

    private String hashPassword(String input)
    {
        try {

            // Static getInstance method is called with hashing MD5
            MessageDigest md = MessageDigest.getInstance("MD5");

            // digest() method is called to calculate message digest
            //  of an input digest() return array of byte
            byte[] messageDigest = md.digest(input.getBytes());

            // Convert byte array into signum representation
            BigInteger no = new BigInteger(1, messageDigest);

            // Convert message digest into hex value
            String hashtext = no.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        }

        // For specifying wrong message digest algorithms
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
