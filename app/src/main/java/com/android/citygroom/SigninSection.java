package com.android.citygroom;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

public class SigninSection extends AppCompatActivity {

    Button  signin_btn;
    DatabaseReference usersref;
    String email,pwd;
    ProgressDialog p;
    public static final String MyPREFERENCES = "MyPrefs" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin_section);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        signin_btn = (Button) findViewById(R.id.signin_btn);
        signin_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                p = new ProgressDialog(SigninSection.this);
                if(isNetworkAvailable())
                {
                    loginuser();
                }
                else Toast.makeText(SigninSection.this, "Please ensure network connectivity!", Toast.LENGTH_LONG).show();

            }
        });
    }

    private void loginuser()
    {
        EditText editTextEmail = findViewById(R.id.signin_email_txtbx);
        EditText editTextPwd = findViewById(R.id.signin_pwd_txtbx);

        email = editTextEmail.getText().toString().trim();
        pwd = editTextPwd.getText().toString().trim();

        if(email.isEmpty() || pwd.isEmpty())
        {
            Toast.makeText(this, "Please fill in all the details!", Toast.LENGTH_LONG).show();
        }
        else
        {
            p.setMessage("Logging in...");
            p.show();

            pwd = hashPassword(pwd);
            usersref = FirebaseDatabase.getInstance().getReference("USERS");

            usersref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.child(email).exists())
                    {
                            User user = dataSnapshot.child(email).getValue(User.class);

                            if (user.getPwd().equals(pwd))
                            {
                                SharedPreferences sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedpreferences.edit();

                                editor.putString("UserId_Email", user.getEmail());
                                editor.apply();

                                Toast.makeText(SigninSection.this, "Login Successful!", Toast.LENGTH_LONG).show();

                                Intent intent = new Intent(SigninSection.this, AfterloginSection.class);
                                intent.putExtra("user_email",email);
                                startActivity(intent);
                                finish();
                                p.dismiss();

                            } else
                                Toast.makeText(SigninSection.this, "Invalid Credentials!", Toast.LENGTH_LONG).show();


                    } else
                        Toast.makeText(SigninSection.this, "Invalid Credentials!", Toast.LENGTH_LONG).show();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
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
