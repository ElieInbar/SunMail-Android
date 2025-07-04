// fichier : MailAdapter.java
package com.example.sunmail.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.sunmail.R;
import com.example.sunmail.model.Mail;
import java.util.List;

public class MailAdapter extends RecyclerView.Adapter<MailAdapter.MailViewHolder> {

    private List<Mail> mailList;

    public MailAdapter(List<Mail> mailList) {
        this.mailList = mailList;
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
        holder.sender.setText(mail.getSender());
        holder.time.setText(mail.getTime());
        holder.subject.setText(mail.getSubject());
        holder.snippet.setText(mail.getSnippet());
    }

    @Override
    public int getItemCount() {
        return mailList.size();
    }


    public void setMailList(List<Mail> mails) {
        this.mailList = mails;
        notifyDataSetChanged();
    }

    static class MailViewHolder extends RecyclerView.ViewHolder {
        TextView sender, time, subject, snippet;
        MailViewHolder(View itemView) {
            super(itemView);
            sender  = itemView.findViewById(R.id.text_sender);
            time    = itemView.findViewById(R.id.text_time);
            subject = itemView.findViewById(R.id.text_subject);
            snippet = itemView.findViewById(R.id.text_snippet);
        }
    }
}
