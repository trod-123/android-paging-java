package com.thirdarm.paging.db;

import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.arch.persistence.room.Dao;
import android.util.Log;

import com.thirdarm.paging.model.Repo;

import java.util.List;
import java.util.concurrent.Executor;

/**
 * Class that handles the DAO local data source. This ensures that methods are triggered on the
 * correct executor.
 */
public class GithubLocalCache {
    private RepoDao repoDao;
    private Executor ioExecutor;

    public GithubLocalCache(RepoDao repoDao, Executor ioExecutor) {
        this.repoDao = repoDao;
        this.ioExecutor = ioExecutor;
    }

    /**
     * Inserts a list of repos in the database on a background thread
     * @param repos
     */
    public void insert(final List<Repo> repos, final InsertFinishedListener listener) {
        ioExecutor.execute(new Runnable() {
            @Override
            public void run() {
                Log.d("GithubLocalCache", String.format("Inserting %s repos", repos.size()));
                repoDao.insert(repos);
                listener.onInsertFinished();
            }
        });
    }

    /**
     * Request a LiveData<List<Repo>> from the Dao, based on a repo name. If the name contains
     * multiple words separated by spaces, then we're emulating the GitHub API behavior and allow
     * any characters between the words.
     * @param name repository name
     */
    public DataSource.Factory<Integer, Repo> reposByName(String name) { // previously returned LiveData<List<Repo>>
        // appending '%' so we can allow other characters to be before and after the query string
        String query = String.format("%%%s%%", name.replace(' ', '%'));
        return repoDao.reposByName(query);
    }

    public interface InsertFinishedListener {
        void onInsertFinished();
    }
}
