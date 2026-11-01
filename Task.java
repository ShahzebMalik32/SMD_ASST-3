package com.example.taskflow.model;

import java.io.Serializable;
import java.util.Calendar;

public class Task implements Serializable {

    public static final String TODO = "To do";
    public static final String IN_PROGRESS = "In progress";
    public static final String DONE = "Done";

    public static final String HIGH = "High";
    public static final String NORMAL = "Normal";
    public static final String LOW = "Low";

    private static int nextId = 1;

    private final int id;
    private String description;
    private String status = TODO;
    private String priority = NORMAL;
    private long dueDate = 0;            // 0 = no due date (millis)
    private final long createdAt;

    public Task(String description, String priority, String status, long dueDate) {
        this.id = nextId++;
        this.description = description.trim();
        this.priority = priority;
        this.status = status;
        this.dueDate = dueDate;
        this.createdAt = System.currentTimeMillis();
    }

    public int getId() { return id; }
    public String getDescription() { return description; }
    public void setDescription(String d) { this.description = d.trim(); }
    public String getStatus() { return status; }
    public void setStatus(String s) { this.status = s; }
    public String getPriority() { return priority; }
    public void setPriority(String p) { this.priority = p; }
    public long getDueDate() { return dueDate; }
    public void setDueDate(long d) { this.dueDate = d; }
    public long getCreatedAt() { return createdAt; }

    public boolean hasDueDate() { return dueDate != 0; }
    public boolean isDone() { return DONE.equals(status); }

    /** Overdue = due date before today (local) and not Done. */
    public boolean isOverdue() {
        return hasDueDate() && !isDone() && dueDate < startOfToday();
    }

    public boolean isDueToday() {
        long start = startOfToday();
        return hasDueDate() && dueDate >= start && dueDate < start + 24L * 60 * 60 * 1000;
    }

    public static long startOfToday() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTimeInMillis();
    }
}
