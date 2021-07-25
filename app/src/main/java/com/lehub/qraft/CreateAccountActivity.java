package com.lehub.qraft;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class CreateAccountActivity extends AppCompatActivity {

    private AppCompatEditText createAccountName,createAccountEmailInput,createAccountPasswordInput,createAccountConfirmPasswordInput;
    private AppCompatButton createAccountButton;

    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();


        createAccountName = findViewById(R.id.createAccountName);
        createAccountEmailInput = findViewById(R.id.createAccountEmailInput);
        createAccountPasswordInput = findViewById(R.id.createAccountPasswordInput);
        createAccountConfirmPasswordInput = findViewById(R.id.createAccountConfirmPasswordInput);
        createAccountButton = findViewById(R.id.createAccountButton);

        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                createAccount();
            }
        });

    }

    private void createAccount() {

        String name = createAccountName.getText().toString();
        String email = createAccountEmailInput.getText().toString();
        String password = createAccountPasswordInput.getText().toString();
        String confirmPassword = createAccountConfirmPasswordInput.getText().toString();

        if (name.isEmpty()){
            createAccountName.setError("Type your email");
        }
        else if (email.isEmpty()) {
            createAccountEmailInput.setError("Type your email");
        }
        else if (password.isEmpty()) {
            createAccountPasswordInput.setError("Type your password");
        }
        else if (confirmPassword.isEmpty()) {
            createAccountConfirmPasswordInput.setError("Confirm password");
        }
        else if (!password.equals(confirmPassword)){

            createAccountPasswordInput.setError("Password does not match the one below");
            createAccountConfirmPasswordInput.setError("Password does not match the one above");
        }
        else{

            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()){

                        String uid = mAuth.getCurrentUser().getUid();

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("name", name);
                        hashMap.put("email", email);
                        hashMap.put("uid", uid);

                        firestore.collection("Users").document(uid).set(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull  Task<Void> task) {

                                if (task.isSuccessful()){

                                    Intent intent = new Intent(CreateAccountActivity.this,MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }

                            }
                        });
                    }
                    else {
                        Toast.makeText(CreateAccountActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(CreateAccountActivity.this, "Failed" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}