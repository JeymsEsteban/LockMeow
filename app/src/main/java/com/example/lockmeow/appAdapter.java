package com.example.lockmeow;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;


public class appAdapter extends RecyclerView.Adapter<appAdapter.adapter_design_java> {


    List<appModel> appModels = new ArrayList<>();
    Context con;

    public appAdapter(List<appModel> appModels, Context con){
        this.appModels = appModels;
        this.con = con;
    }

    @NonNull
    @Override
    public adapter_design_java onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(con).inflate(R.layout.app_adapter_design,parent, false);
        adapter_design_java diseño = new adapter_design_java(view);
        return diseño;
    }

    @Override
    public void onBindViewHolder(@NonNull adapter_design_java holder, int position) {
        appModel app = appModels.get(position);

        holder.appName.setText(app.getappName());
        holder.appIcon.setImageDrawable(app.getappIcon());

        if (app.getappStatus() == 0){

        }

        else{

        }


    }

    @Override
    public int getItemCount() {
        return appModels.size();
    }

    public class adapter_design_java extends RecyclerView.ViewHolder {

        TextView appName;
        ImageView appIcon, appStatus;
        public adapter_design_java(@NonNull View itemView) {
            super(itemView);
            appName = itemView.findViewById(R.id.appName);
            appIcon = itemView.findViewById(R.id.appIcon);
            appStatus = itemView.findViewById(R.id.appStatus);
        }
    }
}
