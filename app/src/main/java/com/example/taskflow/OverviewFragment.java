package com.example.taskflow;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class OverviewFragment extends ProjectFragment {

    private TextView tvDescription;
    private TextView tvProgressSummary;
    private TextView tvPercent;
    private ProgressBar progressBar;
    private TextView tvTodo;
    private TextView tvInProgress;
    private TextView tvDone;
    private TextView tvOverdue;
    private TextView tvFilesSubtitle;
    private TextView tvDiscussionSubtitle;

    public OverviewFragment() {
        super(R.layout.fragment_overview);
    }

    public static OverviewFragment newInstance(String projectId) {
        OverviewFragment fragment = new OverviewFragment();
        fragment.setArguments(argsFor(projectId));
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvDescription = view.findViewById(R.id.tvOverviewDescription);
        tvProgressSummary = view.findViewById(R.id.tvProgressSummary);
        tvPercent = view.findViewById(R.id.tvProgressPercent);
        progressBar = view.findViewById(R.id.progressOverview);
        tvTodo = view.findViewById(R.id.tvCountTodo);
        tvInProgress = view.findViewById(R.id.tvCountInProgress);
        tvDone = view.findViewById(R.id.tvCountDone);
        tvOverdue = view.findViewById(R.id.tvCountOverdue);
        tvFilesSubtitle = view.findViewById(R.id.tvFilesSubtitle);
        tvDiscussionSubtitle = view.findViewById(R.id.tvDiscussionSubtitle);

        view.findViewById(R.id.btnEditProject).setOnClickListener(v -> editProject());
        view.findViewById(R.id.btnOverviewAddTask).setOnClickListener(v -> addTask());

        // Task Breakdown cards open the Tasks tab with the matching filter.
        view.findViewById(R.id.cardTodo).setOnClickListener(v -> host.showTasksFiltered(1, Constants.DATE_ALL));
        view.findViewById(R.id.cardInProgress).setOnClickListener(v -> host.showTasksFiltered(2, Constants.DATE_ALL));
        view.findViewById(R.id.cardDone).setOnClickListener(v -> host.showTasksFiltered(3, Constants.DATE_ALL));
        view.findViewById(R.id.cardOverdue).setOnClickListener(v -> host.showTasksFiltered(0, Constants.DATE_OVERDUE));

        view.findViewById(R.id.linkAttachments).setOnClickListener(v -> host.selectTab(Constants.TAB_FILES));
        view.findViewById(R.id.linkDiscussion).setOnClickListener(v -> host.selectTab(Constants.TAB_DISCUSSION));

        refresh();
    }

    @Override
    public void refresh() {
        if (getView() == null || host == null) {
            return;
        }
        Project p = project();
        tvDescription.setText(p.getDescription());
        tvProgressSummary.setText(getString(R.string.tasks_completed_format,
                p.getCompletedTaskCount(), p.getTotalTaskCount()));
        tvPercent.setText(getString(R.string.percent_format, p.getProgressPercent()));
        progressBar.setProgress(p.getProgressPercent());

        tvTodo.setText(String.valueOf(p.countByStatus(Task.STATUS_TODO)));
        tvInProgress.setText(String.valueOf(p.countByStatus(Task.STATUS_IN_PROGRESS)));
        tvDone.setText(String.valueOf(p.countByStatus(Task.STATUS_DONE)));
        tvOverdue.setText(String.valueOf(p.getOverdueCount()));

        tvFilesSubtitle.setText(getResources().getQuantityString(
                R.plurals.files_count, p.getAttachmentCount(), p.getAttachmentCount()));
        tvDiscussionSubtitle.setText(getResources().getQuantityString(
                R.plurals.comments_count, p.getCommentCount(), p.getCommentCount()));
    }

    private void editProject() {
        ProjectFormDialog.show(requireContext(), project(), (name, tag, description, attachment) -> {
            project().edit(name, tag, description);
            host.onProjectChanged();
        });
    }

    private void addTask() {
        TaskDialog.show(requireContext(), null, (description, priority, status, dueDate) -> {
            project().addTask(new Task(description, priority, status, dueDate));
            host.onProjectChanged();
        }, null);
    }
}
