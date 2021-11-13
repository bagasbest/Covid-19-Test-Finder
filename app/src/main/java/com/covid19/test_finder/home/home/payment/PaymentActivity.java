package com.covid19.test_finder.home.home.payment;

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

import com.covid19.test_finder.R;
import com.covid19.test_finder.databinding.ActivityPaymentBinding;
import com.covid19.test_finder.home.HomeActivity;
import com.covid19.test_finder.home.home.place.PlaceModel;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

public class PaymentActivity extends AppCompatActivity {

    public static final String EXTRA_PLACE = "place";
    public static final String EXTRA_DATE = "date";
    public static final String EXTRA_CHECK = "check";
    public static final String EXTRA_PAYMENT = "payment";
    public static final String EXTRA_HISTORY_ID = "timeInMillis";
    private ActivityPaymentBinding binding;
    private String dp;
    private static final int REQUEST_FROM_GALLERY = 1001;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPaymentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        String check = getIntent().getStringExtra(EXTRA_CHECK);
        PlaceModel model = getIntent().getParcelableExtra(EXTRA_PLACE);
        binding.location.setText(model.getLocation());
        binding.dateTime.setText(getIntent().getStringExtra(EXTRA_DATE));
        binding.checkMethod.setText(check);
        binding.paymentMethod.setText(getIntent().getStringExtra(EXTRA_PAYMENT));

        /// number format digunakan untuk money currency, misal IDR. 100.000
        NumberFormat formatter = new DecimalFormat("#,###");
        if(check.equals("SWAB Test")) {
            binding.price.setText("Rp. " + formatter.format(model.getSwab()));
        } else {
            binding.price.setText("Rp. " + formatter.format(model.getPcr()));
        }

        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        // KLIK TAMBAH GAMBAR
        binding.paymentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.with(PaymentActivity.this)
                        .galleryOnly()
                        .compress(1024)
                        .start(REQUEST_FROM_GALLERY);
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
                                    /// update payment proof
                                    updatePaymentProof(mProgressDialog);
                                })
                                .addOnFailureListener(e -> {
                                    mProgressDialog.dismiss();
                                    Toast.makeText(PaymentActivity.this, "Failure upload payment proof", Toast.LENGTH_SHORT).show();
                                    Log.d("imageDp: ", e.toString());
                                }))
                .addOnFailureListener(e -> {
                    mProgressDialog.dismiss();
                    Toast.makeText(PaymentActivity.this, "Failure upload payment proof", Toast.LENGTH_SHORT).show();
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
                .document(getIntent().getStringExtra(EXTRA_HISTORY_ID))
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


    private void showSuccessDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Upload Payment Proof Success")
                .setMessage("You success upload payment proof, admin will be verify your payment proof as soon as possible.\n\nThank You")
                .setIcon(R.drawable.ic_baseline_check_circle_outline_24)
                .setCancelable(false)
                .setPositiveButton("OK", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    Intent intent = new Intent(PaymentActivity.this, HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    dialogInterface.dismiss();
                    startActivity(intent);
                    finish();
                })
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}