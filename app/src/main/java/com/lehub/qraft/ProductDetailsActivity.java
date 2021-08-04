package com.lehub.qraft;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
    private ImageView productDetailsImage;
    private Button productDetailsAddToCart, productDetailsGoToCart;
    private ElegantNumberButton productDetailsQuantity;
    private RecyclerView similarProductsRecyclerView;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;

    String state = "Normal";

    RecyclerView.LayoutManager layoutManager;
    private FirebaseFirestore firestore;

    private String pid = "",productName = "", image = "",description = "",price = "", category = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        pid = getIntent().getStringExtra("pid").toString();
        productName = getIntent().getStringExtra("productName").toString();
        price = getIntent().getStringExtra("price").toString();
        category = getIntent().getStringExtra("category").toString();

        productDetailsName = findViewById(R.id.productDetailsName);
        productDetailsPrice = findViewById(R.id.productDetailsPrice);
        productDetailsDescription = findViewById(R.id.productDetailsDescription);
        productDetailsImage = findViewById(R.id.productDetailsImage);
        productDetailsAddToCart = findViewById(R.id.productDetailsAddToCart);
        productDetailsGoToCart = findViewById(R.id.productDetailsGoToCart);
        productDetailsQuantity = findViewById(R.id.productDetailsQuantity);
        similarProductsRecyclerView = findViewById(R.id.productDetailsRecyclerViewSimilarProducts);

        productDetailsGoToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = mAuth.getCurrentUser();

                if (user == null){

                    CharSequence[] sequence = new CharSequence[]{"Login", "Not Now"};
                    AlertDialog.Builder builder = new AlertDialog.Builder(ProductDetailsActivity.this);
                    builder.setTitle("Please Login to enable Cart");
                    builder.setItems(sequence, (dialog, which) -> {

                        if (which == 0){

                            Intent intent = new Intent(ProductDetailsActivity.this,LoginActivity.class);
                            startActivity(intent);

                        }
                        if (which == 1){

                            dialog.dismiss();

                        }

                    });
                    builder.create().show();
                }
                else {
                    Intent intent = new Intent(ProductDetailsActivity.this,CartActivity.class);
                    startActivity(intent);
                }
            }
        });

        loadingBar = new ProgressDialog(this);
        loadingBar.setTitle("Loading "+productName + " details");
        loadingBar.setMessage("Please Wait");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.setCancelable(false);
        loadingBar.show();


        productDetailsName.setText(productName);
        productDetailsPrice.setText(price);



        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        firestore.collection("Products").document(pid).get()
                .addOnCompleteListener(task -> {

                    DocumentSnapshot documentSnapshot = task.getResult();

                    if (task.isSuccessful()){
                        String theDescription = documentSnapshot.getString("description");
                        String theImage = documentSnapshot.getString("image");
                        String theImage1 = documentSnapshot.getString("image1");
                        String theImage2 = documentSnapshot.getString("image2");


                        productDetailsDescription.setText(theDescription);
                        Picasso.get().load(theImage).into(productDetailsImage);

                        description = theDescription;
                        image = theImage;

                        loadingBar.dismiss();



                    }
                });

        layoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        firestore = FirebaseFirestore.getInstance();

        loadSimilarProductsToRecyclerView(category);

        productDetailsAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loadingBar = new ProgressDialog(ProductDetailsActivity.this);
                loadingBar.setTitle("Adding "+productName + " to Cart");
                loadingBar.setMessage("Please Wait");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();

                checkOrderState();
            }
        });

    }

    private void checkOrderState() {

        firestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        String user = mAuth.getCurrentUser().getUid();

        firestore.collection("Orders").document(user)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

               if (task.isSuccessful()){

                   DocumentSnapshot snapshot = task.getResult();

                   if (snapshot.exists()) {

                       Toast.makeText(ProductDetailsActivity.this, "You have pending orders, please wait for it to be approved", Toast.LENGTH_SHORT).show();
                       loadingBar.dismiss();
                   }else{

                       addToCart();
                   }

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
                                loadingBar.dismiss();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(ProductDetailsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();

                    }
                });
            }
        });


    }

    private void loadSimilarProductsToRecyclerView(String category) {

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

                holder.productName.setText(model.getPname());
                holder.productPrice.setText("UGX " + model.getPrice());
                Picasso.get().load(model.getImage()).into(holder.productImage);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(ProductDetailsActivity.this,ProductDetailsActivity.class);
                        intent.putExtra("productName", model.getPname());
                        intent.putExtra("price", model.getPrice());
                        intent.putExtra("category", model.getCategory());
                        intent.putExtra("pid", model.getPid());
                        startActivity(intent);

                    }
                });

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
}