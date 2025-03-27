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

    private List<Note> originalList;
    private List<Note> displayList;
    private DatabaseHelper dbHelper;
    private NotesActivity activity;  // ✅ Reference to NotesActivity

    public NotesAdapter(List<Note> notesList, DatabaseHelper dbHelper, NotesActivity activity) {
        this.originalList = new ArrayList<>(notesList);
        this.displayList = new ArrayList<>(notesList);
        this.dbHelper = dbHelper;
        this.activity = activity;  // ✅ Store the activity reference
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

    public void updateList(List<Note> fullList, String keyword) {
        this.originalList = new ArrayList<>(fullList);
        this.displayList.clear();

        for (Note note : originalList) {
            if (note.getContent().toLowerCase().contains(keyword.toLowerCase())) {
                this.displayList.add(note);
            }
        }
        notifyDataSetChanged();
    }

    static class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView textViewNoteTile;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewNoteTile = itemView.findViewById(R.id.textViewNoteTile);
        }
    }
}
