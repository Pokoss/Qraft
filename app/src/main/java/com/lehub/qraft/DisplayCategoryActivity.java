package com.lehub.qraft;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

import Model.Product;
import ViewHolder.ProductViewHolder;

public class DisplayCategoryActivity extends AppCompatActivity {

    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView displayCategoryProductToRecyclerView;
    private FirebaseFirestore firestore;
    private FirebaseAuth mAuth;
    private TextView appBarCategoryText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_category);

        firestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        String category = getIntent().getStringExtra("category").toString();


        appBarCategoryText = findViewById(R.id.appBarCategoryText);
        displayCategoryProductToRecyclerView = findViewById(R.id.displayCategoryProductToRecyclerView);

        appBarCategoryText.setText(category);

        layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);

        displayCategory(category);

    }

    private void displayCategory(String category) {

        firestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        Query query = firestore.collection("Products");

        FirestoreRecyclerOptions<Product> options = new FirestoreRecyclerOptions.Builder<Product>()
                .setQuery(query.whereEqualTo("category",category), Product.class)
                .build();

        FirestoreRecyclerAdapter<Product, ProductViewHolder> adapter;

        adapter = new FirestoreRecyclerAdapter<Product, ProductViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull Product model) {

                FirebaseUser user = mAuth.getCurrentUser();

                holder.productName.setText(model.getPname());
                holder.productDescription.setText(model.getDescription());
                holder.productPrice.setText("UGX " + model.getPrice());
                Picasso.get().load(model.getImage()).into(holder.productImage);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (user == null){

                            CharSequence[] sequence = new CharSequence[]{"Login", "Not Now"};
                            AlertDialog.Builder builder = new AlertDialog.Builder(DisplayCategoryActivity.this);
                            builder.setTitle("Please Login to view "  + model.getPname());
                            builder.setItems(sequence, (dialog, which) -> {

                                if (which == 0){

                                    Intent intent = new Intent(DisplayCategoryActivity.this,LoginActivity.class);
                                    startActivity(intent);
                                }
                                if (which == 1){

                                    dialog.dismiss();
                                }
                            });
                            builder.create().show();
                        }
                        else{

                            Intent intent = new Intent(DisplayCategoryActivity.this,ProductDetailsActivity.class);
                            intent.putExtra("productName", model.getPname());
                            intent.putExtra("price", model.getPrice());
                            intent.putExtra("category", model.getCategory());
                            intent.putExtra("pid", model.getPid());
                            startActivity(intent);
                        }
                    }
                });
            }

            @NonNull
            @Override
            public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.productlayout2, parent,false);
                ProductViewHolder holder = new ProductViewHolder(view);
                return holder;
            }
        };
        displayCategoryProductToRecyclerView.setLayoutManager(layoutManager);
        displayCategoryProductToRecyclerView.setAdapter(adapter);
        adapter.startListening();
    }
}