package com.example.taskflow;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

public class TasksFragment extends ProjectFragment {

    private interface IndexListener {
        void onSelected(int index);
    }

    // Query state. Kept in the fragment (hidden, never destroyed) so it survives tab switches.
    private String query = "";
    private int statusIndex = 0;
    private int priorityIndex = 0;
    private int dateIndex = 0;
    private int sortIndex = 0;

    private final ArrayList<Task> visibleTasks = new ArrayList<>();
    private TaskAdapter adapter;

    private EditText etSearch;
    private Spinner spStatus;
    private Spinner spPriority;
    private Spinner spDate;
    private Spinner spSort;
    private TextView tvShowing;
    private ListView lvTasks;
    private View stateNoTasks;
    private View stateNoMatch;

    public TasksFragment() {
        super(R.layout.fragment_tasks);
    }

    public static TasksFragment newInstance(String projectId) {
        TasksFragment fragment = new TasksFragment();
        fragment.setArguments(argsFor(projectId));
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        etSearch = view.findViewById(R.id.etSearchTasks);
        spStatus = view.findViewById(R.id.spStatusFilter);
        spPriority = view.findViewById(R.id.spPriorityFilter);
        spDate = view.findViewById(R.id.spDateFilter);
        spSort = view.findViewById(R.id.spSort);
        tvShowing = view.findViewById(R.id.tvShowingTasks);
        lvTasks = view.findViewById(R.id.lvTasks);
        stateNoTasks = view.findViewById(R.id.stateNoTasks);
        stateNoMatch = view.findViewById(R.id.stateNoMatch);

        setupSpinner(spStatus, Constants.STATUS_FILTERS, statusIndex, i -> {
            if (i != statusIndex) {
                statusIndex = i;
                refresh();
            }
        });
        setupSpinner(spPriority, Constants.PRIORITY_FILTERS, priorityIndex, i -> {
            if (i != priorityIndex) {
                priorityIndex = i;
                refresh();
            }
        });
        setupSpinner(spDate, Constants.DATE_FILTERS, dateIndex, i -> {
            if (i != dateIndex) {
                dateIndex = i;
                refresh();
            }
        });
        setupSpinner(spSort, Constants.SORT_OPTIONS, sortIndex, i -> {
            if (i != sortIndex) {
                sortIndex = i;
                refresh();
            }
        });

        etSearch.setText(query);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                query = s.toString();
                refresh();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        adapter = new TaskAdapter(requireContext(), visibleTasks, (task, checked) -> {
            task.setStatus(checked ? Task.STATUS_DONE : Task.STATUS_TODO);
            host.onProjectChanged();
        });
        lvTasks.setAdapter(adapter);
        lvTasks.setOnItemClickListener((parent, row, position, id) -> editTask(adapter.getItem(position)));

        view.findViewById(R.id.tvResetFilters).setOnClickListener(v -> resetFilters());
        view.findViewById(R.id.btnResetFiltersState).setOnClickListener(v -> resetFilters());
        view.findViewById(R.id.btnAddTask).setOnClickListener(v -> addTask());
        view.findViewById(R.id.btnAddTaskEmpty).setOnClickListener(v -> addTask());

        refresh();
    }

    private void setupSpinner(Spinner spinner, String[] items, int initial, IndexListener listener) {
        ArrayAdapter<String> arrayAdapter =
                new ArrayAdapter<>(requireContext(), R.layout.item_spinner, items);
        arrayAdapter.setDropDownViewResource(R.layout.item_spinner_dropdown);
        spinner.setAdapter(arrayAdapter);
        spinner.setSelection(initial, false);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
                listener.onSelected(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    // ---------------------------------------------------------------- filters

    /** Restores the five defaults: empty search, All statuses, All priorities, All dates, Due date ascending. */
    private void resetFilters() {
        applyFilters(0, Constants.DATE_ALL);
    }

    /** Resets everything to the defaults, then applies the given status and date filter. */
    public void applyFilters(int statusFilterIndex, int dateFilterIndex) {
        query = "";
        statusIndex = statusFilterIndex;
        priorityIndex = 0;
        dateIndex = dateFilterIndex;
        sortIndex = 0;
        if (getView() != null) {
            etSearch.setText("");
            spStatus.setSelection(statusIndex);
            spPriority.setSelection(priorityIndex);
            spDate.setSelection(dateIndex);
            spSort.setSelection(sortIndex);
            refresh();
        }
    }

    private boolean matches(Task task) {
        String q = query.trim().toLowerCase(Locale.ROOT);
        if (!q.isEmpty() && !task.getDescription().toLowerCase(Locale.ROOT).contains(q)) {
            return false;
        }
        if (statusIndex != 0 && !task.getStatus().equals(Constants.STATUS_FILTERS[statusIndex])) {
            return false;
        }
        if (priorityIndex != 0 && !task.getPriority().equals(Constants.PRIORITY_FILTERS[priorityIndex])) {
            return false;
        }
        switch (dateIndex) {
            case Constants.DATE_TODAY:
                return task.isDueToday();
            case Constants.DATE_OVERDUE:
                return task.isOverdue();
            case Constants.DATE_NONE:
                return !task.hasDueDate();
            default:
                return true;
        }
    }

    private Comparator<Task> comparator() {
        switch (sortIndex) {
            case 1:
                return (a, b) -> compareDue(a, b, false);
            case 2:
                return (a, b) -> {
                    int c = Integer.compare(a.priorityRank(), b.priorityRank());
                    return c != 0 ? c : compareDue(a, b, true);
                };
            case 3:
                return (a, b) -> Long.compare(b.getCreatedAt(), a.getCreatedAt());
            default:
                return (a, b) -> compareDue(a, b, true);
        }
    }

    /** Undated tasks always go last. */
    private static int compareDue(Task a, Task b, boolean ascending) {
        if (!a.hasDueDate() && !b.hasDueDate()) {
            return 0;
        }
        if (!a.hasDueDate()) {
            return 1;
        }
        if (!b.hasDueDate()) {
            return -1;
        }
        int c = Long.compare(a.getDueDate(), b.getDueDate());
        return ascending ? c : -c;
    }

    // ---------------------------------------------------------------- refresh

    @Override
    public void refresh() {
        if (getView() == null || host == null) {
            return;
        }
        Project p = project();
        visibleTasks.clear();
        for (Task task : p.getTasks()) {
            if (matches(task)) {
                visibleTasks.add(task);
            }
        }
        Collections.sort(visibleTasks, comparator());
        adapter.notifyDataSetChanged();

        tvShowing.setText(getString(R.string.showing_tasks_format,
                visibleTasks.size(), p.getTotalTaskCount()));

        boolean noTasks = p.getTotalTaskCount() == 0;
        boolean noMatch = !noTasks && visibleTasks.isEmpty();
        lvTasks.setVisibility(visibleTasks.isEmpty() ? View.GONE : View.VISIBLE);
        stateNoTasks.setVisibility(noTasks ? View.VISIBLE : View.GONE);
        stateNoMatch.setVisibility(noMatch ? View.VISIBLE : View.GONE);
    }

    // ---------------------------------------------------------------- add / edit

    private void addTask() {
        TaskDialog.show(requireContext(), null, (description, priority, status, dueDate) -> {
            project().addTask(new Task(description, priority, status, dueDate));
            host.onProjectChanged();
        }, null);
    }

    private void editTask(Task task) {
        final String taskId = task.getId();
        TaskDialog.show(requireContext(), task, (description, priority, status, dueDate) -> {
            for (Task t : project().getTasks()) {
                if (t.getId().equals(taskId)) {
                    t.update(description, priority, status, dueDate);
                    break;
                }
            }
            host.onProjectChanged();
        }, () -> {
            project().removeTask(taskId);
            host.onProjectChanged();
        });
    }
}
