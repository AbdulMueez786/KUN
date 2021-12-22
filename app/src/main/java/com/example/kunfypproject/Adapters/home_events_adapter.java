package com.example.kunfypproject.Adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.kunfypproject.R;
import com.example.kunfypproject.Models.Schedule;
import com.squareup.picasso.Picasso;

import java.util.List;

public class home_events_adapter extends RecyclerView.Adapter<home_events_adapter.MyViewHolder> {
    private List<Schedule> ls;
    private Context c;
    public home_events_adapter(Context c, List<Schedule> ls){
        this.c = c;
        this.ls = ls;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(c).inflate(R.layout.row_item,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Picasso.get().load(Uri.parse(ls.get(position).getStatus())).into(holder.imageView);
        holder.textView.setText(ls.get(position).getName());

    }

    @Override
    public int getItemCount() {
        return ls.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view);
            textView = itemView.findViewById(R.id.text_view);
        }
    }
}
