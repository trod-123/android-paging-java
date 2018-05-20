package com.thirdarm.paging.ui;

import android.support.annotation.NonNull;
import android.support.v7.recyclerview.extensions.ListAdapter;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.thirdarm.paging.model.Repo;

/**
 * Adapter for the list of repositories.
 */
public class ReposAdapter extends ListAdapter<Repo, RecyclerView.ViewHolder> {

    ReposAdapter() {
        super(new DiffUtil.ItemCallback<Repo>() {
            @Override
            public boolean areItemsTheSame(Repo oldItem, Repo newItem) {
                return oldItem.fullName.equals(newItem.fullName);
            }

            @Override
            public boolean areContentsTheSame(Repo oldItem, Repo newItem) {
                return oldItem.equals(newItem);
            }
        });
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return RepoViewHolder.createViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Repo repoItem = getItem(position);
        if (repoItem != null) {
            ((RepoViewHolder) holder).bind(repoItem);
        }
    }
}
