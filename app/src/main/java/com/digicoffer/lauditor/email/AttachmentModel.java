package com.digicoffer.lauditor.email;

import java.util.List;

public class AttachmentModel {
    String partId;
    String mimeType;
    String filename;
    // Add more fields if needed to represent attachment data

    public String getPartId() {
        return partId;
    }

    public void setPartId(String partId) {
        this.partId = partId;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public void setHeaders(List<Header> headers) {
    }

    public void setBody(Body body) {
    }
}
