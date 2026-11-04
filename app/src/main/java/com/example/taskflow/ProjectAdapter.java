package com.example.taskflow;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

/** Home list rows. Shows the list it is given; the activity owns and filters the data. */
public class ProjectAdapter extends BaseAdapter {

    private final Context context;
    private final List<Project> items;

    public ProjectAdapter(Context context, List<Project> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Project getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private static class Holder {
        TextView tag;
        TextView name;
        TextView description;
        TextView tasks;
        TextView percent;
        ProgressBar progress;
        TextView attachments;
        TextView comments;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder h;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_project_card, parent, false);
            h = new Holder();
            h.tag = convertView.findViewById(R.id.tvProjectTag);
            h.name = convertView.findViewById(R.id.tvProjectName);
            h.description = convertView.findViewById(R.id.tvProjectDescription);
            h.tasks = convertView.findViewById(R.id.tvTaskProgress);
            h.percent = convertView.findViewById(R.id.tvPercent);
            h.progress = convertView.findViewById(R.id.progressProject);
            h.attachments = convertView.findViewById(R.id.tvAttachmentCount);
            h.comments = convertView.findViewById(R.id.tvCommentCount);
            convertView.setTag(h);
        } else {
            h = (Holder) convertView.getTag();
        }

        Project p = items.get(position);
        h.tag.setText(p.getTag());
        h.name.setText(p.getName());
        h.description.setText(p.getDescription());
        h.tasks.setText(context.getString(R.string.task_progress_format,
                p.getCompletedTaskCount(), p.getTotalTaskCount()));
        h.percent.setText(context.getString(R.string.percent_format, p.getProgressPercent()));
        h.progress.setProgress(p.getProgressPercent());
        h.attachments.setText(context.getResources().getQuantityString(
                R.plurals.attachments_count, p.getAttachmentCount(), p.getAttachmentCount()));
        h.comments.setText(context.getResources().getQuantityString(
                R.plurals.comments_count, p.getCommentCount(), p.getCommentCount()));
        return convertView;
    }
}
