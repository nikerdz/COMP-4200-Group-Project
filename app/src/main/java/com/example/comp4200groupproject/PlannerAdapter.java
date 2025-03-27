package com.example.comp4200groupproject;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class PlannerAdapter extends RecyclerView.Adapter<PlannerAdapter.PlannerViewHolder> {

    private List<PlannerEvent> eventList;
    private DatabaseHelper dbHelper;
    private Calendar calendar;

    public PlannerAdapter(List<PlannerEvent> eventList, DatabaseHelper dbHelper) {
        this.eventList = eventList;
        this.dbHelper = dbHelper;
        this.calendar = Calendar.getInstance();
    }

    @NonNull
    @Override
    public PlannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false);
        return new PlannerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlannerViewHolder holder, int position) {
        PlannerEvent event = eventList.get(position);
        holder.EventTitle.setText(event.getTitle());
        holder.EventTime.setText(event.getTime());

        // Long-press to edit and delete event
        holder.itemView.setOnLongClickListener(v -> {
            showEditEventDialog(v.getContext(), event, position);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public static class PlannerViewHolder extends RecyclerView.ViewHolder {
        TextView EventTitle, EventTime;

        public PlannerViewHolder(@NonNull View itemView) {
            super(itemView);
            EventTitle = itemView.findViewById(R.id.eventName);
            EventTime = itemView.findViewById(R.id.eventTime);
        }
    }

    // Method to show the edit/delete dialog
    private void showEditEventDialog(Context context, PlannerEvent event, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        // Inflate the edit_event_dialog layout
        View view = LayoutInflater.from(context).inflate(R.layout.edit_event_dialog, null);
        EditText etEventName = view.findViewById(R.id.et_edit_event_name);
        EditText etEventTime = view.findViewById(R.id.et_edit_event_time);

        etEventName.setText(event.getTitle());
        etEventTime.setText(event.getTime());

        // Handle time picker
        etEventTime.setOnClickListener(v -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(
                    context,
                    (timePicker, hourOfDay, minute) -> {
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);
                        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
                        etEventTime.setText(timeFormat.format(calendar.getTime()));
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    false
            );
            timePickerDialog.show();
        });

        builder.setView(view);
        builder.setTitle("Edit Event");

        // Positive Button: Save changes
        builder.setPositiveButton("Save", (dialog, which) -> {
            String newTitle = etEventName.getText().toString().trim();
            String newTime = etEventTime.getText().toString().trim();

            if (!newTitle.isEmpty() && !newTime.isEmpty()) {
                event.setTitle(newTitle);
                event.setTime(newTime);
                dbHelper.updateEvent(event);  // Update event in database
                notifyItemChanged(position);
                Toast.makeText(context, "Event updated!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            }
        });

        // Neutral Button: Delete event
        builder.setNeutralButton("Delete", (dialog, which) -> {
            dbHelper.deleteEvent(event.getId());  // Delete event from DB
            eventList.remove(position);
            notifyItemRemoved(position);
            Toast.makeText(context, "Event deleted!", Toast.LENGTH_SHORT).show();

        });

        // Negative Button: Cancel
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        // Create and show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();

        // Set background color programmatically
        dialog.getWindow().setBackgroundDrawableResource(R.color.bg);  // Use a light color for visibility

        // Customize button colors
        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        Button neutralButton = dialog.getButton(AlertDialog.BUTTON_NEUTRAL);

        if (positiveButton != null) positiveButton.setTextColor(context.getResources().getColor(R.color.pink));
        if (negativeButton != null) negativeButton.setTextColor(context.getResources().getColor(R.color.pink));
        if (neutralButton != null) neutralButton.setTextColor(context.getResources().getColor(R.color.red));

        if (positiveButton != null) {
            positiveButton.setText("Save");  // Set button text manually
            positiveButton.setAllCaps(false);  // Disable uppercase transformation
        }

        if (negativeButton != null) {
            negativeButton.setText("Cancel");
            negativeButton.setAllCaps(false);  // Disable uppercase transformation
        }

        if (neutralButton != null) {
            neutralButton.setText("Delete");
            neutralButton.setAllCaps(false);  // Disable uppercase transformation
        }
    }
}