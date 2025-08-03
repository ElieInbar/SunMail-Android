package com.example.sunmail.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sunmail.R;
import com.example.sunmail.model.Label;
import com.example.sunmail.model.Mail;
import com.example.sunmail.util.FlowLayout;
import com.example.sunmail.util.SimpleCallback;
import com.example.sunmail.viewmodel.LabelViewModel;
import com.example.sunmail.viewmodel.MailViewModel;

import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.List;

public class ViewMailActivity extends AppCompatActivity {
    private Mail mail;
    private MailViewModel mailViewModel;
    private LabelViewModel labelViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_mail);

        mail = (Mail) getIntent().getSerializableExtra("mail");
        mailViewModel = new ViewModelProvider(this).get(MailViewModel.class);
        labelViewModel = new ViewModelProvider(this).get(LabelViewModel.class);

        TextView subject = findViewById(R.id.text_subject);
        TextView sender = findViewById(R.id.text_sender);
        TextView body = findViewById(R.id.text_body);
        TextView senderMail = findViewById(R.id.text_sender_email);
        TextView avatar = findViewById(R.id.text_avatar);
        FlowLayout labelContainer = findViewById(R.id.label_container);

        Toolbar toolbar = findViewById(R.id.viewmail_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        toolbar.setNavigationOnClickListener(v -> finish());

        String username = (String) getIntent().getSerializableExtra("senderName");
        String sunmail = username.concat("@sunmail.com");
        if (mail != null) {
            subject.setText(mail.getSubject());
            sender.setText(username);
            body.setText(mail.getBody());
            senderMail.setText(sunmail);
            avatar.setText(username.isEmpty() ? "?" : username.substring(0, 1).toUpperCase());
        }

        mailViewModel.getDeleteResult().observe(this, result -> {
            if ("success".equals(result)) {
                Toast.makeText(this, "Mail deleted", Toast.LENGTH_SHORT).show();
                finish();
            } else if (result != null && !result.equals("")) {
                Toast.makeText(this, "Error: " + result, Toast.LENGTH_LONG).show();
            }
        });

        // Observe ajout / suppression de label
        mailViewModel.getLabelAddStatus().observe(this, status -> {
            if ("success".equals(status)) {
                Toast.makeText(this, "Label added", Toast.LENGTH_SHORT).show();
                mailViewModel.loadLabelsForMail(mail.getId());
            } else if (status != null) {
                Toast.makeText(this, "Error adding label: " + status, Toast.LENGTH_SHORT).show();
            }
        });

        mailViewModel.getLabelRemoveStatus().observe(this, status -> {
            if ("success".equals(status)) {
                Toast.makeText(this, "Label removed", Toast.LENGTH_SHORT).show();
                mailViewModel.loadLabelsForMail(mail.getId());
            } else if (status != null) {
                Toast.makeText(this, "Error removing label: " + status, Toast.LENGTH_SHORT).show();
            }
        });

        mailViewModel.loadLabelsForMail(mail.getId());
        mailViewModel.getMailLabels().observe(this, labels -> {
            labelContainer.removeAllViews();
            if (labels != null && !labels.isEmpty()) {
                for (Label label : labels) {
                    String name = label.getName().toLowerCase();
                    if (name.equals("all") || name.equals("sent")) continue;

                    TextView chip = new TextView(this);
                    chip.setText(label.getName());
                    chip.setTextSize(13);
                    chip.setTextColor(getResources().getColor(android.R.color.white));
                    chip.setBackgroundResource(R.drawable.label_chip_background);
                    chip.setPadding(20, 10, 20, 10);
                    chip.setClickable(true);

                    chip.setOnClickListener(v -> {
                        new AlertDialog.Builder(this)
                                .setTitle("Remove the label ?")
                                .setMessage("Remove the label \"" + label.getName() + "\" of this mail ?")
                                .setPositiveButton("Yes", (d, w) -> {
                                    mailViewModel.removeLabelFromMail(mail.getId(), label.getId());

                                })
                                .setNegativeButton("Cancel", null)
                                .show();
                    });

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.setMarginEnd(16);
                    chip.setLayoutParams(params);
                    labelContainer.addView(chip);
                }
            } else {
                TextView empty = new TextView(this);
                empty.setText("No labels");
                labelContainer.addView(empty);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.action_add_label) {
            showAddLabelDialog();
            return true;
        }

        if (itemId == R.id.action_toggle_star) {
            mailViewModel.addSystemLabelFast(mail.getId(), "starred");
            return true;
        }

        if (itemId == R.id.action_toggle_important) {
            mailViewModel.addSystemLabelFast(mail.getId(), "important");
            return true;
        }

        if (itemId == R.id.action_delete) {
            new AlertDialog.Builder(this)
                    .setTitle("Delete")
                    .setMessage("Delete this Mail")
                    .setPositiveButton("Yes", (dialog, which) -> mailViewModel.deleteMail(mail.getId()))
                    .setNegativeButton("No", null)
                    .show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.viewmail_menu, menu);
        return true;
    }

    private void showAddLabelDialog() {
        mailViewModel.loadLabelsForMail(mail.getId());
        labelViewModel.fetchLabels();

        LiveData<List<Label>> allLabelsLive = labelViewModel.getLabels();
        LiveData<List<Label>> mailLabelsLive = mailViewModel.getMailLabels();
        MediatorLiveData<Boolean> mediator = new MediatorLiveData<>();

        mediator.addSource(allLabelsLive, labels -> {
            if (labels != null && mailLabelsLive.getValue() != null) mediator.setValue(true);
        });

        mediator.addSource(mailLabelsLive, labels -> {
            if (labels != null && allLabelsLive.getValue() != null) mediator.setValue(true);
        });

        mediator.observe(this, ready -> {
            tryShowDialog();
            mediator.removeObservers(this);
        });
    }

    private void tryShowDialog() {
        List<Label> allLabels = labelViewModel.getLabels().getValue();
        List<Label> assignedLabels = mailViewModel.getMailLabels().getValue();

        if (allLabels == null || assignedLabels == null) return;

        List<Label> filtered = new ArrayList<>();
        for (Label label : allLabels) {
            boolean alreadyAssigned = false;
            for (Label assigned : assignedLabels) {
                if (label.getId().equals(assigned.getId())) {
                    alreadyAssigned = true;
                    break;
                }
            }
            if (!alreadyAssigned) filtered.add(label);
        }

        if (filtered.isEmpty()) {
            Toast.makeText(this, "No Label to add", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] labelNames = new String[filtered.size()];
        for (int i = 0; i < filtered.size(); i++) {
            labelNames[i] = filtered.get(i).getName();
        }

        new AlertDialog.Builder(this)
                .setTitle("Add a label")
                .setItems(labelNames, (dialog, which) -> {
                    String labelId = filtered.get(which).getId();
                    mailViewModel.addLabelToMail(mail.getId(), labelId);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
