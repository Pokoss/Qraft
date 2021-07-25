package com.lehub.qraft;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import Model.Product;
import ViewHolder.ProductViewHolder;

public class ProductDetailsActivity extends AppCompatActivity {

    private TextView productDetailsName,productDetailsPrice,productDetailsDescription;
    private ImageView productDetailsImage, productDetailsImage1, productDetailsImage2;
    private Button productDetailsAddToCart, productDetailsGoTOCart;
    private ElegantNumberButton productDetailsQuantity;
    private RecyclerView similarProductsRecyclerView;
    FirebaseAuth mAuth;

    String state = "Normal";

    RecyclerView.LayoutManager layoutManager;
    private FirebaseFirestore firestore;

    private String pid = "",productName = "", image = "", image1 = "", image2 = "",description = "",price = "", category = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        pid = getIntent().getStringExtra("pid").toString();
        productName = getIntent().getStringExtra("productName").toString();
        image = getIntent().getStringExtra("image").toString();
        image1 = getIntent().getStringExtra("image1").toString();
        image2 = getIntent().getStringExtra("image2").toString();
        description = getIntent().getStringExtra("description").toString();
        price = getIntent().getStringExtra("price").toString();
        category = getIntent().getStringExtra("category").toString();

        productDetailsName = findViewById(R.id.productDetailsName);
        productDetailsPrice = findViewById(R.id.productDetailsPrice);
        productDetailsDescription = findViewById(R.id.productDetailsDescription);
        productDetailsImage = findViewById(R.id.productDetailsImage);
        productDetailsImage1 = findViewById(R.id.productDetailsImage1);
        productDetailsImage2 = findViewById(R.id.productDetailsImage2);
        productDetailsAddToCart = findViewById(R.id.productDetailsAddToCart);
        productDetailsGoTOCart = findViewById(R.id.productDetailsGoToCart);
        productDetailsQuantity = findViewById(R.id.productDetailsQuantity);
        similarProductsRecyclerView = findViewById(R.id.productDetailsRecyclerViewSimilarProducts);

        productDetailsName.setText(productName);
        productDetailsPrice.setText(price);
        productDetailsDescription.setText(description);
        Picasso.get().load(image).into(productDetailsImage);
        Picasso.get().load(image1).into(productDetailsImage1);
        Picasso.get().load(image2).into(productDetailsImage2);

        mAuth = FirebaseAuth.getInstance();

        layoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        firestore = FirebaseFirestore.getInstance();

        loadSimilarProductsToRecyclerView();

        productDetailsAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkOrderState();
            }
        });

    }

    private void checkOrderState() {

        String user = mAuth.getCurrentUser().getUid();

        DocumentReference documentReference = firestore.collection("Orders").document(user);

        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

               if (task.isSuccessful()){

                   DocumentSnapshot snapshot = task.getResult();

                   if (snapshot.exists()) {

                       Toast.makeText(ProductDetailsActivity.this, "You have pending orders, please wait for it to be approved", Toast.LENGTH_SHORT).show();
                   }


               }
               else {

                   addToCart();
               }

            }

            private void addToCart() {

                String quantity = productDetailsQuantity.getNumber().toString();

                String theName = productName;
                String thePrice = price;
                 String theCategory = category;
                String thePid = pid;
                String theQuantity = quantity;
                String theImage = image;

                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("pid", pid);
                hashMap.put("pname",theName );
                hashMap.put("price", price);
                hashMap.put("quantity", theQuantity);
                hashMap.put("image", theImage);
                hashMap.put("category", theCategory);

                firestore.collection("Cart List").document("User View").collection(user).document(pid).set(hashMap)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                Toast.makeText(ProductDetailsActivity.this, theName + " added to cart", Toast.LENGTH_SHORT).show();

                            }
                        });

            }
        });


    }

    private void loadSimilarProductsToRecyclerView() {

        Query query = firestore.collection("Products");

        FirestoreRecyclerOptions<Product> options = new FirestoreRecyclerOptions.Builder<Product>()
                .setQuery(query.whereEqualTo("category",category), Product.class)
                .build();

        FirestoreRecyclerAdapter<Product, ProductViewHolder> adapter;

        adapter = new FirestoreRecyclerAdapter<Product, ProductViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull Product model) {

                holder.productName.setText(model.getPname());
                holder.productPrice.setText("UGX " + model.getPrice());
                Picasso.get().load(model.getImage()).into(holder.productImage);

            }

            @NonNull
            @Override
            public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_layout1, parent,false);
                ProductViewHolder holder = new ProductViewHolder(view);
                return holder;
            }
        };
        similarProductsRecyclerView.setLayoutManager(layoutManager);
        similarProductsRecyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    private void getProductDetails(String pid){

        firestore.collection("Products").document(pid).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        DocumentSnapshot documentSnapshot = task.getResult();

                        if (task.isSuccessful()){
                            String description = documentSnapshot.getString("description");
                        }
                    }
                });
    }
}