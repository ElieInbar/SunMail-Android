package com.example.sunmail.model;

public class UserRegisterForm {
    private String firstName;
    private String lastName;
    private String gender;
    private String birthDate;
    private String userName;
    private String password;
    private String confirmPassword;

    public UserRegisterForm(String firstName, String lastName, String gender,
                            String birthDate, String userName, String password, String confirmPassword) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.birthDate = birthDate;
        this.userName = userName;
        this.password = password;
        this.confirmPassword = confirmPassword;
    }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getGender() { return gender; }
    public String getBirthDate() { return birthDate; }
    public String getUserName() { return userName; }
    public String getPassword() { return password; }
    public String getConfirmPassword() { return confirmPassword; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setGender(String gender) { this.gender = gender; }
    public void setBirthDate(String birthDate) { this.birthDate = birthDate; }
    public void setUserName(String userName) { this.userName = userName; }
    public void setPassword(String password) { this.password = password; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }

    public boolean isValid() {
        return firstName != null && !firstName.trim().isEmpty() &&
                userName != null && !userName.trim().isEmpty() &&
                birthDate != null && !birthDate.trim().isEmpty() &&
                gender != null && !gender.trim().isEmpty() &&
                password != null && !password.trim().isEmpty() &&
                confirmPassword != null && !confirmPassword.trim().isEmpty() &&
                password.equals(confirmPassword);
    }
}