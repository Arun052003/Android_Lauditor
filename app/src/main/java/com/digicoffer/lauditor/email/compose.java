package com.digicoffer.lauditor.email;

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

public abstract class compose extends Fragment implements AsyncTaskCompleteListener {
    private NewModel mViewModel;
    ImageView close_documents;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.compose, container, false);
        mViewModel = new ViewModelProvider(requireActivity()).get(NewModel.class);
        mViewModel.setData("Emails");

        close_documents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                View composeView = LayoutInflater.from(getContext()).inflate(R.layout.compose, null);


                ViewGroup parentLayout = (ViewGroup) view.getParent();
                parentLayout.addView(composeView);


            }
        });









        return view;
    }

    @Override
    public void onAsyncTaskComplete(HttpResultDo httpResult) {

    }


}
