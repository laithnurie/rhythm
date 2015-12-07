package com.laithlab.core.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.laithlab.core.R;
import com.laithlab.core.adapter.SearchAdapter;
import com.laithlab.core.utils.MusicDataUtility;

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

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        searchList.setLayoutManager(layoutManager);
        searchList.setAdapter(new SearchAdapter(MusicDataUtility.getAllSearchResults(this)));

    }

    private void setupSearchView(SearchView sv) {
        ImageView searchButton = (ImageView) sv.findViewById(R.id.search_mag_icon);
        searchButton.setImageResource(R.drawable.ic_search_24dp_white);
        TextView searchBox = (TextView) sv.findViewById(R.id.search_src_text);
        searchBox.setHint("Search...");
        searchBox.setHintTextColor(getResources().getColor(R.color.white));
        searchBox.setTextColor(getResources().getColor(R.color.white));

        ImageView searchClose = (ImageView) sv.findViewById(R.id.search_close_btn);
        searchClose.setImageResource(R.drawable.ic_clear_24dp);

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
