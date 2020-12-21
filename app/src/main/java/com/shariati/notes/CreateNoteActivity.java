package com.shariati.notes;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shariati.notes.R;

import java.util.Date;
import java.util.HashMap;

public class CreateNoteActivity extends AppCompatActivity {
    private EditText note;
    private EditText title;
    private FloatingActionButton save;
    private FloatingActionButton cancel;


    private CoordinatorLayout coordinatorLayout;

    ProgressDialog pd;

    private DatabaseReference mRootRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_note_activity);
        note = findViewById(R.id.editTextNote);
        title = findViewById(R.id.editTextTitle);
        save = findViewById(R.id.save);
        cancel = findViewById(R.id.cancel);


        coordinatorLayout = findViewById(R.id.coordinatorLayout);

        pd = new ProgressDialog(this);


        mRootRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CreateNoteActivity.this, HomeActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                finish();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txtTitle = title.getText().toString().trim();
                String txtNote = note.getText().toString().trim();
                Date creationDate = new Date();
                Boolean flag = validationFields(txtNote, txtTitle);
                if (flag) {
                    saveNote(txtTitle, txtNote, creationDate);
                }
            }
        })
        ;}

    private void saveNote(String txtTitle, String txtNote, Date creationDate) {
        pd.setMessage("Please Wait");
        pd.show();
        mRootRef = mRootRef.child("Note");

        HashMap<String, Object> mapNote = new HashMap<>();
        mapNote.put("title", txtTitle);
        mapNote.put("note", txtNote);
        mapNote.put("creation_date", creationDate);
        mapNote.put("publisher", mAuth.getCurrentUser().getUid());
        mRootRef.push().setValue(mapNote).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    pd.dismiss();
                    Toast.makeText(CreateNoteActivity.this, "Save Note Successful", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(CreateNoteActivity.this, HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(CreateNoteActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Boolean validationFields(String txtNote, String txtTitle) {
        Boolean flag = true;
        if (txtTitle.isEmpty()) {
            Toast.makeText(CreateNoteActivity.this, "title is Empty!", Toast.LENGTH_SHORT).show();
            flag = false;
        } else if (txtNote.isEmpty()) {
            Toast.makeText(CreateNoteActivity.this, "note is Empty!", Toast.LENGTH_SHORT).show();
            flag = false;
        }
        return flag;
    }
}
