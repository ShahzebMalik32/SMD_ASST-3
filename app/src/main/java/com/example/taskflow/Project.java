package com.example.taskflow;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

public class Project implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String id;
    private String tag;
    private String name;
    private String description;
    private int commentCount;
    private final ArrayList<Task> tasks = new ArrayList<>();
    private final ArrayList<AttachmentLink> attachments = new ArrayList<>();

    public Project(String name, String tag, String description) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.tag = tag;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public String getTag() {
        return tag;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public void edit(String name, String tag, String description) {
        this.name = name;
        this.tag = tag;
        this.description = description;
    }

    // ---------------------------------------------------------------- tasks

    public ArrayList<Task> getTasks() {
        return tasks;
    }

    public void addTask(Task task) {
        tasks.add(task);
    }

    public boolean removeTask(String taskId) {
        Iterator<Task> it = tasks.iterator();
        while (it.hasNext()) {
            if (it.next().getId().equals(taskId)) {
                it.remove();
                return true;
            }
        }
        return false;
    }

    public int getTotalTaskCount() {
        return tasks.size();
    }

    public int countByStatus(String status) {
        int count = 0;
        for (Task task : tasks) {
            if (task.getStatus().equals(status)) {
                count++;
            }
        }
        return count;
    }

    public int getCompletedTaskCount() {
        return countByStatus(Task.STATUS_DONE);
    }

    /** Unfinished tasks whose due date is before today. A subset of To do + In progress. */
    public int getOverdueCount() {
        int count = 0;
        for (Task task : tasks) {
            if (task.isOverdue()) {
                count++;
            }
        }
        return count;
    }

    /** Done / total * 100 with the fraction removed; 0 for an empty project. */
    public int getProgressPercent() {
        if (tasks.isEmpty()) {
            return 0;
        }
        return (getCompletedTaskCount() * 100) / tasks.size();
    }

    // ---------------------------------------------------------------- attachments

    public ArrayList<AttachmentLink> getAttachments() {
        return attachments;
    }

    public void addAttachment(AttachmentLink link) {
        attachments.add(link);
    }

    public boolean removeAttachment(String linkId) {
        Iterator<AttachmentLink> it = attachments.iterator();
        while (it.hasNext()) {
            if (it.next().getId().equals(linkId)) {
                it.remove();
                return true;
            }
        }
        return false;
    }

    public int getAttachmentCount() {
        return attachments.size();
    }

    public int countAttachmentsOfType(boolean images) {
        int count = 0;
        for (AttachmentLink link : attachments) {
            CatalogItem item = CatalogItem.find(link.getCatalogId());
            if (item != null && item.isImage() == images) {
                count++;
            }
        }
        return count;
    }
}
