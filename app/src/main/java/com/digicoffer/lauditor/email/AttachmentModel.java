package com.digicoffer.lauditor.email;

import java.util.List;

public class AttachmentModel {
    String PartId;
    String mimeType;
    String filename;
    public String getPartId() {
        return PartId;
    }

    public void setPartId(String PartId) {
        this.PartId= PartId;
    }
    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String PartId) {
        this.PartId= PartId;
    }
    public String getFilename(){
        return filename;
    }
    public void setFilename(String filename){
        this.filename= filename;
    }

    public void setHeaders(List<Header> headers) {
    }

    public void setBody(Body body) {
    }
}
