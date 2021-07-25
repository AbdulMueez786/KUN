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

public class SignUp extends AppCompatActivity {
    private com.google.android.material.textfield.TextInputLayout Signup_Password,Signup_email,Signup_confirm_password;
    private com.google.android.material.button.MaterialButton Signup_button;
    private TextView sign_in_here;

    private FirebaseAuth auth;
   // private DatabaseReference user_ref;
    private FirebaseUser USER;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        auth=FirebaseAuth.getInstance();
        Signup_email=findViewById(R.id.signup_email);
        Signup_Password=findViewById(R.id.signup_password);
        Signup_confirm_password=findViewById(R.id.signup_confirm_password);
        Signup_button=findViewById(R.id.signup_button);
        //user_ref = FirebaseDatabase.getInstance().getReference("users");
        sign_in_here=findViewById(R.id.sign_in_here);
        sign_in_here.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(SignUp.this,Login.class);
                startActivity(intent);
                finish();
            }
        });

        Signup_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String EMAIL=Signup_email.getEditText().getText().toString();
                final String PASSWORD=Signup_Password.getEditText().getText().toString();
                final String CONFIRM_PASSWORD=Signup_confirm_password.getEditText().getText().toString();

                if(checkDataEntered()) {
                    auth.createUserWithEmailAndPassword(EMAIL, PASSWORD)
                            .addOnCompleteListener(SignUp.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {



                                        Toast toast = Toast.makeText(SignUp.this, "Successfully created", Toast.LENGTH_LONG); // initiate the Toast with context, message and duration for the Toast
                                        toast.show(); // display the Toast
                                        USER = FirebaseAuth.getInstance().getCurrentUser();
                                        String id = USER.getUid();


                                        //Intent intent = new Intent(SignUp.this, create_profile.class);
                                        //startActivity(intent);
                                        finish();
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Toast toast = Toast.makeText(SignUp.this, "Already account on this email", Toast.LENGTH_LONG); // initiate the Toast with context, message and duration for the Toast
                                        toast.show(); // display the Toast
                                    }

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast toast = Toast.makeText(SignUp.this, "Already account on this email", Toast.LENGTH_LONG); // initiate the Toast with context, message and duration for the Toast
                                    toast.show(); // display the Toast
                                }
                            });
                }
            }
        });
    }
    //Varification Checks
    private boolean isEmpty(com.google.android.material.textfield.TextInputLayout obj) {
        CharSequence str = obj.getEditText().getText().toString();
        return TextUtils.isEmpty(str);
    }
    private boolean isEmail(com.google.android.material.textfield.TextInputLayout text) {
        CharSequence email = text.getEditText().getText().toString();
        return (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }
    private boolean checkDataEntered() {

        if(isEmpty(this.Signup_email)){
            this.Signup_email.setError("email is required!");
            return false;
        }
        else if(isEmail(this.Signup_email)==false){
            this.Signup_email.setError("invalid email");
            return false;
        }
        else{
            this.Signup_email.setErrorEnabled(false);
        }
        if(isEmpty(this.Signup_Password)){
            this.Signup_Password.setError("password is required!");
            return false;
        }
        else if(this.Signup_Password.getEditText().getText().length()>6){
            this.Signup_Password.setError("password size minimum 6 character!");
            return false;
        }
        else{
            this.Signup_Password.setErrorEnabled(false);
        }
        if(this.Signup_Password.getEditText().getText().toString().matches(this.Signup_confirm_password.getEditText().getText().toString())==false){
            this.Signup_confirm_password.setError("incorrect!");
            return false;
        }
        else{
            this.Signup_confirm_password.setErrorEnabled(false);
        }

        return true;
    }
    @Override
    public void onBackPressed() {
        finish();
    }

}