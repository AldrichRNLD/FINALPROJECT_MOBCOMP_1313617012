package com.mobcom1313617012.dudetracker.view.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.mobcom1313617012.dudetracker.R;
import com.mobcom1313617012.dudetracker.model.User;
import com.mobcom1313617012.dudetracker.repo.Database;
import com.mobcom1313617012.dudetracker.view.MainActivity;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.FriendViewHolder> {
    private final ArrayList<User> users;
    private final Context ctx;

    public FriendAdapter(Context ctx, ArrayList<User> users) {
        this.users = users;
        this.ctx = ctx;
    }

    @NonNull
    @NotNull
    @Override
    public FriendViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_item, parent, false);
        return new FriendViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull FriendViewHolder holder, int position) {
        User user = users.get(position);
        holder.name.setText(user.name);
        String status = "Online";
        if(user.status == 0)
            status = "Offline";
        holder.status.setText(status);
        holder.itemView.setOnClickListener(v -> {
            if(ctx instanceof MainActivity) {
                GoogleMap gmap = ((MainActivity) ctx).gmap;
                if(gmap != null && user.lat != 0 && user.lng != 0) {
                    try {
                        gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(user.lat, user.lng), 20.0F));
                    } catch (Exception ignored) {}
                }
            }
        });
        holder.itemView.setOnLongClickListener(v -> {
            AlertDialog dialog = new AlertDialog.Builder(ctx)
                    .setTitle("Delete a Friend")
                    .setMessage("Are you sure want to delete "+user.name+" from friend list?")
                    .setNegativeButton("Cancel", ((dialog1, which) -> {}))
                    .setPositiveButton("Ok", ((dialog1, which) -> {
                        new Database(ctx).delete(user);
                        users.remove(position);
                        notifyItemRemoved(position);
                    })).create();
            dialog.show();
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class FriendViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView status;
        public FriendViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.item_name);
            status = itemView.findViewById(R.id.item_status);
        }
    }
}
