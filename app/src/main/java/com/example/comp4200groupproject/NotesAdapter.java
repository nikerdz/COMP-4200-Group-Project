package com.example.comp4200groupproject;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder> {
    private List<Note> displayList;
    private DatabaseHelper dbHelper;
    private NotesActivity activity;
    private String searchKeyword = "";

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
        String content = note.getContent();

        if (!searchKeyword.isEmpty()) {
            SpannableString spannable = new SpannableString(content);
            int index = content.toLowerCase().indexOf(searchKeyword.toLowerCase());
            if (index >= 0) {
                spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#4B0082")),  // Deep purple
                        index, index + searchKeyword.length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                holder.textViewNoteTile.setText(spannable);
            } else {
                holder.textViewNoteTile.setText(content);
            }
        } else {
            holder.textViewNoteTile.setText(content);
        }

        // Click to edit
        holder.itemView.setOnLongClickListener(v -> {
            activity.showEditNoteDialog(note);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return displayList.size();
    }

    public void updateList(List<Note> fullList, String query) {
        searchKeyword = query.trim();
        displayList.clear();

        for (Note note : fullList) {
            if (note.getContent().toLowerCase().contains(searchKeyword.toLowerCase())) {
                displayList.add(note);
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
