package com.laithlab.core.activity;

import android.content.Context;
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
import android.widget.ImageView;
import android.widget.TextView;
import com.laithlab.core.R;
import com.laithlab.core.RestAdapterFactory;
import com.laithlab.core.customview.CircularSeekBar;
import com.laithlab.core.customview.CustomAnimUtil;
import com.laithlab.core.db.Song;
import com.laithlab.core.dto.SongDTO;
import com.laithlab.core.echonest.EchoNestApi;
import com.laithlab.core.echonest.EchoNestSearch;
import com.squareup.picasso.Picasso;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public class PlayerActivity extends AppCompatActivity implements MediaPlayer.OnErrorListener, MediaPlayer.OnInfoListener,
		MediaPlayer.OnPreparedListener {

	private EchoNestApi echoNestApi;
	private Context context;
	//	private AssetFileDescriptor afd;
	private MediaPlayer mediaPlayer;

	private DrawerLayout drawerLayout;
	private TextView txtDuration;
	private CircularSeekBar trackProgress;
	private CircleImageView albumCover;
	private ImageView playButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_player);
		context = this;

		Bundle extras = getIntent().getExtras();
		SongDTO currentSong = extras.getParcelable("song");

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		final ActionBar actionBar = getSupportActionBar();
		if (actionBar != null) {
			getSupportActionBar().setDisplayShowTitleEnabled(false);
			actionBar.setHomeAsUpIndicator(R.drawable.ic_action_menu);
			actionBar.setDisplayHomeAsUpEnabled(true);
		}

		mediaPlayer = new MediaPlayer();
		mediaPlayer.setOnErrorListener(this);
		mediaPlayer.setOnInfoListener(this);
		mediaPlayer.setOnPreparedListener(this);
		mediaPlayer.setScreenOnWhilePlaying(false);

		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawerLayout.setStatusBarBackgroundColor(getResources().getColor(R.color.color_primary));

		TextView artist = (TextView) findViewById(R.id.txt_artist);
		TextView album = (TextView) findViewById(R.id.txt_album);
		TextView track = (TextView) findViewById(R.id.txt_track);
		txtDuration = (TextView) findViewById(R.id.txt_duration);
		playButton = (ImageView) findViewById(R.id.play_button);

		View tiltedView = findViewById(R.id.tilted_view);
		tiltedView.setPivotX(0f);
		tiltedView.setPivotY(0f);
		tiltedView.setRotation(-5f);

		trackProgress = (CircularSeekBar) findViewById(R.id.track_progress);
		trackProgress.setMax(100);

		trackProgress.setOnSeekBarChangeListener(new CircularSeekBar.OnCircularSeekBarChangeListener() {
			@Override
			public void onProgressChanged(CircularSeekBar circularSeekBar, int progress, boolean fromUser) {
				float currentDuration = (((float) circularSeekBar.getProgress() / 100) * mediaPlayer.getDuration());
				updateDuration(milliSecondsToTimer((long) currentDuration), milliSecondsToTimer(mediaPlayer.getDuration()));
			}

			@Override
			public void onStopTrackingTouch(CircularSeekBar seekBar) {
				mediaPlayer.pause();
				int currentMill = (int) (((float) seekBar.getProgress() / 100) * mediaPlayer.getDuration());
				mediaPlayer.seekTo(currentMill);
				playButton.setImageResource(R.drawable.ic_pause_white);
				mediaPlayer.start();
			}

			@Override
			public void onStartTrackingTouch(CircularSeekBar seekBar) {

			}
		});

		albumCover = (CircleImageView) findViewById(R.id.album_cover);
		echoNestApi = RestAdapterFactory.getEchoNestApi();

		MediaMetadataRetriever mmr = new MediaMetadataRetriever();
		try {
			//			afd = getAssets().openFd("Ours Samplus - Blue Bird.mp3");
			mediaPlayer.setDataSource(currentSong.getSongLocation());
			mediaPlayer.prepare();

			//			mmr.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
			mmr.setDataSource(currentSong.getSongLocation());
			fetchAlbumCover(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)
					, mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE));

			artist.setText(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST));
			album.setText(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM));
			track.setText(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE));
		} catch (IOException e) {
			e.printStackTrace();
		}
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
		if (trackUrl != null && !trackUrl.isEmpty()){
			Picasso.with(context).load(trackUrl).placeholder(R.drawable.ic_media_play)
					.into(albumCover);
		}
	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		updateDuration("0:00", milliSecondsToTimer(mp.getDuration()));
		runMedia();

		playButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mediaPlayer.isPlaying()) {
					playButton.setImageResource(R.drawable.ic_play_arrow_white);
					mediaPlayer.pause();
				} else {
					playButton.setImageResource(R.drawable.ic_pause_white);
					CustomAnimUtil.overShootAnimation(albumCover);
					mediaPlayer.start();
				}
			}
		});
	}

	@Override
	public boolean onInfo(MediaPlayer mp, int what, int extra) {
		return false;
	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		return false;
	}

	private class MediaObserver implements Runnable {
		private AtomicBoolean stop = new AtomicBoolean(false);

		public void stop() {
			stop.set(true);
		}

		@Override
		public void run() {
			while (!stop.get()) {

				if (mediaPlayer != null) {
					if (!trackProgress.isPressed() || !mediaPlayer.isPlaying()) {
						final int currentProgress = (int) (((float) mediaPlayer.getCurrentPosition() / mediaPlayer.getDuration()) * 100);

						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								trackProgress.setProgress(currentProgress);
								updateDuration(milliSecondsToTimer(mediaPlayer.getCurrentPosition()), milliSecondsToTimer(mediaPlayer.getDuration()));
							}
						});

						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				} else {
					stop();
				}

			}
		}
	}

	private MediaObserver observer = null;

	public void runMedia() {
		mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				observer.stop();
				trackProgress.setProgress(mp.getCurrentPosition());
			}
		});
		observer = new MediaObserver();
		new Thread(observer).start();
	}

	private void updateDuration(String currentDuration, String totalDuration) {
		txtDuration.setText(context.getResources().getString(R.string.duration_format, currentDuration, totalDuration));
	}

	private void fetchAlbumCover(String artist, String songTitle) {

		if (artist != null && songTitle != null) {
			echoNestApi.getSongImage(artist, songTitle, new Callback<EchoNestSearch>() {
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
		} else {
			echoNestApi.getArtistImage(artist, new Callback<EchoNestSearch>() {
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
	}

	private String milliSecondsToTimer(long milliseconds) {
		String finalTimerString = "";
		String secondsString;

		// Convert total duration into time
		int hours = (int) (milliseconds / (1000 * 60 * 60));
		int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
		int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);
		// Add hours if there
		if (hours > 0) {
			finalTimerString = hours + ":";
		}

		// Prepending 0 to seconds if it is one digit
		if (seconds < 10) {
			secondsString = "0" + seconds;
		} else {
			secondsString = "" + seconds;
		}

		finalTimerString = finalTimerString + minutes + ":" + secondsString;

		// return timer string
		return finalTimerString;
	}
}
