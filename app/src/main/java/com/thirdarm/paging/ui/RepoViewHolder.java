package com.thirdarm.paging.ui;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.thirdarm.paging.R;
import com.thirdarm.paging.model.Repo;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * View Holder for a [Repo] RecyclerView list item.
 */
public class RepoViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.repo_name)
    TextView name;

    @BindView(R.id.repo_description)
    TextView description;

    @BindView(R.id.repo_stars)
    TextView stars;

    @BindView(R.id.repo_language)
    TextView language;

    @BindView(R.id.repo_forks)
    TextView forks;

    private Repo repo = null;

    private RepoViewHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (repo != null && repo.url != null) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(repo.url));
                    v.getContext().startActivity(intent);
                }
            }
        });
    }

    public static RepoViewHolder createViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.repo_view_item, parent, false);
        return new RepoViewHolder(view);
    }

    public void bind(@Nullable Repo repo) {
        if (repo == null) {
            Resources resources = itemView.getResources();
            name.setText(resources.getString(R.string.loading));
            description.setVisibility(View.GONE);
            language.setVisibility(View.GONE);
            stars.setText(resources.getString(R.string.unknown));
            forks.setText(resources.getString(R.string.unknown));
        } else {
            showRepoData(repo);
        }
    }

    private void showRepoData(Repo repo) {
        this.repo = repo;
        name.setText(repo.fullName);

        // if the description is missing, hide the textview
        int descriptionVisibility = View.GONE;
        if (repo.description != null) {
            description.setText(repo.description);
            descriptionVisibility = View.VISIBLE;
        }
        description.setVisibility(descriptionVisibility);

        stars.setText(String.format("%s",repo.stars));
        forks.setText(String.format("%s",repo.forks));

        // if the language is missing, hide the label and the value
        int languageVisibility = View.GONE;
        if (repo.language != null && !repo.language.isEmpty()) {
            Resources resources = this.itemView.getContext().getResources();
            language.setText(resources.getString(R.string.language, repo.language));
            languageVisibility = View.VISIBLE;
        }
        language.setVisibility(languageVisibility);
    }

}
