package com.digicoffer.lauditor.Calendar.Models;

public class CalendarDo {
    String ProjectName;

    public String getProjectName() {
        return ProjectName;
    }

    public void setProjectName(String projectName) {
        ProjectName = projectName;
    }

    public CalendarDo(String projectName) {
        ProjectName = projectName;
    }
}
