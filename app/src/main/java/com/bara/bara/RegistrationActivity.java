package com.bara.bara;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

public class RegistrationActivity extends AppCompatActivity {

    private Button mRegistration;
    private EditText mEmail,mPassword,mName;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthStateListner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        firebaseAuthStateListner = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null)
                {
                    Intent intent = new Intent(getApplication(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                    return;
                }
            }
        };

        mAuth = FirebaseAuth.getInstance();

        mRegistration = findViewById(R.id.registration);
        mName = findViewById(R.id.name);
        mEmail = this.<EditText>findViewById(R.id.email);
        mPassword = findViewById(R.id.password);

        mRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = mName.getText().toString();
                final String email = mEmail.getText().toString();
                final String password = mPassword.getText().toString();

                mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener( RegistrationActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful())
                        {
                            Toast.makeText(getApplication(),"Registration ERROR",Toast.LENGTH_SHORT).show();
                        }
                        else {
                            String userId = mAuth.getCurrentUser().getUid();
                            DatabaseReference currentUserDb = FirebaseDatabase.getInstance().getReference().child("users").child(userId);

                            Map userInfo = new HashMap<>();
                            userInfo.put("email",email);
                            userInfo.put("name",name);

                            currentUserDb.updateChildren(userInfo);
                        }
                    }
                });
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(firebaseAuthStateListner);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(firebaseAuthStateListner);
    }
}
