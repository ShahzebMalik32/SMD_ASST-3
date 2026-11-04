package com.example.taskflow;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.tabs.TabLayout;

public class ProjectActivity extends AppCompatActivity implements ProjectHost {

    private static final String[] FRAGMENT_TAGS = {"overview", "tasks", "files", "discussion"};

    /** The one working project object for this visit. */
    private Project project;

    private TextView tvProjectName;
    private TextView tvProjectTag;
    private TabLayout tabLayout;

    private final Fragment[] fragments = new Fragment[4];
    private int currentTab = Constants.TAB_OVERVIEW;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);

        if (savedInstanceState != null) {
            project = (Project) savedInstanceState.getSerializable(Constants.STATE_PROJECT);
            currentTab = savedInstanceState.getInt(Constants.STATE_TAB, Constants.TAB_OVERVIEW);
        }
        if (project == null) {
            project = (Project) getIntent().getSerializableExtra(Constants.EXTRA_PROJECT);
        }
        if (project == null) {
            finish();
            return;
        }

        tvProjectName = findViewById(R.id.tvProjectName);
        tvProjectTag = findViewById(R.id.tvProjectTag);
        tabLayout = findViewById(R.id.tabLayout);
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
        findViewById(R.id.tvInvite).setOnClickListener(v -> showTeamDialog());

        bindHeader();
        setupFragments(savedInstanceState);
        setupTabs();
        publishResult();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(Constants.STATE_PROJECT, project);
        outState.putInt(Constants.STATE_TAB, currentTab);
    }

    private void showTeamDialog() {
        View view = getLayoutInflater().inflate(R.layout.dialog_team, null);
        AlertDialog dialog = new AlertDialog.Builder(this).setView(view).create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
        view.findViewById(R.id.btnCloseTeam).setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    // ---------------------------------------------------------------- setup

    private void bindHeader() {
        tvProjectName.setText(project.getName());
        tvProjectTag.setText(project.getTag());
    }

    private void setupFragments(Bundle savedInstanceState) {
        FragmentManager fm = getSupportFragmentManager();
        if (savedInstanceState == null) {
            fragments[0] = OverviewFragment.newInstance(project.getId());
            fragments[1] = TasksFragment.newInstance(project.getId());
            fragments[2] = FilesFragment.newInstance(project.getId());
            fragments[3] = DiscussionFragment.newInstance(project.getId());
            FragmentTransaction ft = fm.beginTransaction().setReorderingAllowed(true);
            for (int i = 0; i < fragments.length; i++) {
                ft.add(R.id.fragmentContainer, fragments[i], FRAGMENT_TAGS[i]);
                if (i != Constants.TAB_OVERVIEW) {
                    ft.hide(fragments[i]);
                }
            }
            ft.commit();
        } else {
            for (int i = 0; i < fragments.length; i++) {
                fragments[i] = fm.findFragmentByTag(FRAGMENT_TAGS[i]);
            }
        }
    }

    private void setupTabs() {
        for (int i = 0; i < 4; i++) {
            tabLayout.addTab(tabLayout.newTab().setText(tabTitle(i)));
        }
        TabLayout.Tab initial = tabLayout.getTabAt(currentTab);
        if (initial != null) {
            initial.select();
        }
        showFragment(currentTab);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                showFragment(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    private String tabTitle(int index) {
        switch (index) {
            case Constants.TAB_TASKS:
                return getString(R.string.tab_tasks_format, project.getTotalTaskCount());
            case Constants.TAB_FILES:
                return getString(R.string.tab_files_format, project.getAttachmentCount());
            case Constants.TAB_DISCUSSION:
                return getString(R.string.tab_discussion_format, project.getCommentCount());
            default:
                return getString(R.string.tab_overview);
        }
    }

    private void updateTabTitles() {
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab != null) {
                tab.setText(tabTitle(i));
            }
        }
    }

    /** Shows one fragment and hides the others; hidden fragments keep their state. */
    private void showFragment(int index) {
        currentTab = index;
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction().setReorderingAllowed(true);
        for (int i = 0; i < fragments.length; i++) {
            if (fragments[i] == null) {
                continue;
            }
            if (i == index) {
                ft.show(fragments[i]);
            } else {
                ft.hide(fragments[i]);
            }
        }
        ft.commit();
    }

    // ---------------------------------------------------------------- ProjectHost

    @Override
    public Project getProject() {
        return project;
    }

    @Override
    public void onProjectChanged() {
        bindHeader();
        updateTabTitles();
        publishResult();
        Fragment visible = fragments[currentTab];
        if (visible instanceof ProjectFragment && visible.getView() != null) {
            ((ProjectFragment) visible).refresh();
        }
    }

    @Override
    public void selectTab(int tabIndex) {
        TabLayout.Tab tab = tabLayout.getTabAt(tabIndex);
        if (tab != null) {
            tab.select();
        }
        if (currentTab != tabIndex) {
            showFragment(tabIndex);
        }
    }

    @Override
    public void showTasksFiltered(int statusFilterIndex, int dateFilterIndex) {
        Fragment tasks = fragments[Constants.TAB_TASKS];
        if (tasks instanceof TasksFragment) {
            ((TasksFragment) tasks).applyFilters(statusFilterIndex, dateFilterIndex);
        }
        selectTab(Constants.TAB_TASKS);
    }

    // ---------------------------------------------------------------- result to HomeActivity

    /** Keeps HomeActivity's result up to date so both the toolbar arrow and system Back return it. */
    private void publishResult() {
        Intent data = new Intent();
        data.putExtra(Constants.EXTRA_PROJECT, project);
        setResult(RESULT_OK, data);
    }
}
