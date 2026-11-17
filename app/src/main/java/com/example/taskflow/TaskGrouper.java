package com.example.taskflow;

import java.util.ArrayList;
import java.util.List;

public class TaskGrouper {

    public static final String OVERDUE = "Overdue";

    /** Order: Overdue, In progress, To do, Done. Empty groups are skipped. Keeps the incoming order inside each group. */
    public static List<TaskListItem> group(List<Task> tasks) {
        List<Task> overdue = new ArrayList<>();
        List<Task> inProgress = new ArrayList<>();
        List<Task> todo = new ArrayList<>();
        List<Task> done = new ArrayList<>();

        for (Task t : tasks) {
            if (t.isDone()) {
                done.add(t);
            } else if (t.isOverdue()) {
                overdue.add(t);
            } else if (Task.STATUS_IN_PROGRESS.equals(t.getStatus())) {
                inProgress.add(t);
            } else {
                todo.add(t);
            }
        }

        List<TaskListItem> out = new ArrayList<>();
        add(out, OVERDUE, overdue);
        add(out, Task.STATUS_IN_PROGRESS, inProgress);
        add(out, Task.STATUS_TODO, todo);
        add(out, Task.STATUS_DONE, done);
        return out;
    }

    private static void add(List<TaskListItem> out, String title, List<Task> list) {
        if (list.isEmpty()) {
            return;
        }
        out.add(TaskListItem.header(title, list.size()));
        for (Task t : list) {
            out.add(TaskListItem.task(t));
        }
    }
}