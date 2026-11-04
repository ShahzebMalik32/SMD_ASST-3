package com.example.taskflow;

import java.io.Serializable;
import java.util.UUID;

/** Links a project to a catalog item. Only references are stored, never file bytes. */
public class AttachmentLink implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String id;
    private final int catalogId;
    private final String resourceRef;

    public AttachmentLink(int catalogId, String resourceRef) {
        this.id = UUID.randomUUID().toString();
        this.catalogId = catalogId;
        this.resourceRef = resourceRef;
    }

    public String getId() {
        return id;
    }

    public int getCatalogId() {
        return catalogId;
    }

    public String getResourceRef() {
        return resourceRef;
    }
}
