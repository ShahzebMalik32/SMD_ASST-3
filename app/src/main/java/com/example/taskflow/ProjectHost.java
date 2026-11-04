package com.example.taskflow;

/** Implemented by ProjectActivity; the fragments talk to it instead of keeping their own data. */
public interface ProjectHost {

    /** The one working project object for this visit. */
    Project getProject();

    /** Call after any change to the project's data (tasks, attachments, name/tag/description). */
    void onProjectChanged();

    /** Selects a tab (Constants.TAB_*) and shows its fragment. */
    void selectTab(int tabIndex);

    /** Resets the Tasks filters, applies the given status/date filter and opens the Tasks tab. */
    void showTasksFiltered(int statusFilterIndex, int dateFilterIndex);
}
