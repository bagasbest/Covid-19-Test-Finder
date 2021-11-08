package com.covid19.test_finder.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.covid19.test_finder.R;
import com.covid19.test_finder.databinding.ActivityLoginBinding;
import com.covid19.test_finder.home.HomeActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        /// autoLogin
        autoLogin();

        /// klik login
        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                formValidation();
            }
        });


        /// klik register
        binding.registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
    }

    private void autoLogin() {
        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
            finish();
        }
    }

    /// validasi inputan pengguna
    private void formValidation() {
        String email = binding.email.getText().toString().trim();
        String password = binding.password.getText().toString().trim();

        if(email.isEmpty()) {
            Toast.makeText(LoginActivity.this, "Email must be filled", Toast.LENGTH_SHORT).show();
            return;
        } else if (password.isEmpty()) {
            Toast.makeText(LoginActivity.this, "Password must be filled", Toast.LENGTH_SHORT).show();
            return;
        }


        /// cek apakah user sudah terdaftar dalam database atau belum
        binding.progressBar.setVisibility(View.VISIBLE);
        FirebaseAuth
                .getInstance()
                .signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            binding.progressBar.setVisibility(View.GONE);
                            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                            finish();
                        } else {
                            binding.progressBar.setVisibility(View.GONE);
                            showFailureDialog();
                        }
                    }
                });

    }

    /// munculkan dialog ketika gagal login
    private void showFailureDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Login Failure")
                .setMessage("Please check your email or password, and then check your internet connection.")
                .setIcon(R.drawable.ic_baseline_clear_24)
                .setPositiveButton("OK", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                })
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}