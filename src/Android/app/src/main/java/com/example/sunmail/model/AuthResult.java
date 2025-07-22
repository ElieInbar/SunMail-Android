package com.example.sunmail.model;

public abstract class AuthResult {
    public static class Success extends AuthResult {}
    public static class Error extends AuthResult {
        private final String message;
        public Error(String message) { this.message = message; }
        public String getMessage() { return message; }
    }
}