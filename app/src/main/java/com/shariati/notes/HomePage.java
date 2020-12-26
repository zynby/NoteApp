package com.shariati.notes;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.shariati.notes.R;
import com.shariati.notes.adapter.NoteAdapter;
import com.shariati.notes.model.Note;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class HomePage extends AppCompatActivity implements NoteAdapter.OnRecyclerItemClick, NoteAdapter.OnRecyclerItemClickDelete {
    private FloatingActionButton addNote;
    private RecyclerView recyclerView;
    private NoteAdapter noteAdapter;
    private List<Note> mNote;
    private FirebaseAuth mAuth;

    private EditText titleEdit;
    private EditText noteEdit;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        recyclerView = findViewById(R.id.recycleNote);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        mNote = new ArrayList<>();
        noteAdapter = new NoteAdapter(this, mNote);
        recyclerView.setAdapter(noteAdapter);

        Toast.makeText(HomePage.this, "Data is loading...", Toast.LENGTH_SHORT).show();
        progressDialog.show();
        readNote();

        noteAdapter.setOnRecyclerItemClick(this);
        noteAdapter.setOnRecyclerItemClickDelete(this);

        addNote = findViewById(R.id.addNote);
        addNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomePage.this, CreateNoteActivity.class));
            }
        });

    }

    @Override
    public void onClick(Note note) {
        showAlertDialogEdit(note.getTitle(), note.getNote(), note.getCreation_date());
    }

    @Override
    public void onClickDelete(Note note, int pos) {
        DatabaseReference ref = FirebaseDatabase.getInstance("https://nedaprj-315a0-default-rtdb.europe-west1.firebasedatabase.app/").getReference();
        Query noteQuery = ref.child("Note").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                .orderByChild("creation_date").equalTo(note.getCreation_date());

        noteQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    snapshot.getRef().removeValue();
                    Toast.makeText(HomePage.this, "note delete", Toast.LENGTH_SHORT).show();
                }
                noteAdapter.notifyDataSetChanged();
                readNote();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("NoteAdapter", "onCancelledDeleteNote", databaseError.toException());
            }
        });
    }

    private void readNote() {
        final DatabaseReference reference = FirebaseDatabase.getInstance("https://nedaprj-315a0-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("Note").child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid());
        mNote.clear();
        progressDialog.dismiss();
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                String note = Objects.requireNonNull(snapshot.child("note").getValue()).toString();
                Long creation_date = (Long) snapshot.child("creation_date").getValue();
                String title = Objects.requireNonNull(snapshot.child("title").getValue()).toString();
                Note myNote = new Note(title, note, creation_date);
                mNote.add(myNote);
                noteAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }


    public void showAlertDialogEdit(String title, String note, final Long creation_date) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Note");

        View editView = getLayoutInflater().inflate(R.layout.edit_note, null);
        builder.setView(editView);

        titleEdit = editView.findViewById(R.id.editTextTitle);
        noteEdit = editView.findViewById(R.id.editTextNote);

        titleEdit.setText(title);
        noteEdit.setText(note);

        builder.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                boolean flag = validationFields(noteEdit.getText().toString(), titleEdit.getText().toString());
                if (flag) {
                    editNote(titleEdit.getText().toString(),
                            noteEdit.getText().toString(),
                            System.currentTimeMillis(),
                            creation_date);
                }

            }
        });

        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void editNote(String title, String note, Long creation_date, Long time) {

        progressDialog.setMessage("Please Wait");
        progressDialog.show();


        final HashMap<String, Object> mapNote = new HashMap<>();
        mapNote.put("title", title);
        mapNote.put("note", note);
        mapNote.put("creation_date", creation_date);

        DatabaseReference ref = FirebaseDatabase.getInstance("https://nedaprj-315a0-default-rtdb.europe-west1.firebasedatabase.app/").getReference();
        Query noteQuery = ref.child("Note").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                .orderByChild("creation_date").equalTo(time);

        Log.i("method", "delete");

        noteQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    snapshot.getRef().setValue(mapNote);
                    progressDialog.dismiss();
                    Toast.makeText(HomePage.this, "Update Note Successful", Toast.LENGTH_SHORT).show();
                }
                readNote();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private Boolean validationFields(String txtNote, String txtTitle) {
        boolean flag = true;
        if (txtTitle.isEmpty()) {
            Toast.makeText(HomePage.this, "title is Empty!", Toast.LENGTH_SHORT).show();
            flag = false;
        } else if (txtNote.isEmpty()) {
            Toast.makeText(HomePage.this, "note is Empty!", Toast.LENGTH_SHORT).show();
            flag = false;
        }
        return flag;
    }

}
