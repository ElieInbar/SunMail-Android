// model/Label.java
package com.example.sunmail.model;

import com.google.gson.annotations.SerializedName;

public class Label {
    private String id;
    private String name;
    @SerializedName("userId")
    private String userId;
    public String getId() { return id; }
    public String getName() { return name; }
    public String getUserId() { return userId; }
}
