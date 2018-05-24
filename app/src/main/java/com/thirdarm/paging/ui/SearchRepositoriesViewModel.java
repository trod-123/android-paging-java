package com.thirdarm.paging.ui;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.arch.paging.PagedList;
import android.support.annotation.Nullable;

import com.thirdarm.paging.data.GithubRepository;
import com.thirdarm.paging.model.Repo;
import com.thirdarm.paging.model.RepoSearchResult;

import java.util.List;

/**
 * ViewModel for the [SearchRepositoriesActivity] screen.
 * The ViewModel works with the [GithubRepository] to get the data.
 */
public class SearchRepositoriesViewModel extends ViewModel {
    private GithubRepository repository;

    SearchRepositoriesViewModel(GithubRepository repository) {
        this.repository = repository;
    }

    private static final int VISIBLE_THRESHOLD = 5;

    private MutableLiveData<String> queryLiveData = new MutableLiveData<>();
    private LiveData<RepoSearchResult> repoResult = Transformations.map(queryLiveData, new Function<String, RepoSearchResult>() {
        @Override
        public RepoSearchResult apply(String input) {
            return repository.search(input);
        }
    });

    public LiveData<PagedList<Repo>> repos = Transformations.switchMap(repoResult, new Function<RepoSearchResult, LiveData<PagedList<Repo>>>() {
        @Override
        public LiveData<PagedList<Repo>> apply(RepoSearchResult input) {
            return input.getData();
        }
    });

    public LiveData<String> networkErrors = Transformations.switchMap(repoResult, new Function<RepoSearchResult, LiveData<String>>() {
        @Override
        public LiveData<String> apply(RepoSearchResult input) {
            return input.getNetworkErrors();
        }
    });

    /**
     * Search a repository based on a query string
     */
    public void searchRepo(String queryString) {
        queryLiveData.postValue(queryString);
    }

    // For detecting when user reaches the end of the list, load more data
    // Not needed when using PagedLists as PagedLists does it itself
//    public void listScrolled(int visibleItemCount, int lastVisibleItemPosition, int totalItemCount) {
//        if (visibleItemCount + lastVisibleItemPosition + VISIBLE_THRESHOLD >= totalItemCount) {
//            String immutableQuery = lastQueryValue();
//            if (immutableQuery != null) {
//                repository.requestMore(immutableQuery);
//            }
//        }
//    }

    /**
     * Get the last query value
     * @return
     */
    @Nullable
    public String lastQueryValue() {
        return queryLiveData.getValue();
    }
}
