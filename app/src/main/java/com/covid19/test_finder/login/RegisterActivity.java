package com.covid19.test_finder.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.covid19.test_finder.R;
import com.covid19.test_finder.databinding.ActivityRegisterBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private String gender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // pilih gender
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.gender, android.R.layout.simple_list_item_1);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        binding.gender.setAdapter(adapter);
        binding.gender.setOnItemClickListener((adapterView, view, i, l) -> {
            gender = binding.gender.getText().toString();
        });

        /// sudah memiliki akun
        binding.login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        /// register
        binding.registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                formValidation();
            }
        });

    }

    private void formValidation() {
        String username = binding.username.getText().toString().trim();
        String password = binding.password.getText().toString().trim();
        String fullName = binding.fullName.getText().toString().trim();
        String phone = binding.phone.getText().toString().trim();
        String email = binding.email.getText().toString().trim();
        String age = binding.age.getText().toString().trim();
        String nik = binding.nik.getText().toString().trim();


        if (username.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "Username must be filled", Toast.LENGTH_SHORT).show();
            return;
        } else if (password.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "Password must be filled", Toast.LENGTH_SHORT).show();
            return;
        } else if (fullName.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "Name must be filled", Toast.LENGTH_SHORT).show();
            return;
        } else if (phone.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "Phone number must be filled", Toast.LENGTH_SHORT).show();
            return;
        } else if (email.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "Email must be filled", Toast.LENGTH_SHORT).show();
            return;
        } else if (age.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "Age must be filled", Toast.LENGTH_SHORT).show();
            return;
        } else if (nik.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "NIK must be filled", Toast.LENGTH_SHORT).show();
            return;
        } else if (gender == null) {
            Toast.makeText(RegisterActivity.this, "Gender must be choose", Toast.LENGTH_SHORT).show();
            return;
        }

        /// create account
        binding.progressBar.setVisibility(View.VISIBLE);
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            /// simpan data ke database
                            saveDataToDatabase(email, password, username, fullName, phone, age, nik);
                        } else {
                            binding.progressBar.setVisibility(View.GONE);
                            showFailureDialog();
                        }
                    }
                });

    }

    private void saveDataToDatabase(String email, String password, String username, String fullName, String phone, String age, String nik) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Map<String, Object> user = new HashMap<>();
        user.put("uid", uid);
        user.put("username", username);
        user.put("phone", phone);
        user.put("age", age);
        user.put("nik", nik);
        user.put("name", fullName);
        user.put("email", email);
        user.put("password", password);
        user.put("gender", gender);
        user.put("role", "user");


        FirebaseFirestore
                .getInstance()
                .collection("users")
                .document(uid)
                .set(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            binding.progressBar.setVisibility(View.GONE);
                            showSuccessDialog();
                        } else {
                            binding.progressBar.setVisibility(View.GONE);
                            showFailureDialog();
                        }
                    }
                });

    }

    /// munculkan dialog ketika gagal registrasi
    private void showFailureDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Failure Registration")
                .setMessage("Please check your internet connection")
                .setIcon(R.drawable.ic_baseline_clear_24)
                .setPositiveButton("OK", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                })
                .show();
    }

    /// munculkan dialog ketika sukses registrasi
    private void showSuccessDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Success Registration")
                .setMessage("Please login")
                .setIcon(R.drawable.ic_baseline_check_circle_outline_24)
                .setPositiveButton("OK", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    onBackPressed();
                })
                .show();
    }


    /// HAPUSKAN ACTIVITY KETIKA SUDAH TIDAK DIGUNAKAN, AGAR MENGURANGI RISIKO MEMORY LEAKS
    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}