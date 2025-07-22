package com.example.sunmail.util;

import androidx.annotation.Nullable;

public class Resource<T> {
    public enum Status { SUCCESS, ERROR, LOADING }
    public final Status status;
    @Nullable public final T data;
    @Nullable public final String error;

    private Resource(Status status, @Nullable T data, @Nullable String error) {
        this.status = status;
        this.data = data;
        this.error = error;
    }

    public static <T> Resource<T> success(@Nullable T data) {
        return new Resource<>(Status.SUCCESS, data, null);
    }
    public static <T> Resource<T> error(String error) {
        return new Resource<>(Status.ERROR, null, error);
    }
    public static <T> Resource<T> loading() {
        return new Resource<>(Status.LOADING, null, null);
    }
}