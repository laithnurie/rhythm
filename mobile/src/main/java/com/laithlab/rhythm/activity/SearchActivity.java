package com.laithlab.rhythm.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.laithlab.rhythm.R;
import com.laithlab.rhythm.adapter.SearchAdapter;
import com.laithlab.rhythm.dto.SearchResult;
import com.laithlab.rhythm.utils.MusicDataUtility;

import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private RecyclerView searchList;

    public static Intent getIntent(Context context) {
        return new Intent(context, SearchActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(false);
        }

        SearchView searchView = (SearchView) findViewById(R.id.search_view);
        setupSearchView(searchView);

        searchList = (RecyclerView) findViewById(R.id.search_list);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        searchList.setLayoutManager(layoutManager);
        List<SearchResult> results = MusicDataUtility.getAllSearchResults(getApplicationContext());
        if(results != null && results.size() > 0){
            searchList.setAdapter(new SearchAdapter(results));
            findViewById(R.id.no_search_results).setVisibility(View.GONE);
        } else {
            findViewById(R.id.no_search_results).setVisibility(View.VISIBLE);
        }

    }

    private void setupSearchView(SearchView sv) {
        ImageView searchButton = (ImageView) sv.findViewById(R.id.search_mag_icon);
        searchButton.setImageResource(R.drawable.ic_search_white_24dp);
        TextView searchBox = (TextView) sv.findViewById(R.id.search_src_text);
        searchBox.setHint("Search...");
        searchBox.setHintTextColor(getResources().getColor(R.color.white));
        searchBox.setTextColor(getResources().getColor(R.color.white));

        ImageView searchClose = (ImageView) sv.findViewById(R.id.search_close_btn);
        searchClose.setImageResource(R.drawable.ic_clear_white_24dp);

        sv.setIconifiedByDefault(false);

        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                SearchAdapter searchAdapter = (SearchAdapter) searchList.getAdapter();
                if (searchAdapter != null) {
                    searchAdapter.getFilter().filter(newText);
                }
                return false;
            }
        });
    }
}
