package com.covid19.test_finder.home.history;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.covid19.test_finder.R;
import com.covid19.test_finder.databinding.ActivityHistoryDetailBinding;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

public class HistoryDetailActivity extends AppCompatActivity {

    public static final String EXTRA_HISTORY = "history";
    private ActivityHistoryDetailBinding binding;
    private HistoryModel model;
    private String dp;
    private static final int REQUEST_FROM_GALLERY = 1001;

    @Override
    protected void onResume() {
        super.onResume();
        populateHistoryResult();
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHistoryDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        /// get role admin or user
        getRole();

        model = getIntent().getParcelableExtra(EXTRA_HISTORY);
        /// number format digunakan untuk money currency, misal IDR. 100.000
        NumberFormat formatter = new DecimalFormat("#,###");

        binding.status.setText(model.getStatus());
        binding.location.setText(model.getLocation());
        binding.address.setText(model.getAddress());
        binding.dateTime.setText(model.getDateTime());
        binding.checkMethod.setText(model.getCheckMethod());
        binding.price.setText("Rp. " + formatter.format(model.getPrice()));
        binding.phone.setText("Consultant   " + model.getPhone());




        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        binding.imageHint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.with(HistoryDetailActivity.this)
                        .galleryOnly()
                        .compress(1024)
                        .start(REQUEST_FROM_GALLERY);
            }
        });

        binding.acceptPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showConfirmationAcceptPayment();
            }
        });


        binding.deleteHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showConfirmationDeleteHistory();
            }
        });

        binding.imageView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HistoryDetailActivity.this, HistoryDetailResultActivity.class);
                intent.putExtra(HistoryDetailResultActivity.EXTRA_RESULT, binding.result.getText().toString());
                intent.putExtra(HistoryDetailResultActivity.EXTRA_HISTORY_ID, model.getHistoryId());
                startActivity(intent);
            }
        });

    }

    private void populateHistoryResult() {
        FirebaseFirestore
                .getInstance()
                .collection("history")
                .document(model.getHistoryId())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        binding.result.setText("" + documentSnapshot.get("result"));
                    }
                });
    }

    private void showConfirmationDeleteHistory() {
        new AlertDialog.Builder(this)
                .setTitle("Confirm Delete Order History")
                .setMessage("Are you sure, want to delete this order history ?")
                .setIcon(R.drawable.ic_baseline_warning_24)
                .setCancelable(false)
                .setPositiveButton("YES", (dialogInterface, i) -> {
                    deleteHistory();
                    dialogInterface.dismiss();
                })
                .setNegativeButton("NO", null)
                .show();
    }

    private void deleteHistory() {
        FirebaseFirestore
                .getInstance()
                .collection("history")
                .document(model.getHistoryId())
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(HistoryDetailActivity.this, "Success delete this order history", Toast.LENGTH_SHORT).show();
                            onBackPressed();
                        } else {
                            Toast.makeText(HistoryDetailActivity.this, "Failure delete this order history, please check your internet connection", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void showConfirmationAcceptPayment() {
        new AlertDialog.Builder(this)
                .setTitle("Confirm Accept Payment")
                .setMessage("Are you sure, want to accept this order based payment proof below ?")
                .setIcon(R.drawable.ic_baseline_warning_24)
                .setCancelable(false)
                .setPositiveButton("YES", (dialogInterface, i) -> {
                    acceptPayment();
                    dialogInterface.dismiss();
                })
                .setNegativeButton("NO", null)
                .show();
    }

    private void acceptPayment() {
        FirebaseFirestore
                .getInstance()
                .collection("history")
                .document(model.getHistoryId())
                .update("status", "Paid")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(HistoryDetailActivity.this, "Success accept this order", Toast.LENGTH_SHORT).show();
                            binding.acceptPayment.setVisibility(View.GONE);
                            binding.status.setText("Paid");
                        } else {
                            Toast.makeText(HistoryDetailActivity.this, "Failure accept this order, please check your internet connection", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void getRole() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore
                .getInstance()
                .collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(("" + documentSnapshot.get("role")).equals("admin")) {
                            if(!model.getStatus().equals("Paid")) {
                                binding.acceptPayment.setVisibility(View.VISIBLE);
                            }

                            if(!model.getPaymentProof().equals("")) {
                                Glide.with(HistoryDetailActivity.this)
                                        .load(model.getPaymentProof())
                                        .into(binding.paymentProof);
                            }


                            binding.imageView3.setVisibility(View.VISIBLE);

                        } else {
                            if(!model.getStatus().equals("Paid")) {
                                if(model.getPaymentProof().equals("")) {
                                    binding.imageHint.setVisibility(View.VISIBLE);

                                } else {
                                    binding.imageHint.setVisibility(View.VISIBLE);
                                    Glide.with(HistoryDetailActivity.this)
                                            .load(model.getPaymentProof())
                                            .into(binding.paymentProof);
                                }
                            } else {
                                Glide.with(HistoryDetailActivity.this)
                                        .load(model.getPaymentProof())
                                        .into(binding.paymentProof);

                                binding.imageView3.setVisibility(View.VISIBLE);
                            }

                        }
                    }
                });
    }


    /// fungsi untuk memvalidasi kode berdasarkan inisiasi variabel di atas tadi
    @SuppressLint("SetTextI18n")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_FROM_GALLERY) {
                uploadArticleDp(data.getData());
            }
        }
    }


    /// fungsi untuk mengupload foto kedalam cloud storage
    private void uploadArticleDp(Uri data) {
        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
        ProgressDialog mProgressDialog = new ProgressDialog(this);

        mProgressDialog.setMessage("Please wait until process upload complete...");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();
        String imageFileName = "payment/img_" + System.currentTimeMillis() + ".png";

        mStorageRef.child(imageFileName).putFile(data)
                .addOnSuccessListener(taskSnapshot ->
                        mStorageRef.child(imageFileName).getDownloadUrl()
                                .addOnSuccessListener(uri -> {
                                    dp = uri.toString();
                                    Glide.with(this)
                                            .load(dp)
                                            .into(binding.paymentProof);
                                    /// update payment proof
                                    updatePaymentProof(mProgressDialog);
                                })
                                .addOnFailureListener(e -> {
                                    mProgressDialog.dismiss();
                                    Toast.makeText(HistoryDetailActivity.this, "Failure upload payment proof", Toast.LENGTH_SHORT).show();
                                    Log.d("imageDp: ", e.toString());
                                }))
                .addOnFailureListener(e -> {
                    mProgressDialog.dismiss();
                    Toast.makeText(HistoryDetailActivity.this, "Failure upload payment proof", Toast.LENGTH_SHORT).show();
                    Log.d("imageDp: ", e.toString());
                });
    }

    private void updatePaymentProof(ProgressDialog mProgressDialog) {
        Map<String, Object> updateHistory = new HashMap<>();
        updateHistory.put("paymentProof", dp);
        updateHistory.put("status", "Waiting");

        FirebaseFirestore
                .getInstance()
                .collection("history")
                .document(model.getHistoryId())
                .update(updateHistory)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            mProgressDialog.dismiss();
                            showSuccessDialog();
                        } else {
                            mProgressDialog.dismiss();
                            showFailureDialog();
                        }
                    }
                });
    }

    private void showFailureDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Order Failure")
                .setMessage("There are something wrong about your internet connection")
                .setIcon(R.drawable.ic_baseline_clear_24)
                .setCancelable(false)
                .setPositiveButton("OK", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                })
                .show();
    }


    @SuppressLint("SetTextI18n")
    private void showSuccessDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Upload Payment Proof Success")
                .setMessage("You success upload payment proof, admin will be verify your payment proof as soon as possible.\n\nThank You")
                .setIcon(R.drawable.ic_baseline_check_circle_outline_24)
                .setCancelable(false)
                .setPositiveButton("OK", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    binding.status.setText("Waiting");
                })
                .show();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}