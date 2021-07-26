package com.example.kunfypproject.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kunfypproject.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;


public class Login extends AppCompatActivity {

    private com.google.android.material.textfield.TextInputLayout login_email,login_Password;
    private com.google.android.material.button.MaterialButton login_button;
    private TextView login_create_account;

    //Firebase
    private FirebaseUser login_firebaseUser;
    private FirebaseAuth login_auth_user;
    private DatabaseReference login_user_ref;

    @Override
    protected void onStart(){
        super.onStart();

        login_firebaseUser=FirebaseAuth.getInstance().getCurrentUser();

        if(login_firebaseUser!=null){
            System.out.println("Naviagte");
            Intent intent = new Intent(Login.this, DeviceList.class);
            startActivity(intent);
            finish();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login_auth_user=FirebaseAuth.getInstance();
        login_button=findViewById(R.id.login_button);
        login_create_account =findViewById(R.id.login_create_account);
        login_create_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Login.this,SignUp.class);
                startActivity(intent);
            }
        });

        login_email=findViewById(R.id.login_email);
        login_Password=findViewById(R.id.login_password);
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkDataEntered()) {
                    final String email = login_email.getEditText().getText().toString();
                    final String password = login_Password.getEditText().getText().toString();
                    login_auth_user.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {

                                        if (email.trim().matches("admin@gmail.com")) {
                                            //Intent intent = new Intent(Login.this, AdminHome.class);
                                            //startActivity(intent);
                                            finish();
                                        } else {
                                            Intent intent = new Intent(Login.this, DeviceList.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    } else {
                                        Toast toast = Toast.makeText(Login.this, "Incorrect Input", Toast.LENGTH_LONG); // initiate the Toast with context, message and duration for the Toast
                                        toast.show(); // display the Toast
                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast toast = Toast.makeText(Login.this, "Sorry some error occur please try again", Toast.LENGTH_LONG); // initiate the Toast with context, message and duration for the Toast
                                    toast.show(); // display the Toast
                                }
                            });
                }
            }
        });
    }

    //Varification
    private boolean isEmpty(com.google.android.material.textfield.TextInputLayout obj) {
        CharSequence str = obj.getEditText().getText().toString();
        return TextUtils.isEmpty(str);
    }
    private boolean isEmail(com.google.android.material.textfield.TextInputLayout text) {
        CharSequence email = text.getEditText().getText().toString();
        return (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }
    private boolean checkDataEntered() {
        if (isEmpty(this.login_email)) {
            this.login_email.setError("email is required!");
            return false;
        }
        else if(isEmail(this.login_email)==false){
            this.login_email.setError("invalid email");
            return false;
        }
        else{
            this.login_email.setErrorEnabled(false);
        }
        if (isEmpty(this.login_Password)) {
            this.login_Password.setError("password is required!");
            return false;
        }
        else if(this.login_Password.getEditText().getText().length()>6){
            this.login_Password.setError("password size minimum 6 character!");
            return false;
        }
        else{
            this.login_Password.setErrorEnabled(false);
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        finish();
    }

}