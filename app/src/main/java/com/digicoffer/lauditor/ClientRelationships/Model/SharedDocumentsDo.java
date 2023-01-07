package com.digicoffer.lauditor.ClientRelationships.Model;

public class SharedDocumentsDo {
    String category;
    String created;
    String description;
    String doctype;
    String id;
    String name;
    String shareOn;
    private boolean isSelected;
    boolean isChecked;
    boolean isenabled;
    String content_type;
    String expiration_date;
    String filename;
    boolean is_disabled;
    boolean is_encrypted;
    boolean is_password;
    String origin;
    String uploaded_by;
//"Existing Documents"
    public String getContent_type() {
        return content_type;
    }

    public void setContent_type(String content_type) {
        this.content_type = content_type;
    }

    public String getExpiration_date() {
        return expiration_date;
    }

    public void setExpiration_date(String expiration_date) {
        this.expiration_date = expiration_date;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public boolean isIs_disabled() {
        return is_disabled;
    }

    public void setIs_disabled(boolean is_disabled) {
        this.is_disabled = is_disabled;
    }

    public boolean isIs_encrypted() {
        return is_encrypted;
    }

    public void setIs_encrypted(boolean is_encrypted) {
        this.is_encrypted = is_encrypted;
    }

    public boolean isIs_password() {
        return is_password;
    }

    public void setIs_password(boolean is_password) {
        this.is_password = is_password;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getUploaded_by() {
        return uploaded_by;
    }

    public void setUploaded_by(String uploaded_by) {
        this.uploaded_by = uploaded_by;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public boolean isIsenabled() {
        return isenabled;
    }

    public void setIsenabled(boolean isenabled) {
        this.isenabled = isenabled;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDoctype() {
        return doctype;
    }

    public void setDoctype(String doctype) {
        this.doctype = doctype;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShareOn() {
        return shareOn;
    }

    public void setShareOn(String shareOn) {
        this.shareOn = shareOn;
    }
}
