package com.digicoffer.lauditor.Matter;

import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.digicoffer.lauditor.Matter.Models.ViewMatterModel;
import com.digicoffer.lauditor.R;
import com.digicoffer.lauditor.common.AndroidUtils;

import java.util.ArrayList;

public class EditMatterTimeline extends Fragment {
    private ArrayList<ViewMatterModel> models;
    com.google.android.material.imageview.ShapeableImageView siv_time_line,siv_groups,siv_documents;
    CardView cv_client_details;
    ViewMatter matter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.edit_matter, container, false);
//        if (savedInstanceState!=null){
//            ViewMatterModel viewMatterModel = savedInstanceState.getParcelable("viewMatterModel");
//            AndroidUtils.showAlert(viewMatterModel.getTitle(),getContext());
//        }
        try{
            EditMatterTimeline fragment = new EditMatterTimeline();
            Bundle args = new Bundle();
//            args.putSerializable("models", models);
//            fragment.setArguments(args);
//            ViewMatterModel viewMatterModel = new ViewMatterModel();


            if (args != null ){
                ViewMatterModel viewMatterModel = getArguments().getParcelable("viewMatterModel");
//                models = (ArrayList<ViewMatterModel>) getArguments().getSerializable("viewMatterModel");
                //AndroidUtils.showAlert(viewMatterModel.getTitle(),getContext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        siv_time_line = view.findViewById(R.id.siv_timeline);
        siv_groups = view.findViewById(R.id.siv_groups);
        siv_documents = view.findViewById(R.id.siv_documents);
        loadTimeline();
        siv_time_line.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadTimeline();
             // matter.openViewDetailsPopUp();





            }
        });
        siv_groups.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadGCT();
            }
        });
        siv_documents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadDocuments();
            }
        });
        return view;
    }

    private void loadTimeline() {
        siv_time_line.setImageDrawable(getContext().getResources().getDrawable(R.mipmap.timeline_green));
        siv_groups.setImageDrawable(getContext().getResources().getDrawable(R.mipmap.frame_white_background));
        siv_documents.setImageDrawable(getContext().getResources().getDrawable(R.mipmap.white_document));
       // matter.openViewDetailsPopUp();

    }
    public void loadGCT() {
        siv_time_line.setImageDrawable(getContext().getResources().getDrawable(R.mipmap.timeline_white));
        siv_groups.setImageDrawable(getContext().getResources().getDrawable(R.mipmap.group_green_background));
        siv_documents.setImageDrawable(getContext().getResources().getDrawable(R.mipmap.white_document));
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        Fragment parentFragment = new GCT();
        //  FragmentManager childFragmentManager = getChildFragmentManager();
        ft.replace(R.id.parent_container,parentFragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
       // ft.addToBackStack(null);
        ft.commit();
//        Fragment childFragment = new GCT();
//        FragmentManager childFragmentManager = getChildFragmentManager();
//        childFragmentManager.beginTransaction().add(R.id.child_container, childFragment).commit();
    }
    public void loadDocuments() {
        siv_time_line.setImageDrawable(getContext().getResources().getDrawable(R.mipmap.timeline_white));
        siv_groups.setImageDrawable(getContext().getResources().getDrawable(R.mipmap.frame_white_background));
        siv_documents.setImageDrawable(getContext().getResources().getDrawable(R.mipmap.single_document_icon_white));
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        MatterDocuments matterInformation = new MatterDocuments();
        ft.replace(R.id.parent_container, matterInformation);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.addToBackStack(null);
        ft.commit();
//        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
//        MatterDocuments matterInformation = new MatterDocuments();
//        ft.replace(R.id.child_container,matterInformation);
//        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
//        ft.addToBackStack(null);
//        ft.commit();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
