package com.laithlab.core.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import com.laithlab.core.R;
import com.laithlab.core.converter.DTOConverter;
import com.laithlab.core.db.Song;
import com.laithlab.core.dto.AlbumDTO;
import com.laithlab.core.dto.SongDTO;
import com.laithlab.core.fragment.SongFragment;
import com.laithlab.core.fragment.SongFragmentListener;
import io.realm.Realm;
import io.realm.RealmResults;

import java.util.List;

public class SwipePlayerActivity extends AppCompatActivity implements SongFragmentListener {

	private DrawerLayout drawerLayout;
	private Toolbar toolbar;
	private View tiltedView;
	private TextView artist;
	private ViewPager viewPager;

	private TextView album;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_swipe_player);

		Bundle extras = getIntent().getExtras();
		AlbumDTO currentAlbum = extras.getParcelable("album");
		int songPosition = extras.getInt("songPosition");

		toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		final ActionBar actionBar = getSupportActionBar();
		if (actionBar != null) {
			getSupportActionBar().setDisplayShowTitleEnabled(false);
			actionBar.setHomeAsUpIndicator(R.drawable.ic_action_menu);
			actionBar.setDisplayHomeAsUpEnabled(true);
		}

		Realm realm = Realm.getInstance(this);
		final RealmResults<Song> songs = realm.where(Song.class)
				.contains("albumId", currentAlbum.getId())
				.findAll();

		tiltedView = findViewById(R.id.tilted_view);
		tiltedView.setPivotX(0f);
		tiltedView.setPivotY(0f);
		tiltedView.setRotation(-5f);

		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawerLayout.setStatusBarBackgroundColor(getResources().getColor(R.color.color_primary));
		viewPager = (ViewPager) findViewById(R.id.pager);
		viewPager.setOffscreenPageLimit(3);
		final List<SongDTO> songsList = DTOConverter.getSongList(songs.subList(0, songs.size()));
		viewPager.setAdapter(new SongFragmentPager(this.getSupportFragmentManager(),
				songsList));
		if (songPosition > 0) {
			viewPager.setCurrentItem(songPosition, true);
		}

		artist = (TextView) findViewById(R.id.txt_artist);
		album = (TextView) findViewById(R.id.txt_album);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_player, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			drawerLayout.openDrawer(GravityCompat.START);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void changePlayerStyle(int vibrantColor, int songPosition) {
		if (songPosition == viewPager.getCurrentItem()) {
			if (vibrantColor == 0) {
				vibrantColor = getResources().getColor(R.color.color_primary);
			}
			toolbar.setBackgroundColor(vibrantColor);
			tiltedView.setBackgroundColor(vibrantColor);
		}
	}

	@Override
	public void setToolBarText(String artistTitle, String albumTitle) {
		artist.setText(artistTitle);
		album.setText(albumTitle);
	}

	public class SongFragmentPager extends FragmentStatePagerAdapter {

		private List<SongDTO> songDTOs;

		public SongFragmentPager(FragmentManager fm, List<SongDTO> songDTOs) {
			super(fm);
			this.songDTOs = songDTOs;
		}


		@Override
		public Fragment getItem(int position) {
			return SongFragment.newInstance(songDTOs.get(position), position);
		}

		@Override
		public int getCount() {
			return songDTOs.size();
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return "";
		}
	}


}
