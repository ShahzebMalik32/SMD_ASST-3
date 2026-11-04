package com.example.taskflow;

import android.app.DatePickerDialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import java.util.Calendar;

/** Add / edit task dialog shared by the Overview and Tasks tabs. */
public final class TaskDialog {

    public interface OnSave {
        void onSave(String description, String priority, String status, @Nullable Long dueDate);
    }

    private TaskDialog() {
    }

    /**
     * @param existing null to create a task, otherwise the task being edited (prefilled)
     * @param onDelete only used when editing; null hides the Delete button
     */
    public static void show(Context context, @Nullable Task existing, OnSave onSave,
                            @Nullable Runnable onDelete) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_task, null);
        TextView tvTitle = view.findViewById(R.id.tvTaskDialogTitle);
        EditText etDescription = view.findViewById(R.id.etTaskDescription);
        TextView tvCount = view.findViewById(R.id.tvCharCount);
        Spinner spPriority = view.findViewById(R.id.spTaskPriority);
        Spinner spStatus = view.findViewById(R.id.spTaskStatus);
        TextView tvDueDate = view.findViewById(R.id.tvDueDate);
        TextView tvClearDate = view.findViewById(R.id.tvClearDate);
        Button btnCancel = view.findViewById(R.id.btnCancelTask);
        Button btnSave = view.findViewById(R.id.btnSaveTask);
        Button btnDelete = view.findViewById(R.id.btnDeleteTask);

        setupSpinner(context, spPriority, Task.PRIORITIES);
        setupSpinner(context, spStatus, Task.STATUSES);
        spPriority.setSelection(1); // Normal
        spStatus.setSelection(0);   // To do

        final Long[] due = {null};

        if (existing != null) {
            tvTitle.setText(R.string.edit_task_title);
            btnSave.setText(R.string.save_changes);
            etDescription.setText(existing.getDescription());
            spPriority.setSelection(indexOf(Task.PRIORITIES, existing.getPriority()));
            spStatus.setSelection(indexOf(Task.STATUSES, existing.getStatus()));
            due[0] = existing.getDueDate();
            if (onDelete != null) {
                btnDelete.setVisibility(View.VISIBLE);
            }
        }

        tvCount.setText(context.getString(R.string.char_count_format,
                etDescription.getText().length(), Constants.MAX_TASK_LENGTH));
        etDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tvCount.setText(context.getString(
                        R.string.char_count_format, s.length(), Constants.MAX_TASK_LENGTH));
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        Runnable showDate = () -> {
            if (due[0] == null) {
                tvDueDate.setText(R.string.hint_due_date);
                tvClearDate.setVisibility(View.GONE);
            } else {
                tvDueDate.setText(Task.formatDate(due[0]));
                tvClearDate.setVisibility(View.VISIBLE);
            }
        };
        showDate.run();

        tvDueDate.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            if (due[0] != null) {
                c.setTimeInMillis(due[0]);
            }
            new DatePickerDialog(context, (picker, year, month, day) -> {
                due[0] = Task.dateOf(year, month, day);
                showDate.run();
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
        });
        tvClearDate.setOnClickListener(v -> {
            due[0] = null;
            showDate.run();
        });

        AlertDialog dialog = new AlertDialog.Builder(context).setView(view).create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        btnDelete.setOnClickListener(v -> new AlertDialog.Builder(context)
                .setTitle(R.string.delete_task_title)
                .setMessage(R.string.delete_task_message)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.delete, (d, w) -> {
                    dialog.dismiss();
                    if (onDelete != null) {
                        onDelete.run();
                    }
                })
                .show());
        btnSave.setOnClickListener(v -> {
            String description = etDescription.getText().toString().trim();
            if (description.isEmpty()) {
                etDescription.setError(context.getString(R.string.error_task_required));
                return;
            }
            if (description.length() > Constants.MAX_TASK_LENGTH) {
                etDescription.setError(context.getString(R.string.error_task_too_long));
                return;
            }
            onSave.onSave(description,
                    Task.PRIORITIES[spPriority.getSelectedItemPosition()],
                    Task.STATUSES[spStatus.getSelectedItemPosition()],
                    due[0]);
            dialog.dismiss();
        });

        dialog.show();
    }

    private static void setupSpinner(Context context, Spinner spinner, String[] items) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, R.layout.item_spinner, items);
        adapter.setDropDownViewResource(R.layout.item_spinner_dropdown);
        spinner.setAdapter(adapter);
    }

    private static int indexOf(String[] items, String value) {
        for (int i = 0; i < items.length; i++) {
            if (items[i].equals(value)) {
                return i;
            }
        }
        return 0;
    }
}
