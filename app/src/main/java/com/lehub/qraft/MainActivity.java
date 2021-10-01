package com.lehub.qraft;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.squareup.picasso.Picasso;


import Model.Category1;
import Model.Product;
import ViewHolder.Catergory1ViewHolder;
import ViewHolder.ProductViewHolder;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    Toolbar toolbar;
    NavigationView navigationView;
    private LinearLayoutManager layoutManager;
    private LinearLayoutManager layoutcategory0, moreItemsLayout;
    private LinearLayoutManager category1Layout;
    private LinearLayoutManager category2Layout;

    private TextView supplier1TextView, showMoreFromSupplier1,supplier2TextView,showMoreFromSupplier2,
            supplier3TextView,showMoreFromSupplier3,goToSearch;

    private String sponsor1, sponsor2, sponsor3, queryField;

    private ProgressDialog loadingBar;

    private RecyclerView loadCategoriesToRecyclerView,category2Recyclerview;
    private RecyclerView category0RecyclerView, moreItemsHomeRecyclerView, category1Recyclerview;

    private FirebaseFirestore firestore;

    SliderView sliderViewHome;
    int[] images = {
            R.drawable.qraft1,
            R.drawable.qraft2,
            R.drawable.qraft3};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sliderViewHome = findViewById(R.id.imageSliderHome);
        supplier1TextView = findViewById(R.id.supplier1TextView);
        category1Recyclerview = findViewById(R.id.category1Recyclerview);

        firestore = FirebaseFirestore.getInstance();

        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(false)
                .build();
        firestore.setFirestoreSettings(settings);

        loadingBar = new ProgressDialog(this);
        loadingBar.setTitle("Loading Page");
        loadingBar.setMessage("Please Wait");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();


        mAuth = FirebaseAuth.getInstance();

        SliderAdapterHome sliderAdapterHome = new SliderAdapterHome(images);
        sliderViewHome.setSliderAdapter(sliderAdapterHome);
        sliderViewHome.setIndicatorAnimation(IndicatorAnimationType.DROP);
        sliderViewHome.setSliderTransformAnimation(SliderAnimations.VERTICALFLIPTRANSFORMATION);
        sliderViewHome.startAutoCycle();

        drawerLayout = findViewById(R.id.drawer);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView = findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int id = item.getItemId();

                if (id == R.id.nav_cart){

                    FirebaseUser user = mAuth.getCurrentUser();

                    if (user == null){

                        CharSequence[] sequence = new CharSequence[]{"Login", "Not Now"};
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle("Please Login to view to enable Cart");
                        builder.setItems(sequence, (dialog, which) -> {

                            if (which == 0){

                                Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                                startActivity(intent);
                            }
                            if (which == 1){

                                dialog.dismiss();
                            }
                        });
                        builder.create().show();
                    }
                    else {
                        Intent intent = new Intent(MainActivity.this,CartActivity.class);
                        startActivity(intent);
                    }
                }
                if (id == R.id.nav_search){

                    Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                    startActivity(intent);
                }
                if (id == R.id.nav_categories){

                    Intent intent = new Intent(MainActivity.this,ShowCategoriesActivity.class);
                    startActivity(intent);
                }
                if (id == R.id.nav_settings){

                    Toast.makeText(MainActivity.this, "Settings", Toast.LENGTH_SHORT).show();

                }
                if (id == R.id.nav_logout){

                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }

                return true;
            }
        });

        FloatingActionButton actionButton = findViewById(R.id.cartFloatingButton);
        actionButton.setOnClickListener(v -> {

            FirebaseUser user = mAuth.getCurrentUser();

            if (user == null){

                CharSequence[] sequence = new CharSequence[]{"Login", "Not Now"};
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Please Login to enable Cart");
                builder.setItems(sequence, (dialog, which) -> {
                    
                    if (which == 0){

                        Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                        startActivity(intent);

                    }
                    if (which == 1){

                        dialog.dismiss();

                    }

                });
                builder.create().show();
            }
            else {
                Intent intent = new Intent(MainActivity.this,CartActivity.class);
                startActivity(intent);
            }
        });


        loadCategoriesToRecyclerView = findViewById(R.id.loadCategoriesToRecyclerView);
        category0RecyclerView = findViewById(R.id.category0RecyclerView);
        moreItemsHomeRecyclerView = findViewById(R.id.moreItemsHomeRecyclerView);
        category2Recyclerview = findViewById(R.id.category2Recyclerview);
        goToSearch = findViewById(R.id.goToSearch);

        supplier1TextView = (TextView) findViewById(R.id.supplier1TextView);
        showMoreFromSupplier1 = (TextView) findViewById(R.id.showMoreFromSupplier1);
        supplier2TextView = (TextView) findViewById(R.id.supplier2TextView);
        showMoreFromSupplier2 = (TextView) findViewById(R.id.showMoreFromSupplier2);
        supplier3TextView = (TextView) findViewById(R.id.supplier3TextView);
        showMoreFromSupplier3 = (TextView) findViewById(R.id.showMoreFromSupplier3);


        firestore.collection("Sponsors").document("sponsor").get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {


                        if (task.isSuccessful()){

                            DocumentSnapshot snapshot = task.getResult();

                            sponsor1 = snapshot.getString("sponsor1");
                            sponsor2 = snapshot.getString("sponsor2");
                            sponsor3 = snapshot.getString("sponsor3");
                            queryField = snapshot.getString("sortBy");

                            supplier1TextView.setText("Qrafts by " + sponsor1);
                            supplier2TextView.setText("Qrafts by " + sponsor2);
                            supplier3TextView.setText("Qrafts by " + sponsor3);

                            loadProductsToCategory2RecyclerView();
                            loadCategoriesToRecyclerViews();
                            loadProductsToCategory0RecyclerView();
                            loadProductsToRecyclerView();
                            loadProductsToCategory1RecyclerView();


                        }
                    }
                });


        showMoreFromSupplier1.setOnClickListener(v -> {

            Intent intent = new Intent(MainActivity.this, SupplierProductsActivity.class);
            intent.putExtra("sponsor", sponsor1);
            intent.putExtra("sortBy", queryField);
            startActivity(intent);
        });

        showMoreFromSupplier2.setOnClickListener(v -> {

            Intent intent = new Intent(MainActivity.this, SupplierProductsActivity.class);
            intent.putExtra("sponsor", sponsor2);
            intent.putExtra("sortBy", queryField);
            startActivity(intent);
        });

        showMoreFromSupplier3.setOnClickListener(v -> {

            Intent intent = new Intent(MainActivity.this, SupplierProductsActivity.class);
            intent.putExtra("sponsor", sponsor3);
            intent.putExtra("sortBy", queryField);
            startActivity(intent);
        });


        goToSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });

        layoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,true);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);

        layoutcategory0 = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,true);
        layoutcategory0.setReverseLayout(true);
        layoutcategory0.setStackFromEnd(true);

        category1Layout = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,true);
        category1Layout.setReverseLayout(true);
        category1Layout.setStackFromEnd(true);

        category2Layout = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,true);
        category2Layout.setReverseLayout(true);
        category2Layout.setStackFromEnd(true);

        moreItemsLayout = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,true);
        moreItemsLayout.setReverseLayout(true);
        moreItemsLayout.setStackFromEnd(true);


    }

    //Third recyclerView
    private void loadProductsToCategory2RecyclerView() {


        Query query = firestore.collection("Products");

        FirestoreRecyclerOptions<Product> options = new FirestoreRecyclerOptions.Builder<Product>()
                .setQuery(query.whereEqualTo(queryField,sponsor3).limit(5),Product.class)
                .build();

        FirestoreRecyclerAdapter<Product, ProductViewHolder> adapter;

        adapter = new FirestoreRecyclerAdapter<Product, ProductViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull Product model) {

                FirebaseUser user = mAuth.getCurrentUser();

                holder.productName.setText(model.getPname());
                holder.productPrice.setText("UGX " + model.getPrice());
                Picasso.get().load(model.getImage()).into(holder.productImage);
                loadingBar.dismiss();

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (user == null){


                            CharSequence[] sequence = new CharSequence[]{"Login", "Not Now"};
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            builder.setTitle("Please Login to view "  + model.getPname());
                            builder.setItems(sequence, (dialog, which) -> {

                                if (which == 0){

                                    Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                                    startActivity(intent);
                                }
                                if (which == 1){

                                    dialog.dismiss();
                                }

                            });
                            builder.create().show();


                        }

                        else {

                            Intent intent = new Intent(MainActivity.this,ProductDetailsActivity.class);
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
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_layout1, parent,false);
                ProductViewHolder holder = new ProductViewHolder(view);
                return holder;
            }

        };

        category2Recyclerview.setLayoutManager(category2Layout);
        category2Recyclerview.setAdapter(adapter);
        adapter.startListening();
    }

    //second recyclerView
    private void loadProductsToCategory1RecyclerView() {

        Query query = firestore.collection("Products");

        FirestoreRecyclerOptions<Product> options = new FirestoreRecyclerOptions.Builder<Product>()
                .setQuery(query.whereEqualTo(queryField,sponsor2).limit(5),Product.class)
                .build();

        FirestoreRecyclerAdapter<Product, ProductViewHolder> adapter;

        adapter = new FirestoreRecyclerAdapter<Product, ProductViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull Product model) {

                FirebaseUser user = mAuth.getCurrentUser();

                holder.productName.setText(model.getPname());
                holder.productPrice.setText("UGX " + model.getPrice());
                Picasso.get().load(model.getImage()).into(holder.productImage);

                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (user == null){

                                CharSequence[] sequence = new CharSequence[]{"Login", "Not Now"};
                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                builder.setTitle("Please Login to view "  + model.getPname());
                                builder.setItems(sequence, (dialog, which) -> {

                                    if (which == 0){

                                        Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                                        startActivity(intent);
                                    }
                                    if (which == 1){

                                        dialog.dismiss();
                                    }
                                });
                                builder.create().show();


                            }

                            else {

                                Intent intent = new Intent(MainActivity.this,ProductDetailsActivity.class);
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
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_layout1, parent,false);
                ProductViewHolder holder = new ProductViewHolder(view);
                return holder;
            }
        };

        category1Recyclerview.setLayoutManager(category1Layout);
        category1Recyclerview.setAdapter(adapter);
        adapter.startListening();
    }

    //more Products RecyclerView
    private void loadProductsToRecyclerView() {


        Query query = firestore.collection("Products");

        FirestoreRecyclerOptions<Product> options = new FirestoreRecyclerOptions.Builder<Product>()
                .setQuery(query.orderBy("date").limitToLast(18), Product.class)
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
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            builder.setTitle("Please Login to view "  + model.getPname());
                            builder.setItems(sequence, (dialog, which) -> {

                                if (which == 0){

                                    Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                                    startActivity(intent);
                                }
                                if (which == 1){

                                    dialog.dismiss();
                                }
                            });
                            builder.create().show();
                        }
                        else{

                            Intent intent = new Intent(MainActivity.this,ProductDetailsActivity.class);
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
        moreItemsHomeRecyclerView.setLayoutManager(moreItemsLayout);
        moreItemsHomeRecyclerView.setAdapter(adapter);
        adapter.startListening();

    }

    //first recyclerView
    private void loadProductsToCategory0RecyclerView() {

        Query query = firestore.collection("Products");

        FirestoreRecyclerOptions<Product> options = new FirestoreRecyclerOptions.Builder<Product>()
                .setQuery(query.whereEqualTo(queryField,sponsor1).limit(5), Product.class)
                .build();

        FirestoreRecyclerAdapter<Product, ProductViewHolder> adapter;

        adapter = new FirestoreRecyclerAdapter<Product, ProductViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull Product model) {

                FirebaseUser user = mAuth.getCurrentUser();

                holder.productName.setText(model.getPname());
                holder.productPrice.setText("UGX " + model.getPrice());
                Picasso.get().load(model.getImage()).into(holder.productImage);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (user == null){

                            CharSequence[] sequence = new CharSequence[]{"Login", "Not Now"};
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            builder.setTitle("Please Login to view "  + model.getPname());
                            builder.setItems(sequence, (dialog, which) -> {

                                if (which == 0){

                                    Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                                    startActivity(intent);
                                }
                                if (which == 1){

                                    dialog.dismiss();
                                }
                            });
                            builder.create().show();
                        }

                        else {

                            Intent intent = new Intent(MainActivity.this,ProductDetailsActivity.class);
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
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_layout1, parent,false);
                ProductViewHolder holder = new ProductViewHolder(view);
                return holder;
            }
        };
        category0RecyclerView.setLayoutManager(layoutcategory0);
        category0RecyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    private void loadCategoriesToRecyclerViews() {

        Query query = firestore.collection("Category");

        FirestoreRecyclerOptions<Category1> options = new FirestoreRecyclerOptions.Builder<Category1>()
                .setQuery(query, Category1.class)
                .build();

        FirestoreRecyclerAdapter<Category1, Catergory1ViewHolder> adapter;

        adapter = new FirestoreRecyclerAdapter<Category1, Catergory1ViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull Catergory1ViewHolder holder, int position, @NonNull Category1 model) {

                holder.textViewCategory1.setText(model.getCname());

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(MainActivity.this, DisplayCategoryActivity.class);
                        intent.putExtra("category", model.getCname());
                        startActivity(intent);

                    }
                });
            }

            @NonNull
            @Override
            public Catergory1ViewHolder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.categories_layout1, parent,false);
                Catergory1ViewHolder holder = new Catergory1ViewHolder(view);
                return holder;
            }
        };
        loadCategoriesToRecyclerView.setLayoutManager(layoutManager);
        loadCategoriesToRecyclerView.setAdapter(adapter);
        adapter.startListening();
    }
}