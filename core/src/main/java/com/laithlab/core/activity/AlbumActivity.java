package com.laithlab.core.activity;

import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.laithlab.core.R;
import com.laithlab.core.adapter.SongListAdapter;
import com.laithlab.core.dto.AlbumDTO;

public class AlbumActivity extends AppCompatActivity {

	private DrawerLayout drawerLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_album);

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		final ActionBar actionBar = getSupportActionBar();
		if (actionBar != null) {
			getSupportActionBar().setDisplayShowTitleEnabled(false);
			actionBar.setHomeAsUpIndicator(R.drawable.ic_action_menu);
			actionBar.setDisplayHomeAsUpEnabled(true);
		}
		Bundle extras = getIntent().getExtras();
		final AlbumDTO currentAlbum = extras.getParcelable("album");

		TextView albumTitle = (TextView)findViewById(R.id.txt_album);
		albumTitle.setText(currentAlbum.getAlbumTitle());

		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawerLayout.setStatusBarBackgroundColor(getResources().getColor(R.color.color_primary));
		View tiltedView = findViewById(R.id.tilted_view);
		tiltedView.setPivotX(0f);
		tiltedView.setPivotY(0f);
		tiltedView.setRotation(-5f);

        RecyclerView songList = (RecyclerView) findViewById(R.id.rv_songs_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        songList.setLayoutManager(layoutManager);
        songList.setAdapter(new SongListAdapter(this, currentAlbum.getSongs()));
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_browse, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int i = item.getItemId();
		if (i == android.R.id.home) {
			drawerLayout.openDrawer(GravityCompat.START);
			return true;
		} else if (i == R.id.search_menu_item) {
			startActivity(SearchActivity.getSearchIntent(this));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
