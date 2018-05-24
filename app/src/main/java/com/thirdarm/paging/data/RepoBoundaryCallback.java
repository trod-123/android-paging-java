package com.thirdarm.paging.data;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.PagedList;
import android.support.annotation.NonNull;

import com.thirdarm.paging.api.GithubService;
import com.thirdarm.paging.api.GithubServiceUtils;
import com.thirdarm.paging.db.GithubLocalCache;
import com.thirdarm.paging.model.Repo;

import java.util.List;

public class RepoBoundaryCallback extends PagedList.BoundaryCallback<Repo>
        implements GithubServiceUtils.StatusCallback {

    // GRABBED FROM GITHUBREPOSITORY.JAVA

    // keep the last updated page. When the request is successful, increment the page #
    private int lastRequestedPage = 1;

    // LiveData of network errors
    // Previously MutableLiveData<String>. In this class we can use MutableLiveData, but when accessing
    // outside of this class, we only expose LiveData object (non-modifiable)
    private MutableLiveData<String> networkErrors = new MutableLiveData<>();

    public LiveData<String> getNetworkErrors() {
        return networkErrors;
    }

    // Avoid triggering multiple requests in the same time
    private boolean isRequestInProgress = false;

    public static final int NETWORK_PAGE_SIZE = 50;


    private String query;
    private GithubService service;
    private GithubLocalCache cache;

    public RepoBoundaryCallback(String query, GithubService service, GithubLocalCache cache) {
        this.query = query;
        this.service = service;
        this.cache = cache;
    }

    @Override
    public void onZeroItemsLoaded() {
        super.onZeroItemsLoaded();
        requestAndSaveData(query);
    }

    @Override
    public void onItemAtEndLoaded(@NonNull Repo itemAtEnd) {
        super.onItemAtEndLoaded(itemAtEnd);
        requestAndSaveData(query);
    }

    /**
     * ORIGINALLY FROM GITHUBREPOSITORY.JAVA
     * Makes another network call to load and display more data. Last requested page is used to
     * determine the next page of data to query
     *
     * @param query
     */
    private void requestAndSaveData(String query) {
        if (isRequestInProgress) return;

        isRequestInProgress = true;
        GithubServiceUtils.searchRepos(service, query, lastRequestedPage, NETWORK_PAGE_SIZE, this);
    }

    @Override
    public void onSuccess(List<Repo> repos) {
        cache.insert(repos, new GithubLocalCache.InsertFinishedListener() {
            @Override
            public void onInsertFinished() {
                lastRequestedPage++;
                isRequestInProgress = false;
            }
        });
    }

    @Override
    public void onError(String error) {
        networkErrors.postValue(error);
        isRequestInProgress = false;
    }
}
