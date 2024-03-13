package com.digicoffer.lauditor.TimeSheets.Models;

import org.json.JSONObject;

import java.util.Date;

public class TaskModel {
    String taskid;
   String hours;
   String minutes;
   String matterid;
   String Task_matter_name;
   String Task_billing;
   String Task_matter_id;
   String Task_name;

    public String getTask_matter_name() {
        return Task_matter_name;
    }

    public void setTask_matter_name(String task_matter_name) {
        Task_matter_name = task_matter_name;
    }

    public String getTask_billing() {
        return Task_billing;
    }

    public void setTask_billing(String task_billing) {
        Task_billing = task_billing;
    }

    public String getTask_matter_id() {
        return Task_matter_id;
    }

    public void setTask_matter_id(String task_matter_id) {
        Task_matter_id = task_matter_id;
    }

    public String getTask_name() {
        return Task_name;
    }

    public void setTask_name(String task_name) {
        Task_name = task_name;
    }

    public String getTaskid() {
        return taskid;
    }

    public void setTaskid(String taskid) {
        this.taskid = taskid;
    }

    public String getHours() {
        return hours;
    }

    public void setHours(String hours) {
        this.hours = hours;
    }

    public String getMinutes() {
        return minutes;
    }

    public void setMinutes(String minutes) {
        this.minutes = minutes;
    }

    public String getMatterid() {
        return matterid;
    }

    public void setMatterid(String matterid) {
        this.matterid = matterid;
    }
}
