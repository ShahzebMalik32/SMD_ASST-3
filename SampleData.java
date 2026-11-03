package com.example.taskflow.model;

import java.util.ArrayList;
import java.util.List;

public class SampleData {

    private static final long DAY = 24L * 60 * 60 * 1000;

    private static long daysFromToday(int days) {
        return Task.startOfToday() + days * DAY;
    }

    public static List<Project> buildProjects() {
        List<Project> projects = new ArrayList<>();

        // Main sample project: 12 tasks (4 done), 3 attachments, 3 comments
        Project main = new Project("Website Redesign", "DESIGN",
                "Redesign the company website with a modern look, better navigation and improved mobile experience.");
        main.setCommentCount(3);

        main.addTask(new Task("Create wireframes for home page", Task.HIGH, Task.DONE, daysFromToday(-5)));
        main.addTask(new Task("Design color palette and typography", Task.NORMAL, Task.DONE, daysFromToday(-4)));
        main.addTask(new Task("Build navigation bar component", Task.NORMAL, Task.DONE, daysFromToday(-2)));
        main.addTask(new Task("Set up project repository", Task.LOW, Task.DONE, 0));
        main.addTask(new Task("Implement responsive hero section", Task.HIGH, Task.IN_PROGRESS, daysFromToday(0)));
        main.addTask(new Task("Write copy for About page", Task.NORMAL, Task.IN_PROGRESS, daysFromToday(3)));
        main.addTask(new Task("Integrate contact form with backend", Task.HIGH, Task.IN_PROGRESS, daysFromToday(-1)));
        main.addTask(new Task("Optimize images for web", Task.LOW, Task.TODO, daysFromToday(7)));
        main.addTask(new Task("Add analytics tracking", Task.NORMAL, Task.TODO, 0));
        main.addTask(new Task("Cross-browser testing", Task.HIGH, Task.TODO, daysFromToday(-3)));
        main.addTask(new Task("Accessibility audit", Task.NORMAL, Task.TODO, daysFromToday(10)));
        main.addTask(new Task("Prepare launch checklist", Task.LOW, Task.TODO, daysFromToday(14)));

        main.addAttachment(new AttachmentLink(1, "design_mockup.png"));
        main.addAttachment(new AttachmentLink(2, "wireframe.jpg"));
        main.addAttachment(new AttachmentLink(3, "requirements.txt"));
        projects.add(main);

        Project second = new Project("Mobile App Launch", "MOBILE",
                "Plan and ship the first version of the mobile app to the app stores.");
        second.setCommentCount(5);
        second.addTask(new Task("Finalize app icon", Task.NORMAL, Task.DONE, daysFromToday(-2)));
        second.addTask(new Task("Write store listing", Task.HIGH, Task.TODO, daysFromToday(5)));
        second.addAttachment(new AttachmentLink(5, "logo.png"));
        projects.add(second);

        projects.add(new Project("Marketing Campaign", "MKT",
                "Q4 marketing campaign planning across social media and email."));

        return projects;
    }
}
