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
import java.util.ArrayList;
import java.util.List;


public class appAdapter extends RecyclerView.Adapter<appAdapter.ViewHolder> {

    List<appModel> appModels = new ArrayList<>();
    Context con;
    List<String> appsBloqueadas;

    public appAdapter(List<appModel> appModels, Context con){
        this.appModels = appModels;
        this.con = con;
        this.appsBloqueadas = SharedPreferencies.getInstance(con).getListString();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(con).inflate(R.layout.app_adapter_design,parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        appModel app = appModels.get(position);

        holder.appName.setText(app.getappName());
        holder.appIcon.setImageDrawable(app.getappIcon());

        if (app.getappStatus() == 0){
            holder.appStatus.setImageResource(R.drawable.unlock_icon);
        }

        else{
            holder.appStatus.setImageResource(R.drawable.lock_icon);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                if (appsBloqueadas.contains(app.getnamePackage())) {
                    app.setStatus(0);
                    holder.appStatus.setImageResource(R.drawable.unlock_icon);
                    Toast.makeText(con, app.getappName() + " se desbloqueó", Toast.LENGTH_SHORT).show();
                    appsBloqueadas.remove(app.getnamePackage());
                } else {
                    app.setStatus(1);
                    holder.appStatus.setImageResource(R.drawable.lock_icon);
                    Toast.makeText(con, app.getappName() + " se bloqueó", Toast.LENGTH_SHORT).show();
                    appsBloqueadas.add(app.getnamePackage());
                }
                // Update SharedPreferencies after modification
                SharedPreferencies.getInstance(con).putListString(appsBloqueadas);
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




