package com.example.sunmail.util;

public interface SimpleCallback<T> {
    void onSuccess(T data);
    void onError(String message);
}

