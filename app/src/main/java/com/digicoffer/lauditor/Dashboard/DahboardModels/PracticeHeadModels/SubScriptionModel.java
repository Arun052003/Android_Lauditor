package com.digicoffer.lauditor.Dashboard.DahboardModels.PracticeHeadModels;

public class SubScriptionModel {
    boolean active_pay_button;
    boolean is_active_sub;
    boolean is_paid_sub;

    public SubScriptionModel(boolean active_pay_button, boolean is_active_sub, boolean is_paid_sub) {
        this.active_pay_button = active_pay_button;
        this.is_active_sub = is_active_sub;
        this.is_paid_sub = is_paid_sub;
    }

    public boolean isActive_pay_button() {
        return active_pay_button;
    }

    public void setActive_pay_button(boolean active_pay_button) {
        this.active_pay_button = active_pay_button;
    }

    public boolean isIs_active_sub() {
        return is_active_sub;
    }

    public void setIs_active_sub(boolean is_active_sub) {
        this.is_active_sub = is_active_sub;
    }

    public boolean isIs_paid_sub() {
        return is_paid_sub;
    }

    public void setIs_paid_sub(boolean is_paid_sub) {
        this.is_paid_sub = is_paid_sub;
    }
}
