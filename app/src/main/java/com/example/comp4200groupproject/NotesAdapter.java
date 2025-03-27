package com.example.comp4200groupproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder> {
    private List<Note> displayList;
    private DatabaseHelper dbHelper;
    private NotesActivity activity;

    public NotesAdapter(List<Note> notesList, DatabaseHelper dbHelper, NotesActivity activity) {
        this.displayList = new ArrayList<>(notesList);
        this.dbHelper = dbHelper;
        this.activity = activity;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_note_tile, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note note = displayList.get(position);
        holder.textViewNoteTile.setText(note.getContent());

        // Use the styled dialog from NotesActivity when clicked
        holder.itemView.setOnLongClickListener(v -> {
            activity.showEditNoteDialog(note);   // Use NotesActivity's dialog
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return displayList.size();
    }


    static class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView textViewNoteTile;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewNoteTile = itemView.findViewById(R.id.textViewNoteTile);
        }
    }
}
