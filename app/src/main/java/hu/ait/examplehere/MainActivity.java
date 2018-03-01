package hu.ait.examplehere;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hu.ait.examplehere.R;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.etEmail)
    EditText etEmail;
    @BindView(R.id.etPassword)
    EditText etPassword;

    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        firebaseAuth = FirebaseAuth.getInstance();

        ButterKnife.bind(this);
    }

    @OnClick(R.id.btnRegister)
    public void registerClick() {
        if (!isFormValid()) {
            return;
        }

        showProgressDialog();

        firebaseAuth.createUserWithEmailAndPassword(
                etEmail.getText().toString(), etPassword.getText().toString()
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                hideProgressDialog();

                if (task.isSuccessful()) {
                    FirebaseUser firebaseUser = task.getResult().getUser();
                    firebaseUser.updateProfile(
                            new UserProfileChangeRequest.Builder().
                                    setDisplayName(
                                            userNameFromEmail(
                                                    firebaseUser.getEmail())).build()
                    );

                    Toast.makeText(MainActivity.this, "REG OK",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Failed: "+
                                    task.getException().getLocalizedMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                hideProgressDialog();
                Toast.makeText(MainActivity.this,
                        "error: "+e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void showProgressDialog() {
        if (progressDialog  == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Wait for it...");
        }

        progressDialog.show();
    }

    public void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.hide();
        }
    }

    private boolean isFormValid() {
        if (TextUtils.isEmpty(etEmail.getText().toString())) {
            etEmail.setError("This should not be empty");
            return false;
        }

        if (TextUtils.isEmpty(etPassword.getText().toString())) {
            etPassword.setError("This should not be empty");
            return false;
        }

        return true;
    }

    private String userNameFromEmail(String email) {
        if (email.contains("@")) {
            return email.split("@")[0];
        } else {
            return email;
        }
    }

    @OnClick(R.id.btnLogin)
    public void loginClick() {

        if(!isFormValid()) { //check all fields filled?
            return;
        }

        showProgressDialog();

        firebaseAuth.signInWithEmailAndPassword(
                etEmail.getText().toString(), etPassword.getText().toString()
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                hideProgressDialog();
                if(task.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Login ok", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(MainActivity.this, HomeActivity.class));
                } else {
                    Toast.makeText(MainActivity.this, "Failed: " + task.getException().getLocalizedMessage(),
                            Toast.LENGTH_SHORT).show();

                }
            }
        });
    }
}
