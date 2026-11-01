package com.example.taskflow.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Project implements Serializable {

    private static int nextId = 1;

    private final int id;
    private String name;
    private String tag;
    private String description;
    private final List<Task> tasks = new ArrayList<>();
    private int commentCount = 0;

    public Project(String name, String tag, String description) {
        this.id = nextId++;
        this.name = name.trim();
        this.tag = tag.trim();
        this.description = description.trim();
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getTag() { return tag; }
    public String getDescription() { return description; }
    public int getCommentCount() { return commentCount; }
    public List<Task> getTasks() { return tasks; }

    public void edit(String name, String tag, String description) {
        this.name = name.trim();
        this.tag = tag.trim();
        this.description = description.trim();
    }

    public void addTask(Task t) { tasks.add(t); }

    public boolean removeTaskById(int taskId) {
        return tasks.removeIf(t -> t.getId() == taskId);
    }

    public Task findTaskById(int taskId) {
        for (Task t : tasks) if (t.getId() == taskId) return t;
        return null;
    }

    // ---- Derived counts (never stored) ----
    public int getTotalTasks() { return tasks.size(); }

    public int countByStatus(String status) {
        int n = 0;
        for (Task t : tasks) if (t.getStatus().equals(status)) n++;
        return n;
    }

    public int getCompletedTasks() { return countByStatus(Task.DONE); }

    public int getOverdueCount() {
        int n = 0;
        for (Task t : tasks) if (t.isOverdue()) n++;
        return n;
    }

    /** Done / total * 100, fraction removed. Empty project = 0. */
    public int getProgressPercent() {
        return tasks.isEmpty() ? 0 : (getCompletedTasks() * 100) / tasks.size();
    }

    // ---- Validation helpers (1-60, 1-20, 1-500 after trim) ----
    public static boolean isValidName(String s) { return inRange(s, 60); }
    public static boolean isValidTag(String s) { return inRange(s, 20); }
    public static boolean isValidDescription(String s) { return inRange(s, 500); }

    private static boolean inRange(String s, int max) {
        if (s == null) return false;
        int len = s.trim().length();
        return len >= 1 && len <= max;
    }
}
