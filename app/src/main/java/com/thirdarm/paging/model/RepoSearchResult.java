package com.thirdarm.paging.model;

import android.arch.lifecycle.LiveData;
import android.arch.paging.PagedList;

import java.util.List;

/**
 * RepoSearchResult from a search, which contains LiveData<List<Repo>> holding query data,
 * and a LiveData<String> of network error state.
 */
public class RepoSearchResult {
    private LiveData<PagedList<Repo>> data;
    private LiveData<String> networkErrors;

    public RepoSearchResult(LiveData<PagedList<Repo>> data, LiveData<String> networkErrors) {
        this.data = data;
        this.networkErrors = networkErrors;
    }

    public LiveData<PagedList<Repo>> getData() {
        return data;
    }

    public void setData(LiveData<PagedList<Repo>> data) {
        this.data = data;
    }

    public LiveData<String> getNetworkErrors() {
        return networkErrors;
    }

    public void setNetworkErrors(LiveData<String> networkErrors) {
        this.networkErrors = networkErrors;
    }
}
