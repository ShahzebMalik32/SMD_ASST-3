package com.example.taskflow;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import java.util.List;

public class TaskAdapter extends BaseAdapter {

    public interface OnToggleListener {
        void onToggle(Task task, boolean checked);
    }

    private final Context context;
    private final List<Task> items;
    private final OnToggleListener toggleListener;

    public TaskAdapter(Context context, List<Task> items, OnToggleListener toggleListener) {
        this.context = context;
        this.items = items;
        this.toggleListener = toggleListener;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Task getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private static class Holder {
        CheckBox check;
        TextView description;
        TextView status;
        TextView priority;
        TextView due;
        TextView overdue;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder h;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_task, parent, false);
            h = new Holder();
            h.check = convertView.findViewById(R.id.cbTask);
            h.description = convertView.findViewById(R.id.tvTaskDescription);
            h.status = convertView.findViewById(R.id.tvTaskStatus);
            h.priority = convertView.findViewById(R.id.tvTaskPriority);
            h.due = convertView.findViewById(R.id.tvTaskDue);
            h.overdue = convertView.findViewById(R.id.tvTaskOverdue);
            convertView.setTag(h);
        } else {
            h = (Holder) convertView.getTag();
        }

        Task task = items.get(position);
        boolean done = task.isDone();

        h.check.setChecked(done);
        h.check.setOnClickListener(v -> toggleListener.onToggle(task, h.check.isChecked()));

        h.description.setText(task.getDescription());
        h.description.setTextColor(ContextCompat.getColor(context,
                done ? R.color.text_done : R.color.text_primary));
        if (done) {
            h.description.setPaintFlags(h.description.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            h.description.setPaintFlags(h.description.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
        }

        h.status.setText(task.getStatus());
        if (Task.STATUS_DONE.equals(task.getStatus())) {
            style(h.status, R.drawable.bg_badge_green, R.color.green);
        } else if (Task.STATUS_IN_PROGRESS.equals(task.getStatus())) {
            style(h.status, R.drawable.bg_badge_blue, R.color.accent_blue);
        } else {
            style(h.status, R.drawable.bg_badge_grey, R.color.text_done);
        }

        h.priority.setText(task.getPriority());
        if (Task.PRIORITY_HIGH.equals(task.getPriority())) {
            style(h.priority, R.drawable.bg_badge_red, R.color.red);
        } else if (Task.PRIORITY_NORMAL.equals(task.getPriority())) {
            style(h.priority, R.drawable.bg_badge_orange, R.color.orange);
        } else {
            style(h.priority, R.drawable.bg_badge_grey, R.color.text_done);
        }

        if (task.hasDueDate()) {
            h.due.setText(task.isDueToday()
                    ? context.getString(R.string.due_today)
                    : context.getString(R.string.due_format, Task.formatDate(task.getDueDate())));
        } else {
            h.due.setText(R.string.no_due_date);
        }
        h.overdue.setVisibility(task.isOverdue() ? View.VISIBLE : View.GONE);
        return convertView;
    }

    private void style(TextView view, int backgroundRes, int colorRes) {
        view.setBackgroundResource(backgroundRes);
        view.setTextColor(ContextCompat.getColor(context, colorRes));
    }
}
