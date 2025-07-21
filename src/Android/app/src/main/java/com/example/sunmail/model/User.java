package com.example.sunmail.model;


import java.util.Date;
import java.util.List;
import java.util.ArrayList;

public class User {
    private String id;
    private String firstName;
    private String lastName;
    private String userName;
    private String email;
    private String gender;
    private Date birthDate;
    private String profilePicture;
    private String password;
    private List<String> labels;
    private String updatedAt;
    private String token;

    public User(String id, String firstName, String lastName, String userName, String email, String gender, Date birthDate, String profilePicture, String password, List<String> labels, String updatedAt, String token) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.email = email;
        this.gender = gender;
        this.birthDate = birthDate;
        this.profilePicture = profilePicture;
        this.password = password;
        this.labels = labels != null ? new ArrayList<>(labels) : new ArrayList<>();
        this.updatedAt = updatedAt;
        this.token = token;
    }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public Date getBirthDate() { return birthDate; }
    public void setBirthDate(Date birthDate) { this.birthDate = birthDate; }
    public String getProfilePicture() { return profilePicture; }
    public void setProfilePicture(String profilePicture) { this.profilePicture = profilePicture; }
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public List<String> getLabels() { return labels; }
    public void setLabels(List<String> labels) { this.labels = labels != null ? new ArrayList<>(labels) : new ArrayList<>(); }
    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }




}
