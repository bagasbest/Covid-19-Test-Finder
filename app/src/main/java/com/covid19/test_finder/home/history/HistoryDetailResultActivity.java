package com.covid19.test_finder.home.history;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.covid19.test_finder.R;
import com.covid19.test_finder.databinding.ActivityHistoryDetailResultBinding;
import com.covid19.test_finder.home.home.order.OrderActivity;
import com.covid19.test_finder.home.home.payment.PaymentActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

public class HistoryDetailResultActivity extends AppCompatActivity {

    public static final String EXTRA_RESULT = "result";
    public static final String EXTRA_HISTORY_ID = "historyId";
    private ActivityHistoryDetailResultBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHistoryDetailResultBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.result.setText(getIntent().getStringExtra(EXTRA_RESULT));


        binding.saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                formValidate();
            }
        });

        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    private void formValidate() {
        String result = binding.result.getText().toString();

        if(result.isEmpty()) {
            Toast.makeText(HistoryDetailResultActivity.this, "Result must be filled!", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseFirestore
                .getInstance()
                .collection("history")
                .document(getIntent().getStringExtra(EXTRA_HISTORY_ID))
                .update("result", result)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            showSuccessDialog();
                        } else {
                            showFailureDialog();
                        }
                    }
                });
    }

    private void showFailureDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Update Result Failure")
                .setMessage("There are something wrong about your internet connection")
                .setIcon(R.drawable.ic_baseline_clear_24)
                .setCancelable(false)
                .setPositiveButton("OK", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                })
                .show();
    }


    private void showSuccessDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Update Result Success")
                .setMessage("Success to update result.")
                .setIcon(R.drawable.ic_baseline_check_circle_outline_24)
                .setCancelable(false)
                .setPositiveButton("OK", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    onBackPressed();
                })
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}