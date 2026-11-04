package com.example.taskflow;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/** One entry of the local attachment catalog: a bundled image (PNG/JPG) or text document. */
public final class CatalogItem {

    public static final String TYPE_IMAGE = "Image";
    public static final String TYPE_TEXT = "Text";

    private static final List<CatalogItem> ITEMS = Collections.unmodifiableList(Arrays.asList(
            new CatalogItem(1, "mountain_view.png", TYPE_IMAGE, "sample_mountain", R.drawable.sample_mountain),
            new CatalogItem(2, "team_photo.jpg", TYPE_IMAGE, "sample_team", R.drawable.sample_team),
            new CatalogItem(3, "project_brief.txt", TYPE_TEXT, "project_brief", R.raw.project_brief),
            new CatalogItem(4, "city_skyline.png", TYPE_IMAGE, "sample_city", R.drawable.sample_city),
            new CatalogItem(5, "meeting_notes.txt", TYPE_TEXT, "meeting_notes", R.raw.meeting_notes),
            new CatalogItem(6, "api_spec.txt", TYPE_TEXT, "api_spec", R.raw.api_spec)));

    private final int id;
    private final String displayName;
    private final String type;
    private final String resourceRef;
    private final int resId;

    private CatalogItem(int id, String displayName, String type, String resourceRef, int resId) {
        this.id = id;
        this.displayName = displayName;
        this.type = type;
        this.resourceRef = resourceRef;
        this.resId = resId;
    }

    public static List<CatalogItem> all() {
        return ITEMS;
    }

    public static CatalogItem find(int id) {
        for (CatalogItem item : ITEMS) {
            if (item.id == id) {
                return item;
            }
        }
        return null;
    }

    public int getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getType() {
        return type;
    }

    public String getResourceRef() {
        return resourceRef;
    }

    public int getResId() {
        return resId;
    }

    public boolean isImage() {
        return TYPE_IMAGE.equals(type);
    }

    /** Reads the bundled text document (res/raw). */
    public String loadText(Context context) {
        StringBuilder sb = new StringBuilder();
        try (InputStream in = context.getResources().openRawResource(resId);
             BufferedReader reader = new BufferedReader(
                     new InputStreamReader(in, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            return "";
        }
        return sb.toString();
    }

    /** Catalog items that are not yet linked to the given attachment list. */
    public static List<CatalogItem> eligibleFor(List<AttachmentLink> linked) {
        List<CatalogItem> result = new ArrayList<>();
        for (CatalogItem item : ITEMS) {
            boolean already = false;
            for (AttachmentLink link : linked) {
                if (link.getCatalogId() == item.id) {
                    already = true;
                    break;
                }
            }
            if (!already) {
                result.add(item);
            }
        }
        return result;
    }
}
