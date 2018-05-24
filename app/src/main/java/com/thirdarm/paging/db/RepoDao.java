package com.thirdarm.paging.db;

import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.thirdarm.paging.model.Repo;

import java.util.List;

/**
 * Room data access object for accessing the [Repo] table.
 */
@Dao
interface RepoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<Repo>posts);

    // Do a similar query as the search API:
    // Look for repos that contain the query string in the name or in the description
    // and order those results descending, by the number of stars and then by name
    @Query("SELECT * FROM repos WHERE (name LIKE :queryString) OR (description LIKE " +
            ":queryString) ORDER BY stars DESC, name ASC")
    // Previously returned LiveData<List<Repo>>. For paging, the query method needs to be loaded onto
    // a DataSource is the source of data read by the PagedList. PagedList dynamically loads data from
    // this DataSource. The DataSource is automatically invalided and recreated when the data set
    // is updated. Here, return a DataSource.Factory from query method to handle DataSource implementation
    DataSource.Factory<Integer, Repo> reposByName(String queryString);
}
