package com.example.final_pj.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface MarketDao {
    @Insert
    void insertPost(MarketPost post);

    @Insert
    void insertComment(MarketComment comment);

    @Query("SELECT * FROM market_posts ORDER BY id DESC")
    List<MarketPost> getAllPosts();

    @Query("SELECT * FROM market_posts WHERE id = :id LIMIT 1")
    MarketPost getPostById(int id);

    @Query("SELECT * FROM market_comments WHERE postId = :postId ORDER BY id ASC")
    List<MarketComment> getCommentsForPost(int postId);
}
