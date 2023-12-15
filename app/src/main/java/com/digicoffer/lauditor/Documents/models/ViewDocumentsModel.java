package com.digicoffer.lauditor.Documents.models;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ViewDocumentsModel {
    String created;
    String description;
    String expiration_date;
    String filename;
    String content_type;
    String id;
    String[] groups;

    public String[] getGroups() {
        return groups;
    }

    public void setGroups(String groups) {
        this.groups = new String[]{groups};
    }


    String[] matters;
    boolean is_disabled;
    boolean isdisabled;
    boolean added_encryption;
    JSONObject tags;
    String[] tag;
    boolean is_encrypted;
    boolean is_password;
    ClientsModel clientsModel;
    String name;
    String origin;
    String uploaded_by;

    public boolean isIsdisabled() {
        Log.d("VIEW_CLIENT_FILE", "" + is_disabled);
        return isdisabled;
    }

    public void setIsdisabled(boolean isdisabled) {
        this.isdisabled = isdisabled;
    }

    public boolean isAdded_encryption() {
        return added_encryption;
    }

    public void setAdded_encryption(boolean added_encryption) {
        this.added_encryption = added_encryption;
    }

    public JSONObject getTags() {
        return tags;
    }

    public void setTags(JSONObject tags) {
        this.tags = tags;
    }

    public String[] getTag() {
        return tag;
    }

    public void setTag(String[] tag) {
        this.tag = tag;
    }

    public void setClientsModel(ClientsModel clientsModel) {
        this.clientsModel = clientsModel;
    }


    public ClientsModel getClientsModel() {
        return clientsModel;
    }

//    public void setClientsModel(JSONObject clientsModel) throws JSONException {
//        ClientsModel cm = new ClientsModel();
//        JSONObject object = new JSONObject();
//        cm.setId(object.getString("id"));
//        cm.setType(object.getString("type"));
//        cm.setName(object.getString("name"));
//        this.clientsModel = cm;
//    }

    public void setClientsModel(JSONArray clients) throws JSONException {
//        Log.d("setClientsModel: ", String.valueOf(clients));
        ClientsModel cm = new ClientsModel();
        JSONObject result = clients.getJSONObject(0);
        cm.setId((result.getString("id")));
        cm.setType(result.getString("type"));
        cm.setName(result.getString("name"));
        this.clientsModel = cm;
    }


    public String[] getMatters() {
        return matters;
    }

    public void setMatters(String matters) {
        this.matters = new String[]{matters};
    }

    public String getContent_type() {
        return content_type;
    }

    public void setContent_type(String content_type) {
        this.content_type = content_type;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
}
