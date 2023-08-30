package com.digicoffer.lauditor.Chat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.digicoffer.lauditor.R;
import com.digicoffer.lauditor.Webservice.AsyncTaskCompleteListener;
import com.digicoffer.lauditor.Webservice.HttpResultDo;

public class Teams extends Fragment implements AsyncTaskCompleteListener {
    @Override
    public void onClick(View view) {

    }
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.client, container, false);
        return view;
    }

    @Override
    public void onAsyncTaskComplete(HttpResultDo httpResult) {

    }
}
