package com.digicoffer.lauditor.Matter.Models;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONObject;

@SuppressLint("ParcelCreator")
public class ViewMatterModel extends MatterModel implements Parcelable {
    String id;
    String created;
    String matter_title;
    String client_name;
    String caseNumber;
    String casetype;
    String courtName;
    String date_of_filling;
    String description;
    JSONArray clients;
    JSONArray documents;
    JSONArray groupAcls;
    JSONArray groups;
    String client_id;
    String client_type;
    String doc_id;
    String doc_type;
    String user_id;
    String member_id;
    String Email;
    String member_name;
    String closedate;
    String matterNumber;
    String matterType;
    String startdate;
    JSONArray timesheets;

    public JSONArray getGroups_list() {
        return groups_list;
    }

    public void setGroups_list(JSONArray groups_list) {
        this.groups_list = groups_list;
    }
    public JSONArray getClients_list() {
        return clients_list;
    }
    public JSONArray getDocuments_list() {
        return documents_list;
    }

    public void setDocuments_list(JSONArray documents_list) {
        this.documents_list = documents_list;
    }
    //    public JSONArray getDocuments() {
//        return documents;
//    }
//    public void setDocuments(JSONArray documents) {
//        this.documents = documents;
//    }
    public void setClients_list(JSONArray clients_list) {
        this.clients_list = clients_list;
    }
    public JSONArray getMembers_list() {
        return members_list;
    }

    public void setMembers_list(JSONArray members_list) {
        this.members_list = members_list;
    }

    public JSONArray getTimesheets() {
        return timesheets;
    }
    public String getMatter_title() {
        return matter_title;
    }
    public void setMatter_title(String matter_title) {
        this.matter_title = matter_title;
    }

    public void setTimesheets(JSONArray timesheets) {
        this.timesheets = timesheets;
    }

    public String getClosedate() {
        return closedate;
    }

    public void setClosedate(String closedate) {
        this.closedate = closedate;
    }

    public String getMatterNumber() {
        return matterNumber;
    }


    public void setMatterNumber(String matterNumber) {
        this.matterNumber = matterNumber;
    }

    public String getMatterType() {
        return matterType;
    }

    public void setMatterType(String matterType) {
        this.matterType = matterType;
    }

    public String getStartdate() {
        return startdate;
    }

    public void setStartdate(String startdate) {
        this.startdate = startdate;
    }

    public String getMember_name() {
        return member_name;
    }

    public void setMember_name(String member_name) {
        this.member_name = member_name;
    }

    String Advocate_name;
    String Number;
    public boolean isCanDelete() {
        return canDelete;
    }

    public void setCanDelete(boolean canDelete) {
        this.canDelete = canDelete;
    }

    boolean canDelete;
    public String getAdvocate_name() {
        return Advocate_name;
    }

    public void setAdvocate_name(String advocate_name) {
        Advocate_name = advocate_name;
    }

    public String getNumber() {
        return Number;
    }

    public void setNumber(String number) {
        Number = number;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getMember_id() {
        return member_id;
    }

    public void setMember_id(String member_id) {
        this.member_id = member_id;
    }

    public String getDoc_id() {
        return doc_id;
    }

    public void setDoc_id(String doc_id) {
        this.doc_id = doc_id;
    }

    public String getDoc_type() {
        return doc_type;
    }

    public void setDoc_type(String doc_type) {
        this.doc_type = doc_type;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getClient_type() {
        return client_type;
    }

    public void setClient_type(String client_type) {
        this.client_type = client_type;
    }

    private boolean isSelected;
    boolean isChecked;

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

    JSONObject hearingDateDetails;
    boolean is_editable;
    String judges;
    String matterClosedDate;
    JSONArray members;
    String nextHearingDate;
    JSONArray opponentAdvocates;
    JSONObject owner;
    JSONObject client;
    String priority;
    String status;
    JSONObject tags;
    JSONArray tempClients;
    JSONArray temporaryClients;
    String title;
    String group_id;

    public String getGroup_id() {
        return group_id;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }

    public String getGroup_name() {
        return group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

    String group_name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String getCreated() { return  created;
    }
    public void setCreated(String created) {
        this.created = created;
    }

    public String getCaseNumber() {
        return caseNumber;
    }

    public JSONArray getOpponentAdvocates() {
        return opponentAdvocates;
    }

    public void setOpponentAdvocates(JSONArray opponentAdvocates) {
        this.opponentAdvocates = opponentAdvocates;
    }

    public void setCaseNumber(String caseNumber) {
        this.caseNumber = caseNumber;
    }

    public String getCasetype() {
        return casetype;
    }

    public void setCasetype(String casetype) {
        this.casetype = casetype;
    }

    public String getCourtName() {
        return courtName;
    }

    public void setCourtName(String courtName) {
        this.courtName = courtName;
    }

    public String getDate_of_filling() {
        return date_of_filling;
    }

    public void setDate_of_filling(String date_of_filling) {
        this.date_of_filling = date_of_filling;

    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public JSONArray getClients() {
        return clients;
    }

    public void setClients(JSONArray clients) {
        this.clients = clients;
    }

    public JSONArray getDocuments() {
        return documents;
    }

    public void setDocuments(JSONArray documents) {
        this.documents = documents;
    }

    public JSONArray getGroupAcls() {
        return groupAcls;
    }

    public void setGroupAcls(JSONArray groupAcls) {
        this.groupAcls = groupAcls;
    }

    public JSONArray getGroups() {
        return groups;
    }

    public void setGroups(JSONArray groups) {
        this.groups = groups;
    }

    public JSONObject getHearingDateDetails() {
        return hearingDateDetails;
    }

    public void setHearingDateDetails(JSONObject hearingDateDetails) {
        this.hearingDateDetails = hearingDateDetails;
    }

    public boolean isIs_editable() {
        return is_editable;
    }

    public void setIs_editable(boolean is_editable) {
        this.is_editable = is_editable;
    }

    public String getJudges() {
        return judges;
    }

    public void setJudges(String judges) {
        this.judges = judges;
    }
    public String getClient_name() {
        return client_name;
    }
    public JSONArray getGroup_acls() {
        return group_acls;
    }
    public void setGroup_acls(JSONArray group_acls) {
        this.group_acls = group_acls;
    }

    public void setClient_name(String client_name) {
        this.client_name = client_name;
    }

    public String getMatterClosedDate() {
        return matterClosedDate;
    }

    public void setMatterClosedDate(String matterClosedDate) {
        this.matterClosedDate = matterClosedDate;
    }

    public JSONArray getMembers() {
        return members;
    }

    public void setMembers(JSONArray members) {
        this.members = members;
    }

    public String getNextHearingDate() {
        return nextHearingDate;
    }

    public void setNextHearingDate(String nextHearingDate) {
        this.nextHearingDate = nextHearingDate;
    }

    public JSONObject getOwner() {
        return owner;
    }
    public JSONObject getClient(){
        return client;
    }
    public void setClient(JSONObject client){this.client = client;}

    public void setOwner(JSONObject owner) {
        this.owner = owner;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public JSONObject getTags() {
        return tags;
    }

    public void setTags(JSONObject tags) {
        this.tags = tags;
    }

    public JSONArray getTempClients() {
        return tempClients;
    }

    public void setTempClients(JSONArray tempClients) {
        this.tempClients = tempClients;
    }

    public JSONArray getTemporaryClients() {
        return temporaryClients;
    }

    public void setTemporaryClients(JSONArray temporaryClients) {
        this.temporaryClients = temporaryClients;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {

    }

    public void set(int i, MatterModel matterModel) {
    }

    public int size() {

        return 0;
    }
}
