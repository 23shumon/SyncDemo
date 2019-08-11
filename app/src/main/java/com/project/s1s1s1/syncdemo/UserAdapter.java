package com.project.s1s1s1.syncdemo;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private static final String TAG = UserAdapter.class.getSimpleName();
    private List<User> userList;

    public UserAdapter(List<User> userList) {
        this.userList = userList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.nameTV.setText(userList.get(position).getName());
        int sync_status=userList.get(position).getSync_status();
        if (sync_status==Constant.SYNC_STATUS_OK) {
            holder.statusIV.setImageResource(R.drawable.ok3);
        }else {
            holder.statusIV.setImageResource(R.drawable.sync3);
        }
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView nameTV;
        private ImageView statusIV;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTV=itemView.findViewById(R.id.nameTV);
            statusIV=itemView.findViewById(R.id.statusIV);
        }
    }
}
