package com.digicoffer.lauditor.Matter;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.digicoffer.lauditor.Matter.Models.MatterModel;
import com.digicoffer.lauditor.NewModel;
import com.digicoffer.lauditor.R;
import com.digicoffer.lauditor.common.AndroidUtils;
import com.digicoffer.lauditor.common.Constants;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

public class Matter extends Fragment {

    com.google.android.material.imageview.ShapeableImageView siv_matter_icon, siv_groups, siv_documents;
    private HorizontalScrollView scrollView;
    private TextView tv_legal_matter, tv_general_matter;
    private TextView tv_create, tv_view;
    private TextInputEditText tv_matter_title;
    private AppCompatButton matter_title;
    private NewModel mViewModel;
    MatterInformation matterInformation;
    CardView cv_add_opponent_advocate;
    Matter matter;
    matter_edit edit_matter1;

    public ArrayList<MatterModel> matter_arraylist;
    public LinearLayoutCompat create_matter_view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.create_matter, container, false);
        mViewModel = new ViewModelProvider(requireActivity()).get(NewModel.class);
        mViewModel.setData("Matter");
        siv_matter_icon = view.findViewById(R.id.siv_matter_icon);
        create_matter_view = view.findViewById(R.id.create_matter_view);
        siv_groups = view.findViewById(R.id.siv_groups);
        siv_documents = view.findViewById(R.id.siv_documents);
        tv_legal_matter = view.findViewById(R.id.tv_legal_matter);

        tv_matter_title = view.findViewById(R.id.tv_matter_title);

        tv_legal_matter.setText("Legal Matter");
        tv_general_matter = view.findViewById(R.id.tv_general_matter);
        tv_general_matter.setText("General Matter");
        tv_create = view.findViewById(R.id.tv_create_matter);

        tv_create.setText("Create");
        tv_view = view.findViewById(R.id.tv_view_matter);
        tv_view.setText("View");
//        siv_upload = view.findViewById(R.id.upload_icon);
//        siv_view = view.findViewById(R.id.view_icon);
        // loadViewUI();
        if (Constants.MATTER_TYPE.equals("Legal"))
            loadLegalMatter();
        else if (Constants.MATTER_TYPE.equals("General")) {
            loadGeneralMatter();
        } else loadLegalMatter();

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
//                loadgeneralUI();
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
                if (matter_arraylist.size() == 0) {
                    AndroidUtils.showAlert("Please check the Matter information section", getContext());
                } else {
                    loadGCT();

                }
            }
        });
        siv_documents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (matter_arraylist.size() == 0) {

                    AndroidUtils.showAlert("Please check the Matter information,Groups,clients,team member section", getContext());
                } else {
                    loadDocuments();
                }
            }
        });
        matter_arraylist = new ArrayList<>();

        return view;
    }

    public ArrayList<MatterModel> getMatter_arraylist() {
        return matter_arraylist;
    }

    //    public MatterModel matterModel
    void loadViewUI() {
        tv_create.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_left_background));
        tv_create.setTextColor(Color.BLACK);
        tv_view.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_right_green_count));
        tv_view.setTextColor(Color.WHITE);
        create_matter_view.setVisibility(View.GONE);
        viewMatter();
        mViewModel.setData("View Legal Matter");
    }

    private void loadViewgeneralUI() {
        tv_create.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_left_background));
        tv_create.setTextColor(Color.BLACK);
        tv_view.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_right_green_count));
        tv_view.setTextColor(Color.WHITE);
        create_matter_view.setVisibility(View.GONE);
        viewMatter();
        mViewModel.setData("View General Matter");
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
        tv_create.setTextColor(Color.WHITE);
        tv_view.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_right_background));
        tv_view.setTextColor(Color.BLACK);
        loadMatterInformation();
        mViewModel.setData("Create Legal Matter");
    }

    private void loadgeneralUI() {
        create_matter_view.setVisibility(View.VISIBLE);
        tv_create.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_left_green_background));
        tv_create.setTextColor(Color.WHITE);
        tv_view.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_right_background));
        tv_view.setTextColor(Color.BLACK);
        loadMatterInformation();
        mViewModel.setData("Create  General Matter");
    }

    void loadLegalMatter() {
        Constants.MATTER_TYPE = "Legal";
        tv_legal_matter.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_left_green_background));
        tv_legal_matter.setTextColor(Color.WHITE);
        tv_general_matter.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_right_background));
        tv_general_matter.setTextColor(Color.BLACK);
        loadMatterInformation();
        loadViewUI();
        mViewModel.setData(" View Legal Matter");
    }

    void loadGeneralMatter() {
        Constants.MATTER_TYPE = "General";
        tv_legal_matter.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_left_background));
        tv_legal_matter.setTextColor(Color.BLACK);
        tv_general_matter.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_right_green_count));
        tv_general_matter.setTextColor(Color.WHITE);
        loadMatterInformation();
        loadViewUI();

        mViewModel.setData("    View General Matter");
    }

    public void loadDocuments() {
        siv_matter_icon.setImageDrawable(getContext().getResources().getDrawable(R.mipmap.single_document_icon));
        siv_groups.setImageDrawable(getContext().getResources().getDrawable(R.mipmap.frame_white_background));

        siv_documents.setImageDrawable(getContext().getResources().getDrawable(R.mipmap.green_document));
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        MatterDocuments matterInformation = new MatterDocuments();
        ft.replace(R.id.child_container, matterInformation);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.addToBackStack(null);
        ft.commit();
    }

    public void loadGCT() {
        siv_matter_icon.setImageDrawable(getContext().getResources().getDrawable(R.mipmap.single_document_icon));
        siv_groups.setImageDrawable(getContext().getResources().getDrawable(R.mipmap.group_green_background));
        siv_documents.setImageDrawable(getContext().getResources().getDrawable(R.mipmap.white_document));
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        Fragment childFragment = new GCT();
//        tv_matter_title = findViewById(R.id.tv_matter_title);
//        String text = tv_matter_title.getText().toString();
//        matter_title.setText(text);
        //  FragmentManager childFragmentManager = getChildFragmentManager();
        ft.replace(R.id.child_container, childFragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.addToBackStack(null);
        ft.commit();
        //  AndroidUtils.showAlert("Please check the Matter Information section");

        // cv_add_opponent_advocate.setVisibility(View.GONE);

        // childFragmentManager.beginTransaction().add(R.id.child_container, childFragment).commit();
    }

    void loadMatterInformation() {
        siv_matter_icon.setImageDrawable(getContext().getResources().getDrawable(R.mipmap.single_document_icon_white));
        siv_groups.setImageDrawable(getContext().getResources().getDrawable(R.mipmap.frame_white_background));
        siv_groups.setClickable(true);
        siv_documents.setImageDrawable(getContext().getResources().getDrawable(R.mipmap.white_document));
        siv_documents.setClickable(true);
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        MatterInformation matterInformation = new MatterInformation();
        ft.replace(R.id.child_container, matterInformation);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.addToBackStack(null);
        ft.commit();
    }

    public void loadMatterInformmation() {
        siv_matter_icon.setImageDrawable(getContext().getResources().getDrawable(R.mipmap.single_document_icon_white));
        siv_groups.setImageDrawable(getContext().getResources().getDrawable(R.mipmap.frame_white_background));
        siv_groups.setClickable(true);
        siv_documents.setImageDrawable(getContext().getResources().getDrawable(R.mipmap.white_document));
        siv_documents.setClickable(true);
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        MatterInformation matterInformation = new MatterInformation();
        ft.replace(R.id.child_container, matterInformation);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.addToBackStack(null);
        ft.commit();

    }
}
