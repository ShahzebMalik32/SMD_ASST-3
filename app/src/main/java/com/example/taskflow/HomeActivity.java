package com.example.taskflow;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity {

    /** The full project collection. */
    private final ArrayList<Project> projects = new ArrayList<>();
    /** What the RecyclerView currently shows (the collection filtered by the search box). */
    private ArrayList<Project> visibleProjects = new ArrayList<>();

    private ProjectAdapter adapter;
    private EditText etSearch;
    private TextView tvShowing;
    private TextView tvNoProjects;
    private RecyclerView rvProjects;

    private ActivityResultLauncher<Intent> projectLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        projectLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), this::onProjectResult);

        TextView tvGreeting = findViewById(R.id.tvGreeting);
        Button btnNewProject = findViewById(R.id.btnNewProject);
        etSearch = findViewById(R.id.etSearchProjects);
        tvShowing = findViewById(R.id.tvShowingProjects);
        tvNoProjects = findViewById(R.id.tvNoProjects);
        rvProjects = findViewById(R.id.rvProjects);

        String userName = getIntent().getStringExtra(Constants.EXTRA_USER_NAME);
        if (TextUtils.isEmpty(userName)) {
            userName = "there";
        }
        tvGreeting.setText(getString(R.string.greeting_format, userName));

        projects.addAll(SampleData.buildProjects());

        adapter = new ProjectAdapter(this::openProjectScreen);
        rvProjects.setLayoutManager(new LinearLayoutManager(this));
        rvProjects.setAdapter(adapter);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                applyFilter(null);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        btnNewProject.setOnClickListener(v -> showNewProjectDialog());
        applyFilter(null);
    }

    // ---------------------------------------------------------------- list + search

    /** Case-insensitive match on any part of the project name or tag. */
    private void applyFilter(final Project scrollTo) {
        String query = etSearch.getText().toString().trim().toLowerCase(Locale.ROOT);
        ArrayList<Project> filtered = new ArrayList<>();
        for (Project p : projects) {
            if (query.isEmpty()
                    || p.getName().toLowerCase(Locale.ROOT).contains(query)
                    || p.getTag().toLowerCase(Locale.ROOT).contains(query)) {
                filtered.add(p);
            }
        }
        visibleProjects = filtered;
        adapter.submitList(filtered, () -> {
            if (scrollTo != null) {
                int index = visibleProjects.indexOf(scrollTo);
                if (index >= 0) {
                    rvProjects.smoothScrollToPosition(index);
                }
            }
        });
        tvNoProjects.setVisibility(filtered.isEmpty() ? View.VISIBLE : View.GONE);
        tvShowing.setText(getString(R.string.showing_projects_format,
                filtered.size(), projects.size()));
    }

    // ---------------------------------------------------------------- navigation

    private void openProjectScreen(Project project) {
        Intent intent = new Intent(this, ProjectActivity.class);
        intent.putExtra(Constants.EXTRA_PROJECT, project);
        projectLauncher.launch(intent);
    }

    /** Replaces the existing project (matched by ID, never by name or position). */
    @SuppressWarnings("deprecation")
    private void onProjectResult(ActivityResult result) {
        if (result.getResultCode() != RESULT_OK || result.getData() == null) {
            return;
        }
        Project updated = (Project) result.getData().getSerializableExtra(Constants.EXTRA_PROJECT);
        if (updated == null) {
            return;
        }
        for (int i = 0; i < projects.size(); i++) {
            if (projects.get(i).getId().equals(updated.getId())) {
                projects.set(i, updated);
                break;
            }
        }
        applyFilter(null);
    }

    // ---------------------------------------------------------------- new project

    private void showNewProjectDialog() {
        ProjectFormDialog.show(this, null, (name, tag, description, attachment) -> {
            Project project = new Project(name, tag, description);
            if (attachment != null) {
                project.addAttachment(new AttachmentLink(attachment.getId(), attachment.getResourceRef()));
            }
            projects.add(project);
            applyFilter(project);
        });
    }
}