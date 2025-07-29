// model/LabelRequest.java
package com.example.sunmail.model;

public class LabelRequest {
    private String name;

    public LabelRequest(String name) {
        this.name = name;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
