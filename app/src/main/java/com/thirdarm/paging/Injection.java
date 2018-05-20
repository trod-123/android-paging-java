package com.thirdarm.paging;

import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;

import com.thirdarm.paging.api.GithubService;
import com.thirdarm.paging.api.GithubServiceUtils;
import com.thirdarm.paging.data.GithubRepository;
import com.thirdarm.paging.db.GithubLocalCache;
import com.thirdarm.paging.db.RepoDatabase;
import com.thirdarm.paging.ui.ViewModelFactory;

import java.util.concurrent.Executors;

/**
 * Class that handles object creation.
 * Like this, objects can be passed as parameters in the constructors and then replaced for
 * testing, where needed.
 */
public class Injection {

    private static GithubLocalCache provideCache(Context context) {
        RepoDatabase database = RepoDatabase.getInstance(context);
        return new GithubLocalCache(database.reposDao(), Executors.newSingleThreadExecutor());
    }

    private static GithubRepository provideGithubRepository(Context context) {
        return new GithubRepository(GithubServiceUtils.create(), provideCache(context));
    }

    public static ViewModelProvider.Factory provideViewModelFactory(Context context) {
        return new ViewModelFactory(provideGithubRepository(context));
    }
}
