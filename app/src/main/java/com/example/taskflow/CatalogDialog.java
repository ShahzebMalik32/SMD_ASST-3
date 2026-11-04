package com.example.taskflow;

import android.content.Context;

import androidx.appcompat.app.AlertDialog;

import java.util.List;

/** Single-choice catalog picker. "Add" stays disabled until an eligible item is selected. */
public final class CatalogDialog {

    public interface OnPick {
        void onPick(CatalogItem item);
    }

    private CatalogDialog() {
    }

    /** @param linked attachments already linked to the project; those items are not offered. */
    public static void show(Context context, List<AttachmentLink> linked, OnPick onPick) {
        List<CatalogItem> eligible = CatalogItem.eligibleFor(linked);
        if (eligible.isEmpty()) {
            new AlertDialog.Builder(context)
                    .setTitle(R.string.select_attachment)
                    .setMessage(R.string.no_more_catalog_items)
                    .setPositiveButton(R.string.ok, null)
                    .show();
            return;
        }

        String[] labels = new String[eligible.size()];
        for (int i = 0; i < eligible.size(); i++) {
            CatalogItem item = eligible.get(i);
            labels[i] = item.getDisplayName() + "  (" + item.getType() + ")";
        }

        final int[] selected = {-1};
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle(R.string.select_attachment)
                .setSingleChoiceItems(labels, -1, (d, which) -> {
                    selected[0] = which;
                    ((AlertDialog) d).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                })
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.add, (d, w) -> {
                    if (selected[0] >= 0) {
                        onPick.onPick(eligible.get(selected[0]));
                    }
                })
                .create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
    }
}
