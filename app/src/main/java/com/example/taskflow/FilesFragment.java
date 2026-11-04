package com.example.taskflow;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Locale;

public class FilesFragment extends ProjectFragment {

    private static final int TYPE_ALL = 0;
    private static final int TYPE_IMAGES = 1;
    private static final int TYPE_TEXT = 2;

    private String query = "";
    private int typeFilter = TYPE_ALL;

    private final ArrayList<AttachmentLink> visibleLinks = new ArrayList<>();
    private AttachmentAdapter adapter;

    private EditText etSearch;
    private TextView chipAll;
    private TextView chipImages;
    private TextView chipText;
    private TextView tvShowing;
    private ListView lvFiles;
    private View stateNone;
    private View stateNoMatch;

    public FilesFragment() {
        super(R.layout.fragment_files);
    }

    public static FilesFragment newInstance(String projectId) {
        FilesFragment fragment = new FilesFragment();
        fragment.setArguments(argsFor(projectId));
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        etSearch = view.findViewById(R.id.etSearchFiles);
        chipAll = view.findViewById(R.id.chipAll);
        chipImages = view.findViewById(R.id.chipImages);
        chipText = view.findViewById(R.id.chipText);
        tvShowing = view.findViewById(R.id.tvShowingFiles);
        lvFiles = view.findViewById(R.id.lvFiles);
        stateNone = view.findViewById(R.id.stateNoFiles);
        stateNoMatch = view.findViewById(R.id.stateNoFileMatch);

        etSearch.setText(query);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                query = s.toString();
                refresh();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        chipAll.setOnClickListener(v -> setTypeFilter(TYPE_ALL));
        chipImages.setOnClickListener(v -> setTypeFilter(TYPE_IMAGES));
        chipText.setOnClickListener(v -> setTypeFilter(TYPE_TEXT));

        adapter = new AttachmentAdapter(requireContext(), visibleLinks, new AttachmentAdapter.Listener() {
            @Override
            public void onPreview(AttachmentLink link) {
                showPreview(link);
            }

            @Override
            public void onRemove(AttachmentLink link) {
                confirmRemove(link);
            }
        });
        lvFiles.setAdapter(adapter);

        view.findViewById(R.id.btnAddAttachment).setOnClickListener(v -> addAttachment());
        view.findViewById(R.id.btnAddAttachmentEmpty).setOnClickListener(v -> addAttachment());
        view.findViewById(R.id.btnResetFiles).setOnClickListener(v -> resetFilters());

        refresh();
    }

    private void setTypeFilter(int filter) {
        typeFilter = filter;
        refresh();
    }

    private void resetFilters() {
        query = "";
        typeFilter = TYPE_ALL;
        etSearch.setText("");
        refresh();
    }

    // ---------------------------------------------------------------- refresh

    @Override
    public void refresh() {
        if (getView() == null || host == null) {
            return;
        }
        Project p = project();
        String q = query.trim().toLowerCase(Locale.ROOT);

        // Links stay in the order they were added.
        visibleLinks.clear();
        for (AttachmentLink link : p.getAttachments()) {
            CatalogItem item = CatalogItem.find(link.getCatalogId());
            if (item == null) {
                continue;
            }
            if (typeFilter == TYPE_IMAGES && !item.isImage()) {
                continue;
            }
            if (typeFilter == TYPE_TEXT && item.isImage()) {
                continue;
            }
            if (!q.isEmpty() && !item.getDisplayName().toLowerCase(Locale.ROOT).contains(q)) {
                continue;
            }
            visibleLinks.add(link);
        }
        adapter.notifyDataSetChanged();

        // Chip counts always describe the complete project collection.
        int total = p.getAttachmentCount();
        chipAll.setText(getString(R.string.chip_all_format, total));
        chipImages.setText(getString(R.string.chip_images_format, p.countAttachmentsOfType(true)));
        chipText.setText(getString(R.string.chip_text_format, p.countAttachmentsOfType(false)));
        styleChip(chipAll, typeFilter == TYPE_ALL);
        styleChip(chipImages, typeFilter == TYPE_IMAGES);
        styleChip(chipText, typeFilter == TYPE_TEXT);

        tvShowing.setText(getString(R.string.showing_attachments_format, visibleLinks.size(), total));

        boolean none = total == 0;
        boolean noMatch = !none && visibleLinks.isEmpty();
        lvFiles.setVisibility(visibleLinks.isEmpty() ? View.GONE : View.VISIBLE);
        stateNone.setVisibility(none ? View.VISIBLE : View.GONE);
        stateNoMatch.setVisibility(noMatch ? View.VISIBLE : View.GONE);
    }

    private void styleChip(TextView chip, boolean selected) {
        chip.setBackgroundResource(selected ? R.drawable.bg_chip_blue : R.drawable.bg_pill_outline);
        chip.setTextColor(ContextCompat.getColor(requireContext(),
                selected ? R.color.white : R.color.text_secondary));
    }

    // ---------------------------------------------------------------- add

    private void addAttachment() {
        CatalogDialog.show(requireContext(), project().getAttachments(), item -> {
            project().addAttachment(new AttachmentLink(item.getId(), item.getResourceRef()));
            host.onProjectChanged();
        });
    }

    // ---------------------------------------------------------------- preview

    private void showPreview(AttachmentLink link) {
        CatalogItem item = CatalogItem.find(link.getCatalogId());
        if (item == null) {
            return;
        }
        View view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_preview, null);
        TextView tvTitle = view.findViewById(R.id.tvPreviewTitle);
        ImageView ivPreview = view.findViewById(R.id.ivPreview);
        View svText = view.findViewById(R.id.svPreviewText);
        TextView tvText = view.findViewById(R.id.tvPreviewText);
        Button btnClose = view.findViewById(R.id.btnClosePreview);

        tvTitle.setText(item.getDisplayName());
        if (item.isImage()) {
            ivPreview.setVisibility(View.VISIBLE);
            ivPreview.setImageResource(item.getResId());
        } else {
            svText.setVisibility(View.VISIBLE);
            tvText.setText(item.loadText(requireContext()));
        }

        AlertDialog dialog = new AlertDialog.Builder(requireContext()).setView(view).create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
        btnClose.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    // ---------------------------------------------------------------- remove

    /** The user must type the attachment's display name before it can be removed. */
    private void confirmRemove(AttachmentLink link) {
        CatalogItem item = CatalogItem.find(link.getCatalogId());
        if (item == null) {
            return;
        }
        final String linkId = link.getId();
        final String name = item.getDisplayName();

        View view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_remove_attachment, null);
        TextView tvMessage = view.findViewById(R.id.tvRemoveMessage);
        EditText etName = view.findViewById(R.id.etConfirmName);
        Button btnCancel = view.findViewById(R.id.btnCancelRemove);
        Button btnRemove = view.findViewById(R.id.btnConfirmRemove);

        tvMessage.setText(getString(R.string.remove_attachment_message, name));
        btnRemove.setEnabled(false);
        btnRemove.setAlpha(0.4f);

        etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                boolean match = s.toString().trim().equals(name);
                btnRemove.setEnabled(match);
                btnRemove.setAlpha(match ? 1f : 0.4f);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        AlertDialog dialog = new AlertDialog.Builder(requireContext()).setView(view).create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
        btnCancel.setOnClickListener(v -> dialog.dismiss());
        btnRemove.setOnClickListener(v -> {
            project().removeAttachment(linkId);
            dialog.dismiss();
            host.onProjectChanged();
        });
        dialog.show();
    }
}
