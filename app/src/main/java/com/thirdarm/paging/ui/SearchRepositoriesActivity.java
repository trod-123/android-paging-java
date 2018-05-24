package com.thirdarm.paging.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.paging.PagedList;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.thirdarm.paging.Injection;
import com.thirdarm.paging.R;
import com.thirdarm.paging.model.Repo;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchRepositoriesActivity extends AppCompatActivity {
    private static final String LAST_SEARCH_QUERY = "last_search_query";
    private static final String DEFAULT_QUERY = "Android";

    private SearchRepositoriesViewModel viewModel;
    private ReposAdapter adapter = new ReposAdapter();

    @BindView(R.id.list)
    RecyclerView list;

    @BindView(R.id.search_repo)
    EditText search_repo;

    @BindView(R.id.emptyList)
    TextView emptyList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_repositories);
        ButterKnife.bind(this);

        // get the view model
        viewModel = ViewModelProviders.of(this, Injection.provideViewModelFactory((this))).get(SearchRepositoriesViewModel.class);

        // add dividers between RecyclerView's row items
        list.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        list.setLayoutManager(new LinearLayoutManager(this));
//        setupScrollListener(); // Not needed for updating PagedList data

        initAdapter();
        String query = DEFAULT_QUERY;
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(LAST_SEARCH_QUERY)) {
                query = savedInstanceState.getString(LAST_SEARCH_QUERY);
            }
        }
        viewModel.searchRepo(query);
        initSearch(query);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(LAST_SEARCH_QUERY, viewModel.lastQueryValue());
    }

    private void initAdapter() {
        list.setAdapter(adapter);

        // !!! Setup the viewmodel observer
        viewModel.repos.observe(this, new Observer<PagedList<Repo>>() {
            @Override
            public void onChanged(@Nullable PagedList<Repo> repos) {
                Log.d("Activity", String.format("List size: %s", repos != null ? repos.size() : "empty"));
                showEmptyList(repos == null || repos.size() == 0);
                adapter.submitList(repos);
            }
        });
        viewModel.networkErrors.observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                Toast.makeText(SearchRepositoriesActivity.this, String.format("\uD83D\uDE28 Whoops %s", s), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initSearch(String query) {
        search_repo.setText(query);
        search_repo.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    updateRepoListFromInput();
                    return true;
                } else {
                    return false;
                }
            }
        });
        search_repo.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    updateRepoListFromInput();
                    return true;
                } else {
                    return false;
                }
            }
        });
    }

    private void updateRepoListFromInput() {
        String text = search_repo.getText().toString().trim();
        if (!text.isEmpty()) {
            list.scrollToPosition(0);
            viewModel.searchRepo(text);
            adapter.submitList(null);
        }
    }

    private void showEmptyList(Boolean show) {
        if (show) {
            emptyList.setVisibility(View.VISIBLE);
            list.setVisibility(View.GONE);
        } else {
            emptyList.setVisibility(View.GONE);
            list.setVisibility(View.VISIBLE);
        }
    }

//    private void setupScrollListener() {
//        final LinearLayoutManager layoutManager = (LinearLayoutManager) list.getLayoutManager();
//        list.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//                int totalItemCount = layoutManager.getItemCount();
//                int visibleItemCount = layoutManager.getChildCount();
//                int lastVisibleItem = layoutManager.findLastVisibleItemPosition();
//
//                viewModel.listScrolled(visibleItemCount, lastVisibleItem, totalItemCount);
//            }
//        });
//    }
}
