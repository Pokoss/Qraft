package com.lehub.qraft;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ConfirmOrderActivity extends AppCompatActivity {

    private AppCompatEditText nameConfirmOrderEditText, numberConfirmOrderEditText,
            districtConfirmOrderEditText, addressConfirmOrderEditText;
    private AppCompatButton confirmFinalOrder;
    private TextView priceConfirmOrder;
    private String price;
    private ProgressDialog loadingBar;

    private FirebaseFirestore firestore;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confim_order);

        firestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        nameConfirmOrderEditText = (AppCompatEditText) findViewById(R.id.nameConfirmOrderEditText);
        numberConfirmOrderEditText = (AppCompatEditText) findViewById(R.id.numberConfirmOrderEditText);
        districtConfirmOrderEditText = (AppCompatEditText) findViewById(R.id.districtConfirmOrderEditText);
        addressConfirmOrderEditText = (AppCompatEditText) findViewById(R.id.addressConfirmOrderEditText);
        priceConfirmOrder = (TextView) findViewById(R.id.priceConfirmOrder);
        confirmFinalOrder = (AppCompatButton) findViewById(R.id.confirmFinalOrder);

        price = getIntent().getStringExtra("Total Price");
        Toast.makeText(this, price, Toast.LENGTH_SHORT).show();

        priceConfirmOrder.setText("Total Price: UGX " + price);


        confirmFinalOrder.setOnClickListener(v -> {

            loadingBar = new ProgressDialog(this);
            loadingBar.setTitle("Confirming Order");
            loadingBar.setMessage("Please Wait..");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.setCancelable(false);
            loadingBar.show();

            String name = nameConfirmOrderEditText.getText().toString();
            String number = numberConfirmOrderEditText.getText().toString();
            String district = districtConfirmOrderEditText.getText().toString();
            String address = addressConfirmOrderEditText.getText().toString();

            if (name.isEmpty()){
                nameConfirmOrderEditText.setError("Type a Name");
                loadingBar.dismiss();
            }
            else if (number.isEmpty()){
                numberConfirmOrderEditText.setError("Type a Number");
                loadingBar.dismiss();
            }
            else if (district.isEmpty()){
                districtConfirmOrderEditText.setError("Type the delivery district");
                loadingBar.dismiss();
            }
            else if (address.isEmpty()){
                addressConfirmOrderEditText.setError("Type the address to deliver to");
                loadingBar.dismiss();
            }
            else {

                String uid = mAuth.getCurrentUser().getUid();

                Calendar calendar = Calendar.getInstance();

                String saveCurrentDate = new SimpleDateFormat("MMM dd, yyyy").format(calendar.getTime());
                String saveCurrentTime = new SimpleDateFormat("HH:mm:ss a").format(calendar.getTime());

                HashMap<String, Object> hashMap = new HashMap<>();

                hashMap.put("totalAmount", price );
                hashMap.put("name", name );
                hashMap.put("phone", number );
                hashMap.put("address", address );
                hashMap.put("district", district );
                hashMap.put("date", saveCurrentDate );
                hashMap.put("time", saveCurrentTime );
                hashMap.put("uid", uid );
                hashMap.put("state", "Not Delivered" );

                firestore.collection("Orders").document(uid).set(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        loadingBar.dismiss();
                        Toast.makeText(ConfirmOrderActivity.this, "Order Placed", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ConfirmOrderActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });


            }

        });



    }
}