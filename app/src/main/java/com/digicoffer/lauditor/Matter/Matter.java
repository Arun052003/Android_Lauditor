package com.digicoffer.lauditor.Matter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.digicoffer.lauditor.Matter.Models.MatterModel;
import com.digicoffer.lauditor.NewModel;
import com.digicoffer.lauditor.R;
import com.digicoffer.lauditor.common.Constants;

import java.util.ArrayList;

public class Matter extends Fragment {

    com.google.android.material.imageview.ShapeableImageView siv_matter_icon, siv_groups, siv_documents;
    private HorizontalScrollView scrollView;
    private TextView tv_legal_matter, tv_general_matter;
    private TextView tv_create, tv_view;
    private NewModel mViewModel;
    public ArrayList<MatterModel> matter_arraylist;
    public LinearLayoutCompat create_matter_view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.create_matter, container, false);
        mViewModel = new ViewModelProvider(requireActivity()).get(NewModel.class);
        mViewModel.setData("Matter");
        Constants.MATTER_TYPE = "Legal";
        siv_matter_icon = view.findViewById(R.id.siv_matter_icon);
        create_matter_view = view.findViewById(R.id.create_matter_view);
        siv_groups = view.findViewById(R.id.siv_groups);
        siv_documents = view.findViewById(R.id.siv_documents);
        tv_legal_matter = view.findViewById(R.id.tv_legal_matter);
        tv_general_matter = view.findViewById(R.id.tv_general_matter);
        tv_create = view.findViewById(R.id.tv_create_matter);
        tv_view = view.findViewById(R.id.tv_view_matter);
//        siv_upload = view.findViewById(R.id.upload_icon);
//        siv_view = view.findViewById(R.id.view_icon);
        loadMatterInformation();
        tv_legal_matter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadLegalMatter();
            }
        });
        tv_general_matter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadGeneralMatter();
            }
        });
        tv_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadCreateUI();
            }
        });
        tv_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadViewUI();
            }
        });
        siv_matter_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (matter_arraylist.size() != 0) {
                    loadMatterInformation();
                }

//                FragmentManager childFragmentManager = getChildFragmentManager();
//                childFragmentManager.beginTransaction().add(R.id.child_container, childFragment).commit();
            }
        });
        siv_groups.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (matter_arraylist.size() != 0) {
                    loadGCT();
                }
            }
        });
        siv_documents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (matter_arraylist.size() != 0) {
                    loadDocuments();
                }
            }
        });
        matter_arraylist = new ArrayList<>();

        return view;
    }

    public ArrayList<MatterModel> getMatter_arraylist() {
        return  matter_arraylist;
    }

    //    public MatterModel matterModel
    private void loadViewUI() {
        tv_create.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_left_background));
        tv_view.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_right_green_count));
        create_matter_view.setVisibility(View.GONE);
        viewMatter();
    }

    private void viewMatter() {
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        ViewMatter matterInformation = new ViewMatter();
        ft.replace(R.id.child_container, matterInformation);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.addToBackStack(null);
        ft.commit();
    }

    private void loadCreateUI() {
        create_matter_view.setVisibility(View.VISIBLE);
        tv_create.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_left_green_background));
        tv_view.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_right_background));
        loadMatterInformation();
    }

    private void loadLegalMatter() {
        Constants.MATTER_TYPE = "Legal";
        tv_legal_matter.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_left_green_background));
        tv_general_matter.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_right_background));
        loadMatterInformation();
        mViewModel.setData("Legal Matter");
    }

    private void loadGeneralMatter() {
        Constants.MATTER_TYPE = "General";
        tv_legal_matter.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_left_background));
        tv_general_matter.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_right_green_count));
        loadMatterInformation();
        mViewModel.setData("General Matter");
    }

    public void loadDocuments() {
        siv_matter_icon.setImageDrawable(getContext().getResources().getDrawable(R.mipmap.single_document_icon));
        siv_groups.setImageDrawable(getContext().getResources().getDrawable(R.mipmap.groups_material_icon));
        siv_documents.setImageDrawable(getContext().getResources().getDrawable(R.mipmap.documents_copy_white));
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        MatterDocuments matterInformation = new MatterDocuments();
        ft.replace(R.id.child_container, matterInformation);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.addToBackStack(null);
        ft.commit();
    }

    public void loadGCT() {
        siv_matter_icon.setImageDrawable(getContext().getResources().getDrawable(R.mipmap.single_document_icon));
        siv_groups.setImageDrawable(getContext().getResources().getDrawable(R.mipmap.groups_material_icon_white));
        siv_documents.setImageDrawable(getContext().getResources().getDrawable(R.mipmap.documents_copy));
        Fragment childFragment = new GCT();
        FragmentManager childFragmentManager = getChildFragmentManager();
        childFragmentManager.beginTransaction().add(R.id.child_container, childFragment).commit();
    }

    public void loadMatterInformation() {
        siv_matter_icon.setImageDrawable(getContext().getResources().getDrawable(R.mipmap.single_document_icon_white));
        siv_groups.setImageDrawable(getContext().getResources().getDrawable(R.mipmap.groups_material_icon));
        siv_documents.setImageDrawable(getContext().getResources().getDrawable(R.mipmap.documents_copy));
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        MatterInformation matterInformation = new MatterInformation();
        ft.replace(R.id.child_container, matterInformation);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.addToBackStack(null);
        ft.commit();
    }

}
