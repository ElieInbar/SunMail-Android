package com.example.sunmail.model;

public class UserRegisterForm {
    private String firstName;
    private String lastName;
    private String gender;
    private String birthDate;
    private String userName;
    private String password;
    private String confirmPassword;

    public UserRegisterForm() {
    }

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

    // Getters
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getGender() { return gender; }
    public String getBirthDate() { return birthDate; }
    public String getUserName() { return userName; }
    public String getPassword() { return password; }
    public String getConfirmPassword() { return confirmPassword; }

    // Setters
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
//package com.example.sunmail.model;
//
//import android.content.Context;
//import android.net.Uri;
//
//import okhttp3.MultipartBody;
//import okhttp3.RequestBody;
//
//import java.util.Map;
//
//public class UserRegisterForm {
//    public Uri pictureUri;
//    public String firstName, lastName, userName, gender, birthDate;
//    public String email;
//    public String password, confirmPassword;
//
//    /* Multipart helpers */
//    public Map<String,RequestBody> toRequestMap(Context ctx) {
//        Map<String,RequestBody> map = new java.util.HashMap<>();
//        map.put("first_name", createPartFromString(firstName));
//        map.put("last_name", createPartFromString(lastName));
//        map.put("userName", createPartFromString(userName));
//        map.put("email", createPartFromString(email));
//        map.put("gender", createPartFromString(gender));
//        map.put("birthDate", createPartFromString(birthDate));
//        map.put("password", createPartFromString(password));
//        map.put("confirmPassword", createPartFromString(confirmPassword));
//        return map;
//    }
//    private RequestBody createPartFromString(String value) {
//        return RequestBody.create(okhttp3.MultipartBody.FORM, value == null ? "" : value);
//    }
//    public MultipartBody.Part toImagePart(android.content.Context ctx) {
//        if (pictureUri == null) return null;
//        try {
//            java.io.InputStream is = ctx.getContentResolver().openInputStream(pictureUri);
//            byte[] bytes = new byte[is.available()];
//            is.read(bytes);
//            RequestBody req = RequestBody.create(okhttp3.MediaType.parse("image/*"), bytes);
//            return MultipartBody.Part.createFormData("profilePicture", "profile.jpg", req);
//        } catch (Exception e) { e.printStackTrace(); return null; }
//    }
//}
