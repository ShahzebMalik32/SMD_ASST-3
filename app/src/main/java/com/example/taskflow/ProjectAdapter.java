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

import java.util.Objects;

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
                    return a.getId().equals(b.getId());
                }

                @Override
                public boolean areContentsTheSame(@NonNull Project a, @NonNull Project b) {
                    return Objects.equals(a.getName(), b.getName())
                            && Objects.equals(a.getTag(), b.getTag())
                            && Objects.equals(a.getDescription(), b.getDescription())
                            && a.getCompletedTaskCount() == b.getCompletedTaskCount()
                            && a.getTotalTaskCount() == b.getTotalTaskCount()
                            && a.getProgressPercent() == b.getProgressPercent()
                            && a.getAttachmentCount() == b.getAttachmentCount()
                            && a.getCommentCount() == b.getCommentCount();
                }
            };

    @NonNull
    @Override
    public ProjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_project_card, parent, false);
        return new ProjectViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ProjectViewHolder h, int position) {
        Project p = getItem(position);
        h.tvTag.setText(p.getTag());
        h.tvName.setText(p.getName());
        h.tvDescription.setText(p.getDescription());
        h.tvTaskProgress.setText(p.getCompletedTaskCount() + "/" + p.getTotalTaskCount() + " tasks");
        h.tvPercent.setText(p.getProgressPercent() + "%");
        h.progress.setProgress(p.getProgressPercent());
        h.tvAttachments.setText(String.valueOf(p.getAttachmentCount()));
        h.tvComments.setText(String.valueOf(p.getCommentCount()));
        h.itemView.setOnClickListener(v -> listener.onProjectClick(p));
    }

    static class ProjectViewHolder extends RecyclerView.ViewHolder {
        final TextView tvTag, tvName, tvDescription, tvTaskProgress, tvPercent,
                tvAttachments, tvComments;
        final ProgressBar progress;

        ProjectViewHolder(@NonNull View v) {
            super(v);
            tvTag = v.findViewById(R.id.tvProjectTag);
            tvName = v.findViewById(R.id.tvProjectName);
            tvDescription = v.findViewById(R.id.tvProjectDescription);
            tvTaskProgress = v.findViewById(R.id.tvTaskProgress);
            tvPercent = v.findViewById(R.id.tvPercent);
            progress = v.findViewById(R.id.progressProject);
            tvAttachments = v.findViewById(R.id.tvAttachmentCount);
            tvComments = v.findViewById(R.id.tvCommentCount);
        }
    }
}