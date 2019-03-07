package com.restri_tech.Adapter;

import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.restri_tech.R;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class MyArrayAdapter extends RecyclerView.Adapter {
    List<app> apps;
    PackageManager packageManager;


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        app ai = apps.get(position);
        initializeViews(ai, holder, position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView t;
        public ImageView im;
        CheckBox checkBox;
        RelativeLayout rv;

        public ViewHolder(View view) {
            super(view);
            checkBox = view.findViewById(R.id.checkbox);
            t = view.findViewById(R.id.text11);
            im = view.findViewById(R.id.image1);
            rv = view.findViewById(R.id.rw);
        }

    }

    public MyArrayAdapter(PackageManager p, List<app> apps) {
        packageManager = p;
        this.apps = apps;
    }

    @Override
    public int getItemCount() {
        return apps.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row, parent, false);
        return new ViewHolder(v);
    }

    private void initializeViews(final app ai, final RecyclerView.ViewHolder holder, int position) {
        ((ViewHolder) holder).t.setText(ai.aiGet().loadLabel(packageManager));
        ((ViewHolder) holder).im.setImageDrawable(ai.aiGet().loadIcon(packageManager));
        ((ViewHolder) holder).checkBox.setChecked(ai.isSelected());
        ((ViewHolder) holder).checkBox.setTag(new Integer(position));
        ((ViewHolder) holder).rv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox cb = (CheckBox) view.findViewById(R.id.checkbox);
                int clickedPos = ((Integer) cb.getTag()).intValue();
                cb.setChecked(!apps.get(clickedPos).isSelected());
                apps.get(clickedPos).setSelected(cb.isChecked());
                notifyDataSetChanged();
            }
        });
        ((ViewHolder) holder).checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox cb = (CheckBox) view;
                int clickedPos = ((Integer) cb.getTag()).intValue();
                apps.get(clickedPos).setSelected(cb.isChecked());
                notifyDataSetChanged();
            }
        });
    }

    public List<String> getSelectedItem() {
        List<String> itemModelList = new ArrayList<>();
        for (int i = 0; i < apps.size(); i++) {
            app a = apps.get(i);
            if (a.isSelected()) {
                itemModelList.add(a.aiGet().processName);
            }
        }
        return itemModelList;
    }

    public void filterList(List<app> filterdNames) {
        this.apps = filterdNames;
        notifyDataSetChanged();
    }
}
