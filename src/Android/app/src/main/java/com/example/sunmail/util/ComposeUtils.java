package com.example.sunmail.util;

import com.example.sunmail.model.Mail;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ComposeUtils {
    
    /**
     * Format subject for reply email
     */
    public static String formatReplySubject(String originalSubject) {
        if (originalSubject == null || originalSubject.isEmpty()) {
            return "Re: ";
        }
        if (originalSubject.startsWith("Re: ")) {
            return originalSubject;
        }
        return "Re: " + originalSubject;
    }
    
    /**
     * Format subject for forward email
     */
    public static String formatForwardSubject(String originalSubject) {
        if (originalSubject == null || originalSubject.isEmpty()) {
            return "Fwd: ";
        }
        if (originalSubject.startsWith("Fwd: ")) {
            return originalSubject;
        }
        return "Fwd: " + originalSubject;
    }
    
    /**
     * Format body for reply email with original message quoted
     */
    public static String formatReplyBody(Mail originalMail, String senderName) {
        StringBuilder replyBody = new StringBuilder();
        replyBody.append("\n\n");
        replyBody.append("--- Original Message ---\n");
        replyBody.append("From: ").append(senderName != null ? senderName : "Unknown").append("\n");
        replyBody.append("Date: ").append(formatDate(originalMail.getCreatedAt())).append("\n");
        replyBody.append("Subject: ").append(originalMail.getSubject() != null ? originalMail.getSubject() : "No Subject").append("\n\n");
        replyBody.append(originalMail.getBody() != null ? originalMail.getBody() : "");
        
        return replyBody.toString();
    }
    
    /**
     * Format body for forward email with complete original message
     */
    public static String formatForwardBody(Mail originalMail, String senderName, String receiverName) {
        StringBuilder forwardBody = new StringBuilder();
        forwardBody.append("\n\n");
        forwardBody.append("--- Forwarded Message ---\n");
        forwardBody.append("From: ").append(senderName != null ? senderName : "Unknown").append("\n");
        forwardBody.append("To: ").append(receiverName != null ? receiverName : "Unknown").append("\n");
        forwardBody.append("Date: ").append(formatDate(originalMail.getCreatedAt())).append("\n");
        forwardBody.append("Subject: ").append(originalMail.getSubject() != null ? originalMail.getSubject() : "No Subject").append("\n\n");
        forwardBody.append(originalMail.getBody() != null ? originalMail.getBody() : "");
        
        return forwardBody.toString();
    }
    
    /**
     * Format date for display in quoted messages
     */
    private static String formatDate(Date date) {
        if (date == null) {
            return "Unknown date";
        }
        SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy 'at' HH:mm", Locale.getDefault());
        return formatter.format(date);
    }
}
