package com.example.musique.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musique.view.FolderSongs;
import com.example.musique.R;
import com.example.musique.utils.Constants;
import com.example.musique.utils.Functions;

import java.util.ArrayList;

public class FolderListAdapter extends RecyclerView.Adapter<FolderListAdapter.ViewHolder> {

    ArrayList<String> folderList;
    Context context;

    public FolderListAdapter(ArrayList<String> folderList, Context context) {
        this.context = context;
        this.folderList = folderList;
    }


    @NonNull
    @Override
    public FolderListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.list_item_folders, parent, false);
        return new FolderListAdapter.ViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull FolderListAdapter.ViewHolder holder, int position) {
        String folder = folderList.get(position);
        holder.folderName.setText(Functions.decryptName(folder));
        holder.parentView.setOnClickListener(v -> {
            context.startActivity(new Intent(context, FolderSongs.class).putExtra(Constants.FOLDER, folder));
        });
    }

    @Override
    public int getItemCount() {
        return folderList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public LinearLayout parentView;
        public TextView folderName;

        public ViewHolder(View itemView) {
            super(itemView);
            this.parentView = itemView.findViewById(R.id.layout_parent);
            this.folderName = itemView.findViewById(R.id.txt_folder_name);
        }
    }
}
