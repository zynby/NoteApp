package com.shariati.notes;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shariati.notes.R;

import java.util.HashMap;


public class RegisterActivity extends AppCompatActivity {
    private final String regexMobile = "^(\\+98|0098|98|0)?9\\d{9}$";
    private final String regexEmail = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
    private final String regexPassword = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).{8,}$";
    private final String regexUsername = "^[a-z0-9_.]{3,16}$";
    private EditText username;
    private EditText email;
    private EditText password;
    private EditText mobile;
    private Button registerButton;

    private DatabaseReference mRootRef;
    private FirebaseAuth mAuth;

    ProgressDialog pd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        changeStatusBarColor();

        username = findViewById(R.id.editTextName);
        email = findViewById(R.id.editTextEmail);
        mobile = findViewById(R.id.editTextMobile);
        password = findViewById(R.id.editTextPassword);
        registerButton = findViewById(R.id.cirRegisterButton);


        mRootRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        pd = new ProgressDialog(this);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txtUsername = username.getText().toString();
                String txtMobile = mobile.getText().toString();
                String txtEmail = email.getText().toString();
                String txtPassword = password.getText().toString();

                if (TextUtils.isEmpty(txtUsername) || TextUtils.isEmpty(txtMobile) || TextUtils.isEmpty(txtEmail) || TextUtils.isEmpty(txtPassword)) {
                    Toast.makeText(RegisterActivity.this, "Empty Fields!", Toast.LENGTH_SHORT).show();
                } else if (!(txtMobile.matches(regexMobile))) {
                    Toast.makeText(RegisterActivity.this, "Mobile Number Not Valid!", Toast.LENGTH_SHORT).show();
                } else if (!(txtEmail.matches(regexEmail))) {
                    Toast.makeText(RegisterActivity.this, "Email Not Valid!", Toast.LENGTH_SHORT).show();
                } else if (!(txtPassword.matches(regexPassword))) {
                    Toast.makeText(RegisterActivity.this, "Password Not Valid", Toast.LENGTH_SHORT).show();
                } else if (!(txtUsername.matches(regexUsername))) {
                    Toast.makeText(RegisterActivity.this, "Username Not Valid", Toast.LENGTH_SHORT).show();
                } else {
                    registerUser(txtUsername, txtMobile, txtEmail, txtPassword);
                }
            }
        });
    }

    private void registerUser(final String username, final String mobile, final String email, final String password) {

        pd.setMessage("Please Wait");
        pd.show();

        mAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {

                HashMap<String, Object> map = new HashMap<>();
                map.put("mobile", mobile);
                map.put("email", email);
                map.put("username", username);
                map.put("id", mAuth.getCurrentUser().getUid());

                mRootRef.child("Users").child(mAuth.getCurrentUser().getUid()).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            pd.dismiss();
                            Toast.makeText(RegisterActivity.this, "Register is Successful", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(RegisterActivity.this, HomePage.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.setStatusBarColor(Color.TRANSPARENT);
            window.setStatusBarColor(getResources().getColor(R.color.register_bk_color));
        }
    }

    public void onLoginClick(View view) {
        startActivity(new Intent(this, LoginActivity.class));
        overridePendingTransition(R.anim.slide_in_left, android.R.anim.slide_out_right);

    }


}
