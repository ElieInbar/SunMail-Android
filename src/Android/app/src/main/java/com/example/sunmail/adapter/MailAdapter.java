package com.example.sunmail.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MailAdapter extends RecyclerView.Adapter<MailAdapter.MailViewHolder> {
    private MailViewModel mailViewModel;
    private String currentLabel;

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
        View view = LayoutInflater.from(parent.getContext())
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
            holder.itemView.setBackgroundColor(0xFFE3F2FD); // bleu clair
            holder.subject.setTypeface(null, Typeface.BOLD);
            holder.sender.setTypeface(null, Typeface.BOLD);
        } else {
            holder.itemView.setBackgroundColor(0xFFFFFFFF); // blanc
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
            String senderName1 = userMap.get(senderId);
            if (senderName1 == null) senderName1 = senderId;
            intent.putExtra("senderName", senderName1);
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
            notifyDataSetChanged(); // Pour rafraîchir l'affichage avec les bons noms
        }
    }
}
