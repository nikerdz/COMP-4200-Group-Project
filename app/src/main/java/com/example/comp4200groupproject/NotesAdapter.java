package com.example.comp4200groupproject;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder> {
    private List<String> notesList;
    private NotesManager notesManager;

    public NotesAdapter(List<String> notesList, NotesManager notesManager) {
        this.notesList = notesList;
        this.notesManager = notesManager;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        String note = notesList.get(position);
        holder.textViewNote.setText(note);

        // 单击：编辑笔记
        holder.itemView.setOnClickListener(v -> showEditDialog(v.getContext(), position));

        // 长按：删除笔记
        holder.itemView.setOnLongClickListener(v -> {
            notesManager.deleteNote(position);
            refreshNotes();
            Toast.makeText(v.getContext(), "笔记已删除", Toast.LENGTH_SHORT).show();
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return notesList.size();
    }

    // 显示编辑弹窗
    private void showEditDialog(Context context, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("编辑笔记");

        final EditText input = new EditText(context);
        input.setText(notesList.get(position));
        builder.setView(input);

        builder.setPositiveButton("保存", (dialog, which) -> {
            String newNote = input.getText().toString().trim();
            if (!newNote.isEmpty()) {
                notesManager.editNote(position, newNote);
                refreshNotes();
            } else {
                Toast.makeText(context, "笔记内容不能为空", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("取消", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    // 刷新 RecyclerView 数据
    private void refreshNotes() {
        notesList = notesManager.getNotes();
        notifyDataSetChanged();
    }

    static class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView textViewNote;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewNote = (TextView) itemView.findViewById(android.R.id.text1);
        }
    }
}
