package com.example.taskflow.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/** One item in the local attachment catalog (image or text). */
public class CatalogItem implements Serializable {

    public static final String TYPE_IMAGE = "Image";
    public static final String TYPE_TEXT = "Text";

    private final int id;
    private final String displayName;
    private final String type;
    private final int drawableRes;   // for images (R.drawable.xxx), 0 for text
    private final String textContent; // for text docs, null for images

    public CatalogItem(int id, String displayName, String type, int drawableRes, String textContent) {
        this.id = id;
        this.displayName = displayName;
        this.type = type;
        this.drawableRes = drawableRes;
        this.textContent = textContent;
    }

    public int getId() { return id; }
    public String getDisplayName() { return displayName; }
    public String getType() { return type; }
    public int getDrawableRes() { return drawableRes; }
    public String getTextContent() { return textContent; }
    public boolean isImage() { return TYPE_IMAGE.equals(type); }

    /**
     * Sample catalog. Replace the 0s with your own drawables, e.g. R.drawable.design_mockup
     * (add 2-3 PNG/JPG files to res/drawable).
     */
    public static List<CatalogItem> buildCatalog() {
        List<CatalogItem> list = new ArrayList<>();
        list.add(new CatalogItem(1, "design_mockup.png", TYPE_IMAGE, 0, null));
        list.add(new CatalogItem(2, "wireframe.jpg", TYPE_IMAGE, 0, null));
        list.add(new CatalogItem(3, "requirements.txt", TYPE_TEXT, 0,
                "Project requirements:\n1. User login\n2. Task board\n3. File sharing"));
        list.add(new CatalogItem(4, "meeting_notes.txt", TYPE_TEXT, 0,
                "Meeting notes:\n- Finalize UI\n- Assign tasks\n- Review on Friday"));
        list.add(new CatalogItem(5, "logo.png", TYPE_IMAGE, 0, null));
        return list;
    }

    public static CatalogItem findById(int id) {
        for (CatalogItem c : buildCatalog()) if (c.getId() == id) return c;
        return null;
    }
}
