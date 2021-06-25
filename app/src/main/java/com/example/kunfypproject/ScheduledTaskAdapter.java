package com.example.kunfypproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


public class ScheduledTaskAdapter extends RecyclerView.Adapter<com.example.kunfypproject.ScheduledTaskAdapter.MyViewHolder> {
    List<ScheduledTask> ls;
    Context c;
    public ScheduledTaskAdapter(List<ScheduledTask> ls, Context c) {
        this.c=c;
        this.ls=ls;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(c).inflate(R.layout.scheduledtask_row, parent, false);
        return new MyViewHolder((itemView));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.description.setText(ls.get(position).getDescription());
    }

    @Override
    public int getItemCount() {
        return ls.size();
    }



    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView description;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            description=itemView.findViewById(R.id.task_description);
        }
    }
}
