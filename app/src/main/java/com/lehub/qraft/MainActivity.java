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
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
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
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.LayoutManager layoutRecentlyAdded, moreItemsLayout;
    RecyclerView.LayoutManager category1Layout;
    RecyclerView.LayoutManager category2Layout;

    private TextView mensFashionHome, goToSearch;

    private RecyclerView loadCategoriesToRecyclerView,category2Recyclerview;
    private RecyclerView recentlyAddedRecyclerView, moreItemsHomeRecyclerView, category1Recyclerview;

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

        mensFashionHome = findViewById(R.id.mensFashionHome);

        category1Recyclerview = findViewById(R.id.category1Recyclerview);

        mensFashionHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });


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

                    Intent intent = new Intent(MainActivity.this,CartActivity.class);
                    startActivity(intent);
                }
                if (id == R.id.nav_search){

                    Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                    startActivity(intent);

                }
                if (id == R.id.nav_categories){

                    Toast.makeText(MainActivity.this, "Categories", Toast.LENGTH_SHORT).show();

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


        loadCategoriesToRecyclerView = findViewById(R.id.loadCategoriesToRecyclerView);
        recentlyAddedRecyclerView = findViewById(R.id.recentlyAddedRecyclerViewHome);
        moreItemsHomeRecyclerView = findViewById(R.id.moreItemsHomeRecyclerView);
        category2Recyclerview = findViewById(R.id.category2Recyclerview);
        goToSearch = findViewById(R.id.goToSearch);

        goToSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });

        layoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        layoutRecentlyAdded = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        moreItemsLayout = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        category1Layout = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        category2Layout = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);



        firestore = FirebaseFirestore.getInstance();

        loadCategoriesToRecyclerViews();
        loadProductsToRecentlyAddedRecyclerView();
        loadProductsToRecyclerView();
        loadProductsToCategory1RecyclerView();
        loadProductsToCategory2RecyclerView();

    }

    private void loadProductsToCategory2RecyclerView() {

        Query query = firestore.collection("Products");

        FirestoreRecyclerOptions<Product> options = new FirestoreRecyclerOptions.Builder<Product>()
                .setQuery(query,Product.class)
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            builder.setTitle("Please Login to view " + model.getPname());
                            builder.setPositiveButton("Ok      ", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                                    startActivity(intent);
                                }
                            });
                            builder.setNegativeButton("Not Now", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    dialog.dismiss();
                                }
                            });
                            builder.create().show();


                        }

                        else {

                            Intent intent = new Intent(MainActivity.this,ProductDetailsActivity.class);
                            intent.putExtra("productName", model.getPname());
                            intent.putExtra("image", model.getImage());
                            intent.putExtra("image1", model.getImage1());
                            intent.putExtra("image2", model.getImage2());
                            intent.putExtra("description", model.getDescription());
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

    private void loadProductsToCategory1RecyclerView() {

        Query query = firestore.collection("Products");

        FirestoreRecyclerOptions<Product> options = new FirestoreRecyclerOptions.Builder<Product>()
                .setQuery(query,Product.class)
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

                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                builder.setTitle("Please Login to view " + model.getPname());
                                builder.setPositiveButton("Ok      ", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                                        startActivity(intent);
                                    }
                                });
                                builder.setNegativeButton("Not Now", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        dialog.dismiss();
                                    }
                                });
                                builder.create().show();


                            }

                            else {

                                Intent intent = new Intent(MainActivity.this,ProductDetailsActivity.class);
                                intent.putExtra("productName", model.getPname());
                                intent.putExtra("image", model.getImage());
                                intent.putExtra("image1", model.getImage1());
                                intent.putExtra("image2", model.getImage2());
                                intent.putExtra("description", model.getDescription());
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

    private void loadProductsToRecyclerView() {

        Query query = firestore.collection("Products");

        FirestoreRecyclerOptions<Product> options = new FirestoreRecyclerOptions.Builder<Product>()
                .setQuery(query, Product.class)
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            builder.setTitle("Please Login to view " + model.getPname());
                            builder.setPositiveButton("Ok      ", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                                    startActivity(intent);
                                }
                            });
                            builder.setNegativeButton("Not Now", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    dialog.dismiss();
                                }
                            });
                            builder.create().show();

                        }
                        else{

                            Intent intent = new Intent(MainActivity.this,ProductDetailsActivity.class);
                            intent.putExtra("productName", model.getPname());
                            intent.putExtra("image", model.getImage());
                            intent.putExtra("image1", model.getImage1());
                            intent.putExtra("image2", model.getImage2());
                            intent.putExtra("description", model.getDescription());
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

    private void loadProductsToRecentlyAddedRecyclerView() {

        Query query = firestore.collection("Products");

        FirestoreRecyclerOptions<Product> options = new FirestoreRecyclerOptions.Builder<Product>()
                .setQuery(query, Product.class)
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            builder.setTitle("Please Login to view " + model.getPname());
                            builder.setPositiveButton("Ok      ", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                                    startActivity(intent);
                                }
                            });
                            builder.setNegativeButton("Not Now", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    dialog.dismiss();
                                }
                            });
                            builder.create().show();

                        }

                        else {

                            Intent intent = new Intent(MainActivity.this,ProductDetailsActivity.class);
                            intent.putExtra("productName", model.getPname());
                            intent.putExtra("image", model.getImage());
                            intent.putExtra("image1", model.getImage1());
                            intent.putExtra("image2", model.getImage2());
                            intent.putExtra("description", model.getDescription());
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
        recentlyAddedRecyclerView.setLayoutManager(layoutRecentlyAdded);
        recentlyAddedRecyclerView.setAdapter(adapter);
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

   /*
    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null){

            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Login for a full experience");
            builder.setPositiveButton("Ok      ", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                    startActivity(intent);
                }
            });
            builder.setNegativeButton("Not Now", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    dialog.dismiss();
                }
            });
            builder.create().show();
        }
    }*/ //onStart comment
}