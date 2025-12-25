package com.example.final_pj;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.final_pj.data.AppDatabase;
import com.example.final_pj.data.MarketComment;
import com.example.final_pj.data.MarketPost;
import com.example.final_pj.ui.CommentAdapter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Executors;

public class PostDetailActivity extends AppCompatActivity {

    private int postId;
    private TextView tvTitle, tvContent, tvUser, tvTime;
    private RecyclerView rvComments;
    private EditText etCommentInput;
    private Button btnSend;
    private CommentAdapter adapter;
    private AppDatabase db;
    private int replyingToId = 0; // 0 for root comment

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        postId = getIntent().getIntExtra("post_id", -1);
        if (postId == -1) {
            finish();
            return;
        }

        db = AppDatabase.getInstance(this);

        tvTitle = findViewById(R.id.tv_detail_title);
        tvContent = findViewById(R.id.tv_detail_content);
        tvUser = findViewById(R.id.tv_detail_user);
        tvTime = findViewById(R.id.tv_detail_time);
        rvComments = findViewById(R.id.rv_comments);
        etCommentInput = findViewById(R.id.et_comment_input);
        btnSend = findViewById(R.id.btn_send_comment);

        rvComments.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CommentAdapter();
        rvComments.setAdapter(adapter);

        adapter.setOnReplyClickListener(comment -> {
            replyingToId = comment.getId();
            etCommentInput.setHint("回复 " + comment.getUsername() + ":");
            etCommentInput.requestFocus();
        });

        btnSend.setOnClickListener(v -> sendComment());

        loadPostDetails();
    }

    private void loadPostDetails() {
        Executors.newSingleThreadExecutor().execute(() -> {
            MarketPost post = db.marketDao().getPostById(postId);
            List<MarketComment> comments = db.marketDao().getCommentsForPost(postId);

            runOnUiThread(() -> {
                if (post != null) {
                    tvTitle.setText(post.getTitle());
                    tvContent.setText(post.getContent());
                    tvUser.setText(post.getUsername());
                    tvTime.setText(post.getPostTime());
                }
                processAndDisplayComments(comments);
            });
        });
    }

    private void processAndDisplayComments(List<MarketComment> rawComments) {
        // Build a simple tree or flattened list with depth
        // Strategy: 
        // 1. Find all roots (parentId == 0)
        // 2. For each root, find its children
        // 3. Flatten into List<CommentNode>

        List<CommentAdapter.CommentNode> nodeList = new ArrayList<>();
        if (rawComments == null) return;

        // Map for quick lookup of children
        Map<Integer, List<MarketComment>> childrenMap = new HashMap<>();
        List<MarketComment> roots = new ArrayList<>();

        for (MarketComment c : rawComments) {
            if (c.getParentId() == 0) {
                roots.add(c);
            } else {
                if (!childrenMap.containsKey(c.getParentId())) {
                    childrenMap.put(c.getParentId(), new ArrayList<>());
                }
                childrenMap.get(c.getParentId()).add(c);
            }
        }

        for (MarketComment root : roots) {
            nodeList.add(new CommentAdapter.CommentNode(root, 0));
            addChildrenRecursive(root.getId(), childrenMap, nodeList, 1);
        }

        adapter.setComments(nodeList);
    }

    private void addChildrenRecursive(int parentId, Map<Integer, List<MarketComment>> childrenMap, 
                                      List<CommentAdapter.CommentNode> nodeList, int depth) {
        if (childrenMap.containsKey(parentId)) {
            for (MarketComment child : childrenMap.get(parentId)) {
                nodeList.add(new CommentAdapter.CommentNode(child, depth));
                // Limit nesting visual depth to avoid too much indentation, but logic supports recursion
                // If we want to cap visual indentation at 1, we can pass min(depth + 1, max_depth)
                addChildrenRecursive(child.getId(), childrenMap, nodeList, depth + 1);
            }
        }
    }

    private void sendComment() {
        String content = etCommentInput.getText().toString().trim();
        if (content.isEmpty()) return;

        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        String username = prefs.getString("username", "匿名用户");
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date());

        MarketComment comment = new MarketComment(postId, replyingToId, username, content, time);

        Executors.newSingleThreadExecutor().execute(() -> {
            db.marketDao().insertComment(comment);
            runOnUiThread(() -> {
                etCommentInput.setText("");
                etCommentInput.setHint("写下你的评论...");
                replyingToId = 0; // Reset to root comment
                loadPostDetails(); // Reload to show new comment
                Toast.makeText(PostDetailActivity.this, "评论已发送", Toast.LENGTH_SHORT).show();
            });
        });
    }
}
