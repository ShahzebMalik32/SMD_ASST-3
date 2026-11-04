package com.example.taskflow;

import java.util.ArrayList;

public final class SampleData {

    private SampleData() {
    }

    public static ArrayList<Project> buildProjects() {
        ArrayList<Project> list = new ArrayList<>();
        list.add(servicePage());
        list.add(loginApi());
        list.add(mobileDashboard());
        list.add(releaseChecklist());
        list.add(designAssets());
        return list;
    }

    private static void task(Project p, String description, String priority, String status, Integer dayOffset) {
        Long due = dayOffset == null ? null : Task.daysFromToday(dayOffset);
        p.addTask(new Task(description, priority, status, due));
    }

    private static void attach(Project p, int catalogId) {
        CatalogItem item = CatalogItem.find(catalogId);
        if (item != null) {
            p.addAttachment(new AttachmentLink(item.getId(), item.getResourceRef()));
        }
    }

    /** Main sample: 12 tasks (5 / 3 / 4, 2 overdue), 3 files (2 images + 1 text), 3 comments. */
    private static Project servicePage() {
        Project p = new Project("Service Page Website", "User Interface",
                "Create a page display about services for the company website in blue and red "
                        + "and provide some price information on each product card");
        p.setCommentCount(3);
        task(p, "Build header layout", Task.PRIORITY_HIGH, Task.STATUS_TODO, 0);
        task(p, "Add form validation", Task.PRIORITY_HIGH, Task.STATUS_TODO, 0);
        task(p, "Create low fidelity service page website", Task.PRIORITY_NORMAL, Task.STATUS_TODO, -7);
        task(p, "Write pricing section copy for every product card", Task.PRIORITY_NORMAL, Task.STATUS_TODO, 3);
        task(p, "Set up analytics tracking", Task.PRIORITY_LOW, Task.STATUS_TODO, null);
        task(p, "Configure pricing table responsive grid", Task.PRIORITY_NORMAL, Task.STATUS_IN_PROGRESS, -2);
        task(p, "Implement footer links", Task.PRIORITY_NORMAL, Task.STATUS_IN_PROGRESS, 2);
        task(p, "Review colour contrast for accessibility", Task.PRIORITY_LOW, Task.STATUS_IN_PROGRESS, 5);
        task(p, "Create brand guideline for website design", Task.PRIORITY_LOW, Task.STATUS_DONE, -9);
        task(p, "Draft hero copy and CTA text", Task.PRIORITY_LOW, Task.STATUS_DONE, -8);
        task(p, "Choose colour palette (blue and red)", Task.PRIORITY_NORMAL, Task.STATUS_DONE, -10);
        task(p, "Collect product photos", Task.PRIORITY_LOW, Task.STATUS_DONE, -12);
        attach(p, 1);
        attach(p, 2);
        attach(p, 3);
        return p;
    }

    private static Project loginApi() {
        Project p = new Project("Login API", "Backend",
                "Build authentication endpoints and JWT token refresh handlers");
        p.setCommentCount(2);
        task(p, "Design user table schema", Task.PRIORITY_HIGH, Task.STATUS_DONE, -14);
        task(p, "Implement POST /login endpoint", Task.PRIORITY_HIGH, Task.STATUS_DONE, -10);
        task(p, "Hash passwords with bcrypt", Task.PRIORITY_HIGH, Task.STATUS_DONE, -9);
        task(p, "Issue JWT access tokens", Task.PRIORITY_HIGH, Task.STATUS_IN_PROGRESS, 1);
        task(p, "Add refresh token rotation", Task.PRIORITY_NORMAL, Task.STATUS_IN_PROGRESS, 4);
        task(p, "Rate limit failed login attempts", Task.PRIORITY_NORMAL, Task.STATUS_TODO, 6);
        task(p, "Write integration tests", Task.PRIORITY_NORMAL, Task.STATUS_TODO, -1);
        task(p, "Document the auth endpoints", Task.PRIORITY_LOW, Task.STATUS_TODO, null);
        task(p, "Add logout and token revocation", Task.PRIORITY_NORMAL, Task.STATUS_TODO, 8);
        task(p, "Security review", Task.PRIORITY_HIGH, Task.STATUS_TODO, 12);
        attach(p, 6);
        attach(p, 4);
        return p;
    }

    private static Project mobileDashboard() {
        Project p = new Project("Mobile Dashboard", "Frontend",
                "Responsive overview widgets and telemetry cards for the mobile dashboard");
        p.setCommentCount(1);
        task(p, "Sketch dashboard layout", Task.PRIORITY_NORMAL, Task.STATUS_DONE, -6);
        task(p, "Build summary card component", Task.PRIORITY_HIGH, Task.STATUS_DONE, -4);
        task(p, "Add telemetry line chart", Task.PRIORITY_HIGH, Task.STATUS_IN_PROGRESS, 2);
        task(p, "Connect widgets to sample data", Task.PRIORITY_NORMAL, Task.STATUS_IN_PROGRESS, 3);
        task(p, "Pull to refresh", Task.PRIORITY_LOW, Task.STATUS_TODO, 7);
        task(p, "Dark mode colours", Task.PRIORITY_LOW, Task.STATUS_TODO, null);
        task(p, "Empty and error states", Task.PRIORITY_NORMAL, Task.STATUS_TODO, -2);
        task(p, "Performance check on low-end phone", Task.PRIORITY_NORMAL, Task.STATUS_TODO, 10);
        attach(p, 5);
        return p;
    }

    private static Project releaseChecklist() {
        Project p = new Project("Release Checklist", "QA & Ops",
                "Pre-production verification and smoke tests");
        p.setCommentCount(2);
        task(p, "Run full regression suite", Task.PRIORITY_HIGH, Task.STATUS_DONE, -3);
        task(p, "Verify database migrations", Task.PRIORITY_HIGH, Task.STATUS_DONE, -2);
        task(p, "Smoke test staging build", Task.PRIORITY_NORMAL, Task.STATUS_DONE, -1);
        task(p, "Publish release notes", Task.PRIORITY_LOW, Task.STATUS_DONE, 0);
        attach(p, 3);
        return p;
    }

    private static Project designAssets() {
        Project p = new Project("Design Assets", "Design",
                "Vector icons and illustration library for landing pages");
        p.setCommentCount(3);
        return p;
    }
}
