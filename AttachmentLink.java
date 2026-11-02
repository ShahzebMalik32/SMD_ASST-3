package com.example.taskflow.model;

import java.io.Serializable;

/** A link between a project and a catalog item. Stores references, not bytes. */
public class AttachmentLink implements Serializable {

    private static int nextId = 1;

    private final int id;              // unique among AttachmentLinks
    private final int catalogId;       // CatalogItem id
    private final String resourceRef;  // e.g. drawable name or text file name
    private final long linkedAt;

    public AttachmentLink(int catalogId, String resourceRef) {
        this.id = nextId++;
        this.catalogId = catalogId;
        this.resourceRef = resourceRef;
        this.linkedAt = System.currentTimeMillis();
    }

    public int getId() { return id; }
    public int getCatalogId() { return catalogId; }
    public String getResourceRef() { return resourceRef; }
    public long getLinkedAt() { return linkedAt; }

    public CatalogItem getCatalogItem() { return CatalogItem.findById(catalogId); }
}
