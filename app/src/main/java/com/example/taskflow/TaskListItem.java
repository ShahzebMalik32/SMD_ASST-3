package com.example.taskflow;

public class TaskListItem {
    public static final int TYPE_HEADER = 0;
    public static final int TYPE_TASK = 1;

    public final int type;
    // header
    public final String title;
    public final int count;
    // task snapshot
    public final String taskId;
    public final String description;
    public final String status;
    public final String priority;
    public final Long dueDate;
    public final boolean dueToday;
    public final boolean overdue;

    private TaskListItem(int type, String title, int count, Task t) {
        this.type = type;
        this.title = title;
        this.count = count;
        if (t != null) {
            taskId = t.getId();
            description = t.getDescription();
            status = t.getStatus();
            priority = t.getPriority();
            dueDate = t.getDueDate();
            dueToday = t.isDueToday();
            overdue = t.isOverdue();
        } else {
            taskId = null;
            description = null;
            status = null;
            priority = null;
            dueDate = null;
            dueToday = false;
            overdue = false;
        }
    }

    public static TaskListItem header(String title, int count) {
        return new TaskListItem(TYPE_HEADER, title, count, null);
    }

    public static TaskListItem task(Task t) {
        return new TaskListItem(TYPE_TASK, null, 0, t);
    }
}