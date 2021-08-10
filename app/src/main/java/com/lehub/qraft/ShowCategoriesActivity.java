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

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import Model.Category1;
import ViewHolder.Catergory1ViewHolder;

public class ShowCategoriesActivity extends AppCompatActivity {

    private RecyclerView loadCategoriesToRecyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_categories);

        loadCategoriesToRecyclerView = findViewById(R.id.loadCategoryToRecyclerView);

        layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);


        firestore = FirebaseFirestore.getInstance();

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

                        Intent intent = new Intent(ShowCategoriesActivity.this, DisplayCategoryActivity.class);
                        intent.putExtra("category", model.getCname());
                        startActivity(intent);

                    }
                });
            }

            @NonNull
            @Override
            public Catergory1ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.categories_layout, parent,false);
                Catergory1ViewHolder holder = new Catergory1ViewHolder(view);
                return holder;
            }
        };
        loadCategoriesToRecyclerView.setLayoutManager(layoutManager);
        loadCategoriesToRecyclerView.setAdapter(adapter);
        adapter.startListening();
    }
}