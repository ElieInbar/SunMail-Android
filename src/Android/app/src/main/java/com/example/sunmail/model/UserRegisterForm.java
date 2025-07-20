package com.example.sunmail.model;

import android.content.Context;
import android.net.Uri;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import java.util.Map;

public class UserRegisterForm {
    public Uri pictureUri;
    public String firstName, lastName, userName, gender, birthDate;
    public String email;
    public String password, confirmPassword;

    /* Multipart helpers */
    public Map<String,RequestBody> toRequestMap(Context ctx) {
        Map<String,RequestBody> map = new java.util.HashMap<>();
        map.put("first_name", createPartFromString(firstName));
        map.put("last_name", createPartFromString(lastName));
        map.put("userName", createPartFromString(userName));
        map.put("email", createPartFromString(email));
        map.put("gender", createPartFromString(gender));
        map.put("birthDate", createPartFromString(birthDate));
        map.put("password", createPartFromString(password));
        map.put("confirmPassword", createPartFromString(confirmPassword));
        return map;
    }
    private RequestBody createPartFromString(String value) {
        return RequestBody.create(okhttp3.MultipartBody.FORM, value == null ? "" : value);
    }
    public MultipartBody.Part toImagePart(android.content.Context ctx) {
        if (pictureUri == null) return null;
        try {
            java.io.InputStream is = ctx.getContentResolver().openInputStream(pictureUri);
            byte[] bytes = new byte[is.available()];
            is.read(bytes);
            RequestBody req = RequestBody.create(okhttp3.MediaType.parse("image/*"), bytes);
            return MultipartBody.Part.createFormData("profilePicture", "profile.jpg", req);
        } catch (Exception e) { e.printStackTrace(); return null; }
    }
}
