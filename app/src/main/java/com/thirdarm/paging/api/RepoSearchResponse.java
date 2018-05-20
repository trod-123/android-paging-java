package com.thirdarm.paging.api;

import com.thirdarm.paging.model.Repo;

import java.util.Collections;
import java.util.List;

public class RepoSearchResponse {
    int total_count = 0;
    List<Repo> items = Collections.emptyList();
    int nextPage = -1;
}
