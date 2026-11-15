package com.example.taskflow;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

public class ProjectAdapter extends ListAdapter<Project, ProjectAdapter.ProjectViewHolder> {

    public interface OnProjectClickListener {
        void onProjectClick(Project project);
    }

    private final OnProjectClickListener listener;

    public ProjectAdapter(OnProjectClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<Project> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Project>() {
                @Override
                public boolean areItemsTheSame(@NonNull Project a, @NonNull Project b) {
                    return a.getId() == b.getId();
                }

                @Override
                public boolean areContentsTheSame(@NonNull Project a, @NonNull Project b) {
                    return a.getName().equals(b.getName())
                            && a.getTag().equals(b.getTag())
                            && a.getDescription().equals(b.getDescription())
                            && a.getCompletedCount() == b.getCompletedCount()
                            && a.getTotalCount() == b.getTotalCount()
                            && a.getPercent() == b.getPercent()
                            && a.getAttachmentCount() == b.getAttachmentCount()
                            && a.getCommentCount() == b.getCommentCount();
                }
            };

    @NonNull
    @Override
    public ProjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_project, parent, false);
        return new ProjectViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ProjectViewHolder h, int position) {
        Project p = getItem(position);
        h.tvTag.setText(p.getTag());
        h.tvName.setText(p.getName());
        h.tvDescription.setText(p.getDescription());
        h.tvTasks.setText(p.getCompletedCount() + "/" + p.getTotalCount() + " tasks");
        h.tvPercent.setText(p.getPercent() + "%");
        h.progressBar.setProgress(p.getPercent());
        h.tvAttachments.setText(p.getAttachmentCount() + " attachments");
        h.tvComments.setText(p.getCommentCount() + " comments");
        h.itemView.setOnClickListener(v -> listener.onProjectClick(p));
    }

    static class ProjectViewHolder extends RecyclerView.ViewHolder {
        TextView tvTag, tvName, tvDescription, tvTasks, tvPercent, tvAttachments, tvComments;
        ProgressBar progressBar;

        ProjectViewHolder(@NonNull View v) {
            super(v);
            tvTag = v.findViewById(R.id.tvTag);
            tvName = v.findViewById(R.id.tvName);
            tvDescription = v.findViewById(R.id.tvDescription);
            tvTasks = v.findViewById(R.id.tvTasks);
            tvPercent = v.findViewById(R.id.tvPercent);
            progressBar = v.findViewById(R.id.progressBar);
            tvAttachments = v.findViewById(R.id.tvAttachments);
            tvComments = v.findViewById(R.id.tvComments);
        }
    }
}