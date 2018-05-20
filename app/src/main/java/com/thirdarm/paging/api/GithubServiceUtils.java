package com.thirdarm.paging.api;

import android.util.Log;

import com.thirdarm.paging.model.Repo;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GithubServiceUtils {

    public static GithubService create() {
        final String BASE_URL = "https://api.github.com/";

        HttpLoggingInterceptor logger = new HttpLoggingInterceptor();
        logger.setLevel(HttpLoggingInterceptor.Level.BASIC);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logger)
                .build();

        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(GithubService.class);
    }

    /**
     * Search repos based on a query.
     * Trigger a request to the Github searchRepo API with the following params:
     *
     * @param query        searchRepo keyword
     * @param page         request page index
     * @param itemsPerPage number of repositories to be returned by the Github API per page
     *                     <p>
     *                     The result of the request is handled by the implementation of the functions passed as params
     */
    public static void searchRepos(GithubService service, String query, int page, int itemsPerPage, final StatusCallback callbacks) {
        final String TAG = "GithubService";
        final String IN_QUALIFER = "in:name,description";

        Log.d(TAG, String.format("Query: %s, Page: %s, Items per page: %s", query, page, itemsPerPage));
        String apiQuery = query + IN_QUALIFER;

        service.searchRepos(apiQuery, page, itemsPerPage).enqueue(
                new Callback<RepoSearchResponse>() {
                    @Override
                    public void onResponse(Call<RepoSearchResponse> call, Response<RepoSearchResponse> response) {
                        Log.d(TAG, String.format("Got a response: %s", response));
                        if (response.isSuccessful()) {
                            List<Repo> repos;
                            if (response.body() != null) {
                                repos = response.body().items;
                            } else {
                                repos = Collections.emptyList();
                            }
                            callbacks.onSuccess(repos);
                        } else {
                            try {
                                String body = response.errorBody().string();
                                callbacks.onError(body != null ? body : "Unknown error");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<RepoSearchResponse> call, Throwable t) {
                        Log.d(TAG, "Failed to get data");
                        callbacks.onError(t.getMessage() != null ? t.getMessage() : "Unknown error");
                    }
                }
        );
    }

    public interface StatusCallback {
        void onSuccess(List<Repo> repos);
        void onError(String error);
    }
}
