package com.laithlab.core.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

import com.laithlab.core.R;

public class SearchActivity extends AppCompatActivity {

    public static Intent getSearchIntent(Context context) {
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

        SearchView searchView = (SearchView)findViewById(R.id.search_view);

        setupSearchView(searchView);

    }

    private void setupSearchView(SearchView sv){
        ImageView searchButton = (ImageView) sv.findViewById(R.id.search_mag_icon);
        searchButton.setImageResource(R.drawable.ic_search_24dp_blue);
        TextView searchBox = (TextView) sv.findViewById(R.id.search_src_text);
        searchBox.setHint("Search...");
        searchBox.setHintTextColor(getResources().getColor(R.color.color_primary));
        searchBox.setTextColor(getResources().getColor(R.color.color_primary));

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
//                AnimeGridAdapter animeGridAdapter = (AnimeGridAdapter) grid.getAdapter();
//                if (animeGridAdapter != null) {
//                    animeGridAdapter.getFilter().filter(newText);
//                }
                return false;
            }
        });
    }
}
