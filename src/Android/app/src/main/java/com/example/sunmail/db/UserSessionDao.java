package com.example.sunmail.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.sunmail.model.UserSessionEntity;

@Dao
public interface UserSessionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(UserSessionEntity session);

    @Query("SELECT * FROM user_session LIMIT 1")
    LiveData<UserSessionEntity> getSession();

    @Query("DELETE FROM user_session")
    void clear();
}