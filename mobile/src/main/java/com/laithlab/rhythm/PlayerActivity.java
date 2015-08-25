package com.laithlab.rhythm;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import com.laithlab.core.RestAdapterFactory;
import com.laithlab.core.echonest.EchoNestApi;
import com.laithlab.core.echonest.EchoNestSearch;
import com.squareup.picasso.Picasso;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import java.io.IOException;

public class PlayerActivity extends AppCompatActivity {

	private EchoNestApi echoNestApi;

	private DrawerLayout drawerLayout;
	private CircleImageView albumCover;
	private Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_player);
		context = this;

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		final ActionBar actionBar = getSupportActionBar();
		if (actionBar != null) {
			getSupportActionBar().setDisplayShowTitleEnabled(false);
			actionBar.setHomeAsUpIndicator(R.drawable.ic_action_menu);
			actionBar.setDisplayHomeAsUpEnabled(true);
		}

		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawerLayout.setStatusBarBackgroundColor(getResources().getColor(R.color.color_primary));

		View tiltedView = findViewById(R.id.tilted_view);
		tiltedView.setPivotX(0f);
		tiltedView.setPivotY(0f);
		tiltedView.setRotation(-5f);

		albumCover = (CircleImageView) findViewById(R.id.album_cover);
		albumCover.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				playMusic();
			}
		});
		echoNestApi = RestAdapterFactory.getEchoNestApi();
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

	private void updateTrackImage(String trackUrl) {
		Picasso.with(context).load(trackUrl)
				.into(albumCover);
	}

	private void playMusic() {
		AssetFileDescriptor afd;
		String title;
		String artist;
		try {
			afd = getAssets().openFd("Ours Samplus - Blue Bird.mp3");
			MediaMetadataRetriever mmr = new MediaMetadataRetriever();
			mmr.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
			artist =
					mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
			title =
					mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);

			if (artist != null && title != null) {
				echoNestApi.getSong(artist, title, new Callback<EchoNestSearch>() {
					@Override
					public void success(EchoNestSearch echoNestSearch, Response response) {
						if (echoNestSearch.getResponse() != null && echoNestSearch.getResponse().trackImage() != null) {
							updateTrackImage(echoNestSearch.getResponse().trackImage());
						}
					}

					@Override
					public void failure(RetrofitError error) {
						Log.e("lnln", error.getMessage());
					}
				});
			}

			MediaPlayer player = new MediaPlayer();
			player.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
			player.prepare();
			player.start();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
