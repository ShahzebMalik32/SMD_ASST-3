package com.example.taskflow;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import java.util.ArrayList;

/** New Project / Edit Project dialog. Names, tags and descriptions are trimmed before saving. */
public final class ProjectFormDialog {

    public interface OnSave {
        /** attachment is only ever non-null when creating a project. */
        void onSave(String name, String tag, String description, @Nullable CatalogItem attachment);
    }

    private ProjectFormDialog() {
    }

    /** @param existing null to create a project, otherwise the project being edited (prefilled) */
    public static void show(Context context, @Nullable Project existing, OnSave onSave) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_add_project, null);
        TextView tvTitle = view.findViewById(R.id.tvProjectDialogTitle);
        EditText etName = view.findViewById(R.id.etProjectName);
        EditText etTag = view.findViewById(R.id.etProjectTag);
        EditText etDescription = view.findViewById(R.id.etProjectDescription);
        View groupAttachment = view.findViewById(R.id.groupAttachment);
        TextView tvPicker = view.findViewById(R.id.tvAttachmentPicker);
        Button btnCancel = view.findViewById(R.id.btnCancelProject);
        Button btnSave = view.findViewById(R.id.btnAddProject);

        final CatalogItem[] selected = {null};

        if (existing != null) {
            tvTitle.setText(R.string.edit_project);
            btnSave.setText(R.string.save_changes);
            groupAttachment.setVisibility(View.GONE);
            etName.setText(existing.getName());
            etTag.setText(existing.getTag());
            etDescription.setText(existing.getDescription());
        } else {
            tvPicker.setOnClickListener(v ->
                    CatalogDialog.show(context, new ArrayList<>(), item -> {
                        selected[0] = item;
                        tvPicker.setText(item.getDisplayName());
                    }));
        }

        AlertDialog dialog = new AlertDialog.Builder(context).setView(view).create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String tag = etTag.getText().toString().trim();
            String description = etDescription.getText().toString().trim();

            boolean valid = true;
            if (name.isEmpty() || name.length() > Constants.MAX_PROJECT_NAME) {
                etName.setError(context.getString(R.string.error_project_name));
                valid = false;
            }
            if (tag.isEmpty() || tag.length() > Constants.MAX_PROJECT_TAG) {
                etTag.setError(context.getString(R.string.error_project_tag));
                valid = false;
            }
            if (description.isEmpty() || description.length() > Constants.MAX_PROJECT_DESCRIPTION) {
                etDescription.setError(context.getString(R.string.error_project_description));
                valid = false;
            }
            if (!valid) {
                return;
            }
            onSave.onSave(name, tag, description, selected[0]);
            dialog.dismiss();
        });

        dialog.show();
    }
}
