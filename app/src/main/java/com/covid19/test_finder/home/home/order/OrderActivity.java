package com.covid19.test_finder.home.home.order;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import com.covid19.test_finder.R;
import com.covid19.test_finder.databinding.ActivityOrderBinding;
import com.covid19.test_finder.home.HomeActivity;
import com.covid19.test_finder.home.home.payment.PaymentActivity;
import com.covid19.test_finder.home.home.place.PlaceModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class OrderActivity extends AppCompatActivity {

    public static final String EXTRA_PLACE = "place";
    private ActivityOrderBinding binding;
    private PlaceModel model;
    private String checkMethod;
    private String paymentMethod;
    private String getDateNow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        model = getIntent().getParcelableExtra(EXTRA_PLACE);
        binding.location.setText(model.getLocation());
        binding.address.setText(model.getAddress());

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.method, android.R.layout.simple_list_item_1);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        binding.checkMethod.setAdapter(adapter);
        binding.checkMethod.setOnItemClickListener((adapterView, view, i, l) -> {
            checkMethod = binding.checkMethod.getText().toString();
        });


        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                R.array.payment, android.R.layout.simple_list_item_1);
        // Specify the layout to use when the list of choices appears
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        binding.paymentMethod.setAdapter(adapter2);
        binding.paymentMethod.setOnItemClickListener((adapterView, view, i, l) -> {
            paymentMethod = binding.paymentMethod.getText().toString();
        });


        binding.dateTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaterialDatePicker datePicker = MaterialDatePicker.Builder.datePicker().setTitleText("Choose Order Date").setCalendarConstraints(new CalendarConstraints.Builder().setValidator(DateValidatorPointForward.now()).build()).build();
                datePicker.show(getSupportFragmentManager(), datePicker.toString());
                datePicker.addOnPositiveButtonClickListener(selection -> {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                    getDateNow = sdf.format(new Date(Long.parseLong(selection.toString())));
                    binding.dateTime.setText(getDateNow);
                });
            }
        });


        binding.paymentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                formValidation();
            }
        });

        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    private void formValidation() {
        if (checkMethod == null) {
            Toast.makeText(OrderActivity.this, "Choose Check Method, must be filled", Toast.LENGTH_SHORT).show();
            return;
        } else if (paymentMethod == null) {
            Toast.makeText(OrderActivity.this, "Payment method, must be filled", Toast.LENGTH_SHORT).show();
            return;
        } else if (getDateNow == null) {
            Toast.makeText(OrderActivity.this, "Choose Date Time, must be filled", Toast.LENGTH_SHORT).show();
            return;
        }

        binding.progressBar.setVisibility(View.VISIBLE);

        String timeInMillis = String.valueOf(System.currentTimeMillis());
        String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Map<String, Object> history = new HashMap<>();
        history.put("historyId", timeInMillis);
        history.put("userId", myUid);
        history.put("status", "Not Paid");
        history.put("location", model.getLocation());
        history.put("address", model.getAddress());
        history.put("dateTime", getDateNow);
        history.put("checkMethod", checkMethod);
        history.put("paymentMethod", paymentMethod);
        if (checkMethod.equals("SWAB Test")) {
            history.put("price", model.getSwab());
        } else {
            history.put("price", model.getPcr());
        }
        history.put("phone", model.getPhone());
        history.put("result", "Coming Soon");
        history.put("paymentProof", "");
        history.put("img", model.getImg());

        FirebaseFirestore
                .getInstance()
                .collection("history")
                .document(timeInMillis)
                .set(history)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            binding.progressBar.setVisibility(View.GONE);
                            showSuccessDialog(timeInMillis);
                        } else {
                            binding.progressBar.setVisibility(View.GONE);
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


    private void showSuccessDialog(String timeInMillis) {
        new AlertDialog.Builder(this)
                .setTitle("Order Success")
                .setMessage("You can see your order in History navigation, please click next to pay order or you can go to History navigation later.\n\nThank You.")
                .setIcon(R.drawable.ic_baseline_check_circle_outline_24)
                .setCancelable(false)
                .setPositiveButton("OK", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    Intent intent = new Intent(OrderActivity.this, PaymentActivity.class);
                    intent.putExtra(PaymentActivity.EXTRA_PLACE, model);
                    intent.putExtra(PaymentActivity.EXTRA_DATE, getDateNow);
                    intent.putExtra(PaymentActivity.EXTRA_CHECK, checkMethod);
                    intent.putExtra(PaymentActivity.EXTRA_PAYMENT, paymentMethod);
                    intent.putExtra(PaymentActivity.EXTRA_HISTORY_ID, timeInMillis);
                    startActivity(intent);
                })
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}