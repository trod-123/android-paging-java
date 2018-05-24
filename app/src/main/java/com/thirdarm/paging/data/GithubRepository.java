package com.thirdarm.paging.data;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.DataSource;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;
import android.util.Log;

import com.thirdarm.paging.api.GithubService;
import com.thirdarm.paging.api.GithubServiceUtils;
import com.thirdarm.paging.db.GithubLocalCache;
import com.thirdarm.paging.model.Repo;
import com.thirdarm.paging.model.RepoSearchResult;

import java.util.List;

/**
 * Repository class that works with local and remote data sources.
 */
public class GithubRepository {
    private GithubService service;
    private GithubLocalCache cache;


    // MOVED TO REPOBOUNDARYCALLBACK.JAVA

//    // keep the last updated page. When the request is successful, increment the page #
//    private int lastRequestedPage = 1;
//
//    // LiveData of network errors
//    private MutableLiveData<String> networkErrors = new MutableLiveData<>();
//
//    // Avoid triggering multiple requests in the same time
//    private boolean isRequestInProgress = false;
//
//    private static final int NETWORK_PAGE_SIZE = 50;


    private static final int DATABASE_PAGE_SIZE = 5; // defines number of items per page in PagedList


    public GithubRepository(GithubService service, GithubLocalCache cache) {
        this.service = service;
        this.cache = cache;
    }

    /**
     * Search repositories whose names match the query
     * @param query
     * @return
     */
    public RepoSearchResult search(String query) {
        Log.d("GithubRepository", String.format("New query: %s", query));
//        lastRequestedPage = 1;
//        requestAndSaveData(query);

        // Get data source from the local cache
        DataSource.Factory<Integer, Repo> dataSourceFactory = cache.reposByName(query); // previously LiveData<List<Repo>>

        // Get the boundary callback
        RepoBoundaryCallback boundaryCallback = new RepoBoundaryCallback(query, service, cache);
        LiveData<String> networkErrors = boundaryCallback.getNetworkErrors();

        // Build a LiveData<PagedList> using LivePagedListBuilder, passing in the data source as well as the paging size
        // for the database
        LiveData<PagedList<Repo>> data =
                new LivePagedListBuilder<>(dataSourceFactory, DATABASE_PAGE_SIZE)
                        .setBoundaryCallback(boundaryCallback)
                        .build();

        return new RepoSearchResult(data, networkErrors);
    }

//    /**
//     * Helper method for requesting and saving more data, for when a user scrolls to end of list
//     * @param query
//     */
//    public void requestMore(String query) {
//        requestAndSaveData(query);
//    }

//    /**
//     * Makes another network call to load and display more data. Last requested page is used to
//     * determine the next page of data to query
//     * @param query
//     */
//    private void requestAndSaveData(String query) {
//        if (isRequestInProgress) return;
//
//        isRequestInProgress = true;
//        GithubServiceUtils.searchRepos(service, query, lastRequestedPage, NETWORK_PAGE_SIZE, this);
//    }

//    @Override
//    public void onSuccess(List<Repo> repos) {
//        cache.insert(repos, new GithubLocalCache.InsertFinishedListener() {
//            @Override
//            public void onInsertFinished() {
//                lastRequestedPage++;
//                isRequestInProgress = false;
//            }
//        });
//    }
//
//    @Override
//    public void onError(String error) {
//        networkErrors.postValue(error);
//        isRequestInProgress = false;
//    }
}
