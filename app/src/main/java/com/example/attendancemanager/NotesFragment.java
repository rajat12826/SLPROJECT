package com.example.attendancemanager;



import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class NotesFragment extends Fragment {

    private EditText editTextNoteTitle, editTextNoteContent;
    private Button buttonSaveNote;
    private ListView listViewNotes;

    private DatabaseHelper databaseHelper;
    private ArrayList<NotesModel> notesList;
    private NotesAdapter notesAdapter;

    private boolean isUpdateMode = false;
    private int currentNoteId = -1;

    public NotesFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_notes, container, false);

        databaseHelper = new DatabaseHelper(requireContext());

        editTextNoteTitle = view.findViewById(R.id.editTextNoteTitle);
        editTextNoteContent = view.findViewById(R.id.editTextNoteContent);
        buttonSaveNote = view.findViewById(R.id.buttonSaveNote);
        listViewNotes = view.findViewById(R.id.listViewNotes);

        loadNotesData();

        buttonSaveNote.setOnClickListener(v -> {
            String title = editTextNoteTitle.getText().toString().trim();
            String content = editTextNoteContent.getText().toString().trim();

            if (title.isEmpty()) {
                Toast.makeText(getContext(), "Please enter a title", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean success;
            NotesModel note = new NotesModel(
                    isUpdateMode ? currentNoteId : -1,
                    title,
                    content,
                    null
            );

            if (isUpdateMode) {
                success = databaseHelper.updateNote(note);
                if (success) {
                    Toast.makeText(getContext(), "Note updated successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Failed to update note", Toast.LENGTH_SHORT).show();
                }
                isUpdateMode = false;
                currentNoteId = -1;
                buttonSaveNote.setText("Save Note");
            } else {
                success = databaseHelper.insertNote(note);
                if (success) {
                    Toast.makeText(getContext(), "Note added successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Failed to add note", Toast.LENGTH_SHORT).show();
                }
            }

            if (success) {
                editTextNoteTitle.setText("");
                editTextNoteContent.setText("");
                loadNotesData();
            }
        });

        return view;
    }

    private void loadNotesData() {
        try {
            notesList = databaseHelper.getAllNotes();
            notesAdapter = new NotesAdapter(requireContext(), notesList);
            listViewNotes.setAdapter(notesAdapter);
        } catch (Exception e) {
            Toast.makeText(getContext(), "Error loading notes data", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    class NotesAdapter extends ArrayAdapter<NotesModel> {
        private final Context context;
        private final ArrayList<NotesModel> notesList;

        public NotesAdapter(Context context, ArrayList<NotesModel> notesList) {
            super(context, 0, notesList);
            this.context = context;
            this.notesList = notesList;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View listItem = convertView;
            if (listItem == null) {
                listItem = LayoutInflater.from(context).inflate(R.layout.note_item, parent, false);
            }

            NotesModel currentNote = notesList.get(position);

            TextView textViewNoteTitle = listItem.findViewById(R.id.textViewNoteTitle);
            TextView textViewNoteTimestamp = listItem.findViewById(R.id.textViewNoteTimestamp);
            TextView textViewNotePreview = listItem.findViewById(R.id.textViewNotePreview);
            ImageButton buttonEditNote = listItem.findViewById(R.id.buttonEditNote);
            ImageButton buttonDeleteNote = listItem.findViewById(R.id.buttonDeleteNote);

            textViewNoteTitle.setText(currentNote.getTitle());

            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
                Date date = inputFormat.parse(currentNote.getTimestamp());
                textViewNoteTimestamp.setText(date != null ? outputFormat.format(date) : currentNote.getTimestamp());
            } catch (ParseException e) {
                textViewNoteTimestamp.setText(currentNote.getTimestamp());
            }

            textViewNotePreview.setText(currentNote.getContent());

            buttonEditNote.setOnClickListener(v -> {
                editTextNoteTitle.setText(currentNote.getTitle());
                editTextNoteContent.setText(currentNote.getContent());
                isUpdateMode = true;
                currentNoteId = currentNote.getId();
                buttonSaveNote.setText("Update Note");
                editTextNoteTitle.requestFocus();
            });

            buttonDeleteNote.setOnClickListener(v -> {
                boolean success = databaseHelper.deleteNote(currentNote.getId());
                if (success) {
                    Toast.makeText(getContext(), "Note deleted successfully", Toast.LENGTH_SHORT).show();

                    if (isUpdateMode && currentNoteId == currentNote.getId()) {
                        editTextNoteTitle.setText("");
                        editTextNoteContent.setText("");
                        isUpdateMode = false;
                        currentNoteId = -1;
                        buttonSaveNote.setText("Save Note");
                    }

                    loadNotesData();
                } else {
                    Toast.makeText(getContext(), "Failed to delete note", Toast.LENGTH_SHORT).show();
                }
            });

            return listItem;
        }
    }
}
