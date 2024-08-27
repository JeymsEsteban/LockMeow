package com.example.lockmeow;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class appAdapter extends RecyclerView.Adapter<appAdapter.ViewHolder> {

    private final List<appModel> appModels;
    private final Context context;

    public appAdapter(List<appModel> appModels, Context context) {
        this.appModels = appModels;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.app_adapter_design, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        appModel app = appModels.get(position);
        holder.appName.setText(app.getappName());
        holder.appIcon.setImageDrawable(app.getappIcon());
        holder.appStatus.setImageResource(app.getappStatus() == 0 ? R.drawable.unlock_icon : R.drawable.lock_icon);

        holder.itemView.setOnClickListener(v -> {
            if (app.getappStatus() == 0) {
                app.setStatus(1);
                holder.appStatus.setImageResource(R.drawable.lock_icon);
                Toast.makeText(context, app.getappName() + " se bloqueó", Toast.LENGTH_SHORT).show();
                SharedPreferencies.getInstance(context).agregarAppBloqueada(app.getnamePackage(), context);
            } else {
                app.setStatus(0);
                holder.appStatus.setImageResource(R.drawable.unlock_icon);
                Toast.makeText(context, app.getappName() + " se desbloqueó", Toast.LENGTH_SHORT).show();
                SharedPreferencies.getInstance(context).agregarAppDesbloqueada(app.getnamePackage(), context);
            }
        });
    }

    @Override
    public int getItemCount() {
        return appModels.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView appName;
        ImageView appIcon, appStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            appName = itemView.findViewById(R.id.appName);
            appIcon = itemView.findViewById(R.id.appIcon);
            appStatus = itemView.findViewById(R.id.appStatus);
        }
    }
}


