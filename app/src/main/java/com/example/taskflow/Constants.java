package com.example.taskflow;

public final class Constants {
    public static final String EXTRA_PROJECT = "extra_project";
    public static final String EXTRA_USER_NAME = "extra_user_name";
    public static final String ARG_PROJECT_ID = "arg_project_id";
    public static final String STATE_PROJECT = "state_project";
    public static final String STATE_TAB = "state_tab";

    public static final int MAX_TASK_LENGTH = 500;
    public static final int MAX_PROJECT_NAME = 60;
    public static final int MAX_PROJECT_TAG = 20;
    public static final int MAX_PROJECT_DESCRIPTION = 500;

    public static final int TAB_OVERVIEW = 0;
    public static final int TAB_TASKS = 1;
    public static final int TAB_FILES = 2;
    public static final int TAB_DISCUSSION = 3;

    // Task filter / sort options (index 0 is always the default)
    public static final String[] STATUS_FILTERS = {"All statuses", "To do", "In progress", "Done"};
    public static final String[] PRIORITY_FILTERS = {"All priorities", "High", "Normal", "Low"};
    public static final String[] DATE_FILTERS = {"All dates", "Today", "Overdue", "No due date"};
    public static final String[] SORT_OPTIONS = {
            "Due date ascending", "Due date descending", "Priority (high first)", "Newest first"};

    public static final int DATE_ALL = 0;
    public static final int DATE_TODAY = 1;
    public static final int DATE_OVERDUE = 2;
    public static final int DATE_NONE = 3;

    private Constants() {
    }
}
