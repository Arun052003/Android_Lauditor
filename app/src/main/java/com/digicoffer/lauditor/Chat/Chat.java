package com.digicoffer.lauditor.Chat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.digicoffer.lauditor.R;
import com.digicoffer.lauditor.Webservice.AsyncTaskCompleteListener;
import com.digicoffer.lauditor.Webservice.HttpResultDo;

public class Chat extends Fragment implements AsyncTaskCompleteListener {

    TextView tv_create_event,tv_view_calendar;
    @Override
    public void onClick(View view) {

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chat,container,false);
        tv_create_event = view.findViewById(R.id.tv_create_event);
        tv_view_calendar = view.findViewById(R.id.tv_view_calendar);
        loadClient();
        tv_create_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadClient();;
            }
        });
        tv_view_calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadTeams();
            }
        });
        return view;
    }

    private void loadClient() {
        tv_view_calendar.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_right_background));
        tv_create_event.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_left_green_background));
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(
                R.anim.fade_in,  // Enter animation
                R.anim.fade_out, // Exit animation
                R.anim.slide_in, // Pop-enter animation
                R.anim.slide_out // Pop-exit animation
        );
        Clients clients = new Clients();
        fragmentTransaction.replace(R.id.child_container_timesheets,clients);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
    private void  loadTeams(){
        tv_view_calendar.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_right_green_count));
        tv_create_event.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_left_background));
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(
                R.anim.fade_in,  // Enter animation
                R.anim.fade_out, // Exit animation
                R.anim.slide_in, // Pop-enter animation
                R.anim.slide_out // Pop-exit animation
        );
        Teams clients = new Teams();
        fragmentTransaction.replace(R.id.child_container_timesheets,clients);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }

    @Override
    public void onAsyncTaskComplete(HttpResultDo httpResult) {

    }

}
