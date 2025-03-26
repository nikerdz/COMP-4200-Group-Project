package com.example.comp4200groupproject;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder> {
    private List<Note> originalList;
    private List<Note> displayList;
    private DatabaseHelper dbHelper;

    public NotesAdapter(List<Note> notesList, DatabaseHelper dbHelper) {
        this.originalList = new ArrayList<>(notesList);
        this.displayList = new ArrayList<>(notesList);
        this.dbHelper = dbHelper;
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

        // Edit on single click
        holder.itemView.setOnClickListener(v -> showEditDialog(v.getContext(), note, position));

        // Delete on long press
        holder.itemView.setOnLongClickListener(v -> {
            dbHelper.deleteNote(note.getId());
            originalList.remove(note);
            displayList.remove(note);
            notifyItemRemoved(position);
            Toast.makeText(v.getContext(), "Note deleted", Toast.LENGTH_SHORT).show();
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

    private void showEditDialog(Context context, Note note, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Edit Note");

        final EditText input = new EditText(context);
        input.setText(note.getContent());
        builder.setView(input);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String newContent = input.getText().toString().trim();
            if (!newContent.isEmpty()) {
                dbHelper.updateNote(note.getId(), newContent);
                note.setContent(newContent);
                notifyItemChanged(position);
            } else {
                Toast.makeText(context, "Note cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    static class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView textViewNoteTile;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewNoteTile = itemView.findViewById(R.id.textViewNoteTile);
        }
    }
}
