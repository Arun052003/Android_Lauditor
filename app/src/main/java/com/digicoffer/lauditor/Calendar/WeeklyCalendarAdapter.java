package com.digicoffer.lauditor.Calendar;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.digicoffer.lauditor.Calendar.Models.Day;
import com.digicoffer.lauditor.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class WeeklyCalendarAdapter extends RecyclerView.Adapter<WeeklyCalendarAdapter.DayViewHolder> {

    private List<Day> days;
    private OnDaySelectedListener listener;
    private int selectedPosition = RecyclerView.NO_POSITION;

    public WeeklyCalendarAdapter(List<Day> days, OnDaySelectedListener listener) {
        this.days = days;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.weekly_view_dates, parent, false);
        return new DayViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull DayViewHolder holder, int position) {
        Day day = days.get(position);
        Log.d("Days_size", "" + days.size() + "...." + day.hasEvents());
//        for (int i = 0; i < days.size(); i++) {
//            if (day.getEvents().size() > 0) {
//                holder.dot.setVisibility(View.VISIBLE); // Show the dot
//            } else {
//                holder.dot.setVisibility(View.GONE); // Hide the dot
//            }
//        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        try {
            Date parsedDate = dateFormat.parse(day.getDate());
            SimpleDateFormat dayFormat = new SimpleDateFormat("dd", Locale.US);
            String new_day = dayFormat.format(parsedDate); // "21"
            holder.textDay.setText(new_day);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Log.d("Event_list_size", "" + day.getEvents().size());
        //Set a background drawable to indicate that the day has events


        holder.itemView.setOnClickListener(view -> {
            int previousSelectedPosition = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(previousSelectedPosition);
            notifyItemChanged(selectedPosition);
            if (listener != null) {
                listener.onDaySelected(day);
            }
        });
        // Change the background color to blue if it is today's date
        if (day.isToday()) {
            holder.itemView.setBackgroundDrawable(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.rectangular_complete_blue_background));
            holder.textDay.setTextColor(Color.WHITE);
        } else {
            // Reset the background color for other days
            holder.itemView.setBackgroundResource(R.drawable.rectangle_light_grey_bg);
            holder.textDay.setTextColor(Color.BLACK);
            if (selectedPosition == position) {
                holder.itemView.setBackgroundDrawable(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.rectangular_button_green_count));
                holder.textDay.setTextColor(Color.WHITE);
            }
        }
        //Check for Event Status
        if (day.hasEvents()) {
            holder.dot.setVisibility(View.VISIBLE); // Show the dot
        } else {
            // Reset the background drawable for other days
            holder.dot.setVisibility(View.GONE); // Show the dot
        }
    }

    @Override
    public int getItemCount() {
        return days.size();
    }

    public static class DayViewHolder extends RecyclerView.ViewHolder {
        public TextView textDay;
        public View dot;

        public DayViewHolder(View itemView) {
            super(itemView);
            textDay = itemView.findViewById(R.id.textDay);
            dot = itemView.findViewById(R.id.dot);
        }
    }

    // Interface to handle day selection
    public interface OnDaySelectedListener {
        void onDaySelected(Day day);
    }
}