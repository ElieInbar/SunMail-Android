package com.example.sunmail.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import com.example.sunmail.viewmodel.MailViewModel;
import com.example.sunmail.R;
import com.example.sunmail.activities.ViewMailActivity;
import com.example.sunmail.model.Mail;
import com.example.sunmail.util.AvatarColorHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MailAdapter extends RecyclerView.Adapter<MailAdapter.MailViewHolder> {
    private MailViewModel mailViewModel;
    private String currentLabel;
    private Context context;

    private List<Mail> mailList;
    private Map<String, String> userMap = new HashMap<>();

    public MailAdapter(List<Mail> mailList, MailViewModel mailViewModel, String label) {
        this.mailList = mailList;
        this.mailViewModel = mailViewModel;
        this.currentLabel = label;
    }

    public void setCurrentLabel(String label) {
        this.currentLabel = label;
    }


    @Override
    public MailViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.context = parent.getContext(); // Store context for later use
        View view = LayoutInflater.from(context)
                .inflate(R.layout.mail_item, parent, false);
        return new MailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MailViewHolder holder, int position) {
        Mail mail = mailList.get(position);

        // Avatar (première lettre du sender)
        String senderId = mail.getSender(); // ou getSenderId(), adapte à ton modèle Mail

        // Récupère le nom depuis la map, sinon fallback sur l'id (jamais null !)
        String senderName = userMap.get(senderId);
        if (senderName == null) senderName = senderId;

        holder.sender.setText(senderName);
        holder.avatar.setText(senderName.isEmpty() ? "?" : senderName.substring(0, 1).toUpperCase());

        // Apply the same color logic as in RegisterActivity
        int avatarColor = getColorForUser(senderName);
        holder.avatar.setBackground(createCircleDrawable(avatarColor));


        holder.subject.setText(mail.getSubject());
        holder.snippet.setText(mail.getSnippet());

//        // Affichage étoile
//        if (mail.isStarred()) {
//            holder.star.setImageResource(R.drawable.ic_star_filled_24); // Mets ici le drawable étoile pleine
//        } else {
//            holder.star.setImageResource(R.drawable.ic_star_border_24); // Mets ici le drawable étoile vide
//        }

        // Apparence "unread"
        if (!mail.isRead()) {
            // For unread mails, keep the CardView background but make text bold
            holder.subject.setTypeface(null, Typeface.BOLD);
            holder.sender.setTypeface(null, Typeface.BOLD);
        } else {
            // For read mails, keep the CardView background and normal text
            holder.subject.setTypeface(null, Typeface.NORMAL);
            holder.sender.setTypeface(null, Typeface.NORMAL);
        }

        holder.itemView.setOnClickListener(v -> {
            if (mail!=null && mailViewModel != null && mail.getId() != null && currentLabel != null) {
                mailViewModel.markMailAsRead(mail.getId(), currentLabel, mail);
            }
            Context context = v.getContext();
            Intent intent = new Intent(context, ViewMailActivity.class);
            intent.putExtra("mail", mail);
            intent.putExtra("currentLabel", currentLabel);

            // Pass sender name
            String senderName1 = userMap.get(senderId);
            if (senderName1 == null) senderName1 = senderId;
            intent.putExtra("senderName", senderName1);

            // For drafts, also pass receiver name
            if ("drafts".equals(currentLabel) && mail.getReceiver() != null) {
                String receiverName = userMap.get(mail.getReceiver());
                if (receiverName == null) {
                    // Fallback: convert ID to email format
                    receiverName = mail.getReceiver() + "@sunmail.com";
                } else if (!receiverName.contains("@")) {
                    // Convert username to email format
                    receiverName = receiverName + "@sunmail.com";
                }
                intent.putExtra("receiverName", receiverName);
            }

            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return mailList.size();
    }

    // ViewHolder enrichi
    static class MailViewHolder extends RecyclerView.ViewHolder {
        TextView sender, subject, snippet, avatar;
        ImageView star;

        MailViewHolder(View itemView) {
            super(itemView);
            sender = itemView.findViewById(R.id.text_sender);
            subject = itemView.findViewById(R.id.text_subject);
            snippet = itemView.findViewById(R.id.text_snippet);
            avatar = itemView.findViewById(R.id.text_avatar);
//            star    = itemView.findViewById(R.id.image_star);
        }
    }

    public void setMailList(List<Mail> mails) {
        this.mailList = mails;
        notifyDataSetChanged();
    }


    public void setUserMap(Map<String, String> userMap) {
        if (userMap != null) {
            this.userMap = userMap;
            notifyDataSetChanged();
        }
    }

    // Same color generation logic as in RegisterActivity
    private int getColorForUser(String userName) {
        return AvatarColorHelper.getColorForUser(context, userName);
    }

    private Drawable createCircleDrawable(int color) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.OVAL);
        drawable.setColor(color);
        return drawable;
    }
}
