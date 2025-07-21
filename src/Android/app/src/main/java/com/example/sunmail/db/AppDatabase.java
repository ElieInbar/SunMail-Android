package com.example.sunmail.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.sunmail.model.UserSessionEntity;

@Database(entities = {UserSessionEntity.class}, version = 2)
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserSessionDao userSessionDao();
}