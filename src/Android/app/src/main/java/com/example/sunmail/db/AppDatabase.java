package com.example.sunmail.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.sunmail.model.UserSessionEntity;

@Database(entities = {UserSessionEntity.class}, version = 2)
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserSessionDao userSessionDao();
}

//package com.example.sunmail.model;
//
//import android.content.Context;
//
//import androidx.room.Database;
//import androidx.room.Room;
//import androidx.room.RoomDatabase;
//
//import com.example.sunmail.model.UserEntity;
//
//@Database(entities = {UserEntity.class}, version = 1)
//public abstract class AppDatabase extends RoomDatabase {
//    public abstract UserDao userSessionDao();
//
//    private static volatile AppDatabase INSTANCE;
//
//    public static AppDatabase getDatabase(Context context) {
//        if (INSTANCE == null) {
//            synchronized (AppDatabase.class) {
//                if (INSTANCE == null) {
//                    INSTANCE = Room.databaseBuilder(
//                            context.getApplicationContext(),
//                            AppDatabase.class,
//                            "sunmail_db"
//                    ).build();
//                }
//            }
//        }
//        return INSTANCE;
//    }
//}
