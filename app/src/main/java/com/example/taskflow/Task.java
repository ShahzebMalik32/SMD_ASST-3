package com.example.taskflow;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class Task implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String STATUS_TODO = "To do";
    public static final String STATUS_IN_PROGRESS = "In progress";
    public static final String STATUS_DONE = "Done";
    public static final String[] STATUSES = {STATUS_TODO, STATUS_IN_PROGRESS, STATUS_DONE};

    public static final String PRIORITY_HIGH = "High";
    public static final String PRIORITY_NORMAL = "Normal";
    public static final String PRIORITY_LOW = "Low";
    public static final String[] PRIORITIES = {PRIORITY_HIGH, PRIORITY_NORMAL, PRIORITY_LOW};

    private final String id;
    private final long createdAt;
    private String description;
    private String status;
    private String priority;
    private Long dueDate; // local midnight of the due day, or null for "No due date"

    public Task(String description, String priority, String status, Long dueDate) {
        this.id = UUID.randomUUID().toString();
        this.createdAt = System.currentTimeMillis();
        this.description = description;
        this.priority = priority;
        this.status = status;
        this.dueDate = dueDate;
    }

    public String getId() {
        return id;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public String getPriority() {
        return priority;
    }

    public Long getDueDate() {
        return dueDate;
    }

    public boolean hasDueDate() {
        return dueDate != null;
    }

    public void update(String description, String priority, String status, Long dueDate) {
        this.description = description;
        this.priority = priority;
        this.status = status;
        this.dueDate = dueDate;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isDone() {
        return STATUS_DONE.equals(status);
    }

    /** Due date is before today (device local date) and the task is not Done. */
    public boolean isOverdue() {
        return dueDate != null && !isDone() && startOfDay(dueDate) < today();
    }

    public boolean isDueToday() {
        return dueDate != null && startOfDay(dueDate) == today();
    }

    public int priorityRank() {
        if (PRIORITY_HIGH.equals(priority)) {
            return 0;
        }
        if (PRIORITY_NORMAL.equals(priority)) {
            return 1;
        }
        return 2;
    }

    // ---------------------------------------------------------------- date helpers

    public static long startOfDay(long millis) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(millis);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTimeInMillis();
    }

    public static long today() {
        return startOfDay(System.currentTimeMillis());
    }

    public static long daysFromToday(int days) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(today());
        c.add(Calendar.DAY_OF_YEAR, days);
        return c.getTimeInMillis();
    }

    public static long dateOf(int year, int month, int day) {
        Calendar c = Calendar.getInstance();
        c.clear();
        c.set(year, month, day, 0, 0, 0);
        return c.getTimeInMillis();
    }

    public static String formatDate(long millis) {
        return new SimpleDateFormat("d MMM yyyy", Locale.getDefault()).format(new Date(millis));
    }
}
