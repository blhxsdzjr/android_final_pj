package com.example.final_pj;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.final_pj.data.AppDatabase;
import com.example.final_pj.data.MarketPost;
import com.example.final_pj.ui.MarketAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.List;
import java.util.concurrent.Executors;

public class MarketActivity extends AppCompatActivity {

    private RecyclerView rvPosts;
    private FloatingActionButton fabAdd;
    private MarketAdapter adapter;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market);

        Toolbar toolbar = findViewById(R.id.toolbar_market);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        rvPosts = findViewById(R.id.rv_market_posts);
        fabAdd = findViewById(R.id.fab_add_post);
        
        db = AppDatabase.getInstance(this);

        rvPosts.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MarketAdapter();
        rvPosts.setAdapter(adapter);

        adapter.setOnPostClickListener(post -> {
            Intent intent = new Intent(MarketActivity.this, PostDetailActivity.class);
            intent.putExtra("post_id", post.getId());
            startActivity(intent);
        });

        fabAdd.setOnClickListener(v -> {
            startActivity(new Intent(MarketActivity.this, AddPostActivity.class));
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPosts();
    }

    private void loadPosts() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<MarketPost> posts = db.marketDao().getAllPosts();
            runOnUiThread(() -> {
                adapter.setPosts(posts);
            });
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
