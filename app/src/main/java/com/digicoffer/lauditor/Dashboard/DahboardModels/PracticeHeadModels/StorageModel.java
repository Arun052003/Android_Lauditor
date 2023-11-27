package com.digicoffer.lauditor.Dashboard.DahboardModels.PracticeHeadModels;

public class StorageModel {
    int balanceStorage;
    int currentStorage;
    int totalStorage;

    public StorageModel(int balanceStorage, int currentStorage, int totalStorage) {
        this.balanceStorage = balanceStorage;
        this.currentStorage = currentStorage;
        this.totalStorage = totalStorage;
    }

    public int getBalanceStorage() {
        return balanceStorage;
    }

    public void setBalanceStorage(int balanceStorage) {
        this.balanceStorage = balanceStorage;
    }

    public int getCurrentStorage() {
        return currentStorage;
    }

    public void setCurrentStorage(int currentStorage) {
        this.currentStorage = currentStorage;
    }

    public int getTotalStorage() {
        return totalStorage;
    }

    public void setTotalStorage(int totalStorage) {
        this.totalStorage = totalStorage;
    }
}
