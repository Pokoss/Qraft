package com.lehub.qraft;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import Model.Cart;
import ViewHolder.CartViewHolder;

public class CartActivity extends AppCompatActivity {

    private RecyclerView cartRecyclerView;
    private AppCompatButton goToConfirmOrder;
    private TextView cartIsEmptyText, orderIsPendingText;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    private RecyclerView.LayoutManager layoutManager;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        cartRecyclerView = findViewById(R.id.cartRecyclerView);
        goToConfirmOrder = findViewById(R.id.goToConfirmOrder);
        cartIsEmptyText = findViewById(R.id.cartIsEmptyText);
        orderIsPendingText = findViewById(R.id.orderIsPendingText);

        loadingBar = new ProgressDialog(this);
        loadingBar.setTitle("Loading Cart Items");
        loadingBar.setMessage("Please Wait");
        //loadingBar.setCanceledOnTouchOutside(false);
        //loadingBar.setCancelable(false);
        loadingBar.show();

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        String user = mAuth.getCurrentUser().getUid();

        firestore.collection("Orders").document(user).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.isSuccessful()){

                            DocumentSnapshot snapshot = task.getResult();

                            if (snapshot.exists()) {
                                cartIsEmptyText.setVisibility(View.GONE);
                                orderIsPendingText.setVisibility(View.VISIBLE);
                                loadingBar.dismiss();
                            }
                            else{

                                firestore.collection("Cart List").document("User View").collection(user)
                                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                        if (task.isSuccessful()){

                                            QuerySnapshot snapshot = task.getResult();

                                            if (snapshot.isEmpty()){

                                                cartIsEmptyText.setVisibility(View.VISIBLE);
                                                goToConfirmOrder.setVisibility(View.GONE);
                                                loadingBar.dismiss();

                                            }
                                            else {
                                                goToConfirmOrder.setVisibility(View.VISIBLE);
                                                layoutManager = new LinearLayoutManager(CartActivity.this,LinearLayoutManager.VERTICAL,false);
                                                loadCartItemsToRecyclerView();
                                            }


                                        }
                                        else {

                                            Toast.makeText(CartActivity.this, "something is wrong 1", Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                });

                            }
                        }
                        else {

                            Toast.makeText(CartActivity.this, "Something is wrong", Toast.LENGTH_SHORT).show();

                        }
                    }

                    private void loadCartItemsToRecyclerView() {

                        firestore = FirebaseFirestore.getInstance();
                        mAuth = FirebaseAuth.getInstance();

                        String user = mAuth.getCurrentUser().getUid();

                        Query query = firestore.collection("Cart List").document("User View").collection(user);

                        FirestoreRecyclerOptions<Cart> options = new FirestoreRecyclerOptions.Builder<Cart>()
                                .setQuery(query, Cart.class)
                                .build();

                        FirestoreRecyclerAdapter<Cart, CartViewHolder> adapter;

                        adapter = new FirestoreRecyclerAdapter<Cart, CartViewHolder>(options) {
                            @Override
                            protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull  Cart model) {

                                float total = Float.valueOf(model.getQuantity()) * Float.valueOf(model.getPrice()) ;
                                holder.productNameCart.setText(model.getPname());
                                holder.productQuantityCart.setText("Qty: " + model.getQuantity());
                                holder.productPriceCart.setText("UGX " + model.getPrice());
                                holder.productTotalCart.setText( "Total:  UGX " + String.valueOf(total));
                                Picasso.get().load(model.getImage()).into(holder.cartImage);

                                holder.itemView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        CharSequence[] sequence = new CharSequence[]{"Edit", "Remove"};
                                        AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
                                        builder.setTitle("Cart Options");
                                        builder.setItems(sequence, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                if (which == 0){

                                                    Intent intent = new Intent(CartActivity.this, ProductDetailsActivity.class);
                                                    intent.putExtra("pid", model.getPid());
                                                    intent.putExtra("category", model.getCategory());
                                                    intent.putExtra("productName", model.getPname());
                                                    intent.putExtra("price", model.getPrice());
                                                    startActivity(intent);
                                                }
                                                if (which == 1){

                                                    loadingBar = new ProgressDialog(CartActivity.this);
                                                    loadingBar.setTitle("Removing " + model.getPname() + " from Cart");
                                                    loadingBar.setMessage("Please Wait");
                                                    loadingBar.show();

                                                    firestore.collection("Cart List").document("User View").collection(user).document(model.getPid()).delete()
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {

                                                                    if (task.isSuccessful()){
                                                                        loadingBar.dismiss();
                                                                        Toast.makeText(CartActivity.this, model.getPname() +" removed from cart", Toast.LENGTH_SHORT).show();
                                                                        Intent intent = new Intent(CartActivity.this,CartActivity.class);
                                                                        startActivity(intent);
                                                                    }
                                                                    else {
                                                                        Toast.makeText(CartActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                }
                                                            });
                                                }

                                            }
                                        });
                                        builder.create().show();
                                    }
                                });
                            }

                            @NonNull
                            @Override
                            public CartViewHolder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {

                                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_layout, parent,false);
                                CartViewHolder holder = new CartViewHolder(view);
                                return holder;
                            }
                        };

                        cartRecyclerView.setLayoutManager(layoutManager);
                        cartRecyclerView.setAdapter(adapter);
                        adapter.startListening();
                        loadingBar.dismiss();
                    }
                });
    }
}