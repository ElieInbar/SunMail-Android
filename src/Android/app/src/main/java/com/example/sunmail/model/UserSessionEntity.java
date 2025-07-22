package com.example.sunmail.model;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "user_session")
public class UserSessionEntity {
    @PrimaryKey(autoGenerate = true)
    public int id = 1;
    public String token;
    public String userId;
    public String userName;
    public String email;
    public String profilePicture;

    public UserSessionEntity(String token, String userId, String userName, String email, String profilePicture) {
        this.token = token;
        this.userId = userId;
        this.userName = userName;
        this.email = email;
        this.profilePicture = profilePicture;
    }
    @Ignore
    public UserSessionEntity(String token) {
        this(token, null, null, null, null);
    }
}