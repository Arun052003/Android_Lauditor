package com.digicoffer.lauditor.Calendar;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.digicoffer.lauditor.Calendar.Models.TeamDo;
import com.digicoffer.lauditor.R;

import org.bouncycastle.jcajce.provider.symmetric.TEA;

import java.util.ArrayList;

public class AutocompleteAdapter extends ArrayAdapter<TeamDo> {

    private Context context;
    private ArrayList<TeamDo> items, tempItems, suggestions;
    ;

//    public BusinessAutocompleteAdapter(@NonNull Context context, int resource) {
//        super(context, resource);
//    }

    public AutocompleteAdapter(Context context, int resourceId, ArrayList<TeamDo> arrayList) {
        super(context, resourceId, arrayList);
        this.context = context;
        this.items = arrayList;
        tempItems = new ArrayList<>(items);
        suggestions = new ArrayList<>();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        try {
            if (convertView == null) {
                LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                view = inflater.inflate(R.layout.list_item, parent, false);
            }
            TeamDo biz = getItem(position);
            TextView name = (TextView) view.findViewById(R.id.tv_Name);
            name.setText(biz.getName());
        } catch (Exception e) {
            e.fillInStackTrace();
        }
        return view;
    }

    @Override
    public TeamDo getItem(int position) {
        return items.get(position);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Filter getFilter() {
        return fruitFilter;
    }

    private Filter fruitFilter = new Filter() {
        @Override
        public CharSequence convertResultToString(Object resultValue) {
            TeamDo fruit = (TeamDo) resultValue;
            return fruit.getName();
        }

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            if (charSequence != null) {
                suggestions.clear();
                for (TeamDo biz : tempItems) {
                    if (biz.getName().toLowerCase().contains(charSequence.toString().toLowerCase())) {
                        suggestions.add(biz);
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();
                return filterResults;
            } else {
                return new FilterResults();
            }
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            ArrayList<TeamDo> tempValues = (ArrayList<TeamDo>) filterResults.values;
//            if (filterResults != null && filterResults.count >= 0) {
                clear();
                for (TeamDo fruitObj : tempValues) {
                    add(fruitObj);
                    notifyDataSetChanged();
//                }
//            } else {
//                clear();
//                notifyDataSetChanged();
            }
        }
    };
}
