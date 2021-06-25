package com.example.kunfypproject;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kunfypproject.Fragments.HomeFragment;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MyRVAdapter1 extends RecyclerView.Adapter<com.example.kunfypproject.MyRVAdapter1.MyViewHolder1> {

    private List<Appliance> ls1;
    private Context c;
    private HomeFragment.ConnectedThread mConnectedThread1;
    private HomeFragment.ConnectedThread mConnectedThread2;
    private HomeFragment.ConnectedThread mConnectedThread3;
    private HomeFragment.ConnectedThread mConnectedThread4;

    private boolean led_status=false;
    private boolean Fan_status=false;
    private boolean Door_status=false;
    private boolean Window_status=false;

    public MyRVAdapter1(Context c, List<Appliance> ls1,HomeFragment.ConnectedThread mConnectedThread1,
                        HomeFragment.ConnectedThread mConnectedThread2,HomeFragment.ConnectedThread mConnectedThread3
            ,HomeFragment.ConnectedThread mConnectedThread4){
        this.c = c;
        this.ls1 = ls1;
        this.mConnectedThread1=mConnectedThread1;
        this.mConnectedThread2=mConnectedThread2;
        this.mConnectedThread3=mConnectedThread3;
        this.mConnectedThread4 = mConnectedThread4;
    }

    @NonNull
    @Override
    public MyViewHolder1 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item1,parent,false);
        return new MyViewHolder1(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder1 holder, final int position) {
        Picasso.get().load(Uri.parse(ls1.get(position).getImage())).into(holder.app_image);
               holder.app_image.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {

                       if(position==0){
                           System.out.println("in Fan block");
                           if(Fan_status==false){
                               System.out.println("in Fan block ON");
                               Fan_status=true;
                               mConnectedThread1.write("<Fan_ON>");
                           }
                           else{
                               System.out.println("in Fan block OFF");
                               Fan_status=false;
                               mConnectedThread1.write("<Fan_OFF>");
                           }
                       }
                       else if (position==1){
                           System.out.println("in door block");
                           if(Door_status==false){
                               System.out.println("in door block open");
                               Door_status=true;
                               mConnectedThread2.write("<Door_Open>");
                           }
                           else{
                               System.out.println("in door block closed");
                               Door_status=false;
                               mConnectedThread2.write("<Door_Close>");
                           }
                       }
                       else if (position==2){
                           System.out.println("in light block");
                           if(led_status==false){
                               System.out.println("in light block on ");
                               led_status=true;
                               mConnectedThread3.write("<Light_ON>");
                           }
                           else{
                               System.out.println("in light block off ");
                               led_status=false;
                               mConnectedThread3.write("<Light_OFF>");
                           }
                       }
                       else if (position==3){
                           System.out.println("in window block");
                           if(Window_status==false){
                               System.out.println("in window block open");
                               Window_status=true;
                               mConnectedThread4.write("<Window_Open>");
                           }
                           else{
                               System.out.println("in window block close");
                               Window_status=false;
                               mConnectedThread4.write("<Window_Close>");
                           }
                       }

                   }
               });
    }


    @Override
    public int getItemCount() {
        return ls1.size();
    }

    public class MyViewHolder1 extends RecyclerView.ViewHolder {
        ImageView app_image;
        public MyViewHolder1(@NonNull View itemView) {
            super(itemView);
            app_image = itemView.findViewById(R.id.app_image);
        }
    }
}
