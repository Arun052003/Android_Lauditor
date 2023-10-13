package com.digicoffer.lauditor.AuditTrails;

import androidx.recyclerview.widget.DiffUtil;

import com.digicoffer.lauditor.AuditTrails.Model.AuditsModel;

import java.util.List;

public class AuditsDiffCallback extends DiffUtil.Callback {
    private List<AuditsModel> oldList;
    private List<AuditsModel> newList;

    public AuditsDiffCallback(List<AuditsModel> oldList, List<AuditsModel> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        // You can use a unique identifier of your AuditsModel here, e.g., ID
        return oldList.get(oldItemPosition).getName() == newList.get(newItemPosition).getName();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        AuditsModel oldAuditsModel = oldList.get(oldItemPosition);
        AuditsModel newAuditsModel = newList.get(newItemPosition);

        // Compare AuditsModel properties here to determine if their contents are the same.
        // Example:
        return oldAuditsModel.getName().equals(newAuditsModel.getName()) &&
                oldAuditsModel.getTimestamp().equals(newAuditsModel.getTimestamp()) &&
                oldAuditsModel.getMessage().equals(newAuditsModel.getMessage());
    }
}