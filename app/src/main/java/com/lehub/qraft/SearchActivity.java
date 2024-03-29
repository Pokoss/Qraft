package com.lehub.qraft;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

import Model.Product;
import ViewHolder.ProductViewHolder;

public class SearchActivity extends AppCompatActivity {

    private AppCompatButton searchProductButton;
    private AppCompatEditText getSearchInput;
    private FirebaseFirestore firestore;
    private FirebaseAuth mAuth;
    private String search = "";
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView searchRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchProductButton = findViewById(R.id.searchInputButton);
        getSearchInput = findViewById(R.id.searchInputEditText);

        firestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        searchRecyclerView = findViewById(R.id.recyclerViewSearch);

        layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);

        searchForProduct(search);

        searchProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String searchText = getSearchInput.getText().toString();

                if (searchText.isEmpty()){

                    getSearchInput.setError("Type a product you want to find");
                }
                else {

                    searchForProduct(searchText);
                }

            }


        });
    }

    private void searchForProduct(String searchText) {

        Query query = firestore.collection("Products");

        FirestoreRecyclerOptions<Product> options = new FirestoreRecyclerOptions.Builder<Product>()
                .setQuery(query.orderBy("pname").startAt(searchText), Product.class)
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
                            AlertDialog.Builder builder = new AlertDialog.Builder(SearchActivity.this);
                            builder.setTitle("Please Login to view "  + model.getPname());
                            builder.setItems(sequence, (dialog, which) -> {

                                if (which == 0){

                                    Intent intent = new Intent(SearchActivity.this,LoginActivity.class);
                                    startActivity(intent);
                                }
                                if (which == 1){

                                    dialog.dismiss();
                                }
                            });
                            builder.create().show();
                        }
                        else{

                            Intent intent = new Intent(SearchActivity.this,ProductDetailsActivity.class);
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
        searchRecyclerView.setLayoutManager(layoutManager);
        searchRecyclerView.setAdapter(adapter);
        adapter.startListening();
    }
}