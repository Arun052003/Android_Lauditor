package com.digicoffer.lauditor.TimeSheets.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.digicoffer.lauditor.R;
import com.digicoffer.lauditor.TimeSheets.Models.ProjectTMModel;
import com.digicoffer.lauditor.TimeSheets.Models.ProjectsModel;

import org.minidns.record.A;

import java.util.ArrayList;

public class ProjectAdapter extends RecyclerView.Adapter<ProjectAdapter.MyviewHolder> {
   ArrayList<ProjectsModel> projectList = new ArrayList<>();
   ArrayList<ProjectTMModel> projectTmList = new ArrayList<>();
    Context context;
    public ProjectAdapter(ArrayList<ProjectsModel> projectList, ArrayList<ProjectTMModel> projectTmList,Context cContext) {
        this.projectList = projectList;
        this.projectTmList = projectTmList;
        this.context = cContext;
    }

    @NonNull
    @Override
    public ProjectAdapter.MyviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.projects_recyclerview,parent,false);

        return new  ProjectAdapter.MyviewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProjectAdapter.MyviewHolder holder, int position) {
            ProjectsModel projectsModel =  projectList.get(position);
            holder.tv_project_case_number.setText(projectsModel.getCaseNo());
            holder.tv_project_name.setText(projectsModel.getProjectName());
            holder.rv_tm_projects.setLayoutManager(new GridLayoutManager(context,1));
            String status = "Projects";
            ProjectTMAdapter projectTMAdapter = new ProjectTMAdapter(projectTmList);
            holder.rv_tm_projects.setAdapter(projectTMAdapter);
            holder.rv_tm_projects.setHasFixedSize(true);
            notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return projectList.size();
    }

    public class MyviewHolder extends RecyclerView.ViewHolder {
TextView tv_project_case_number,tv_project_name;
RecyclerView rv_tm_projects;
        public MyviewHolder(@NonNull View itemView) {
            super(itemView);
            tv_project_case_number = itemView.findViewById(R.id.tv_project_case_number);
            tv_project_name = itemView.findViewById(R.id.tv_project_name);
            rv_tm_projects = itemView.findViewById(R.id.rv_tm_projects);
        }
    }
}
