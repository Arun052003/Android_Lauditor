package com.digicoffer.lauditor.email;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.digicoffer.lauditor.NewModel;
import com.digicoffer.lauditor.R;
import com.digicoffer.lauditor.Webservice.AsyncTaskCompleteListener;
import com.digicoffer.lauditor.Webservice.HttpResultDo;

public class Email extends Fragment implements AsyncTaskCompleteListener {
    private NewModel mViewModel;
    ImageView close_documents;

    @Override
    public void onClick(View view) {

    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.email_layout, container, false);
        mViewModel = new ViewModelProvider(requireActivity()).get(NewModel.class);
        mViewModel.setData("Emails");

        ImageView close_documents = view.findViewById(R.id.close_documents);
        close_documents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create an Intent to switch to the ComposeActivity
                Intent intent = new Intent(getContext(),compose.class);
                startActivity(intent);
            }
        });










        return view;
    }

    @Override
    public void onAsyncTaskComplete(HttpResultDo httpResult) {

    }


}
