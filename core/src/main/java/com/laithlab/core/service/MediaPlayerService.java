package com.laithlab.core.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.Rating;
import android.media.session.MediaController;
import android.media.session.MediaSession;
import android.media.session.MediaSession.Callback;
import android.media.session.MediaSessionManager;
import android.os.IBinder;
import android.util.Log;
import com.laithlab.core.R;
import com.laithlab.core.musicutil.MusicUtility;


public class MediaPlayerService extends Service {


	private MediaSessionManager m_objMediaSessionManager;
	private MediaSession m_objMediaSession;
	private MediaController m_objMediaController;
	private MediaPlayer m_objMediaPlayer;


	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@SuppressLint("NewApi")
	private void handleIntent(Intent intent) {
		if (intent == null || intent.getAction() == null)
			return;

		String action = intent.getAction();

		if (action.equalsIgnoreCase(Constants.ACTION_PLAY)) {
			m_objMediaController.getTransportControls().play();
		} else if (action.equalsIgnoreCase(Constants.ACTION_PAUSE)) {
			m_objMediaController.getTransportControls().pause();
		} else if (action.equalsIgnoreCase(Constants.ACTION_FAST_FORWARD)) {
			m_objMediaController.getTransportControls().fastForward();
		} else if (action.equalsIgnoreCase(Constants.ACTION_REWIND)) {
			m_objMediaController.getTransportControls().rewind();
		} else if (action.equalsIgnoreCase(Constants.ACTION_PREVIOUS)) {
			m_objMediaController.getTransportControls().skipToPrevious();
		} else if (action.equalsIgnoreCase(Constants.ACTION_NEXT)) {
			m_objMediaController.getTransportControls().skipToNext();
		} else if (action.equalsIgnoreCase(Constants.ACTION_STOP)) {
			m_objMediaController.getTransportControls().stop();
		}
	}

	@SuppressLint("NewApi")
	private void buildNotification(Notification.Action action) {

		Notification.MediaStyle style = new Notification.MediaStyle();
		style.setMediaSession(m_objMediaSession.getSessionToken());

		Intent intent = new Intent(getApplicationContext(), MediaPlayerService.class);
		intent.setAction(Constants.ACTION_STOP);

		PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 1, intent, 0);
		Notification.Builder builder = new Notification.Builder(this)
				.setSmallIcon(R.drawable.ic_play_arrow_white)
				.setContentTitle("Sample Title")
				.setContentText("Sample Artist")
				.setDeleteIntent(pendingIntent)
				.setStyle(style);

		builder.addAction(generateAction(android.R.drawable.ic_media_previous, "Previous", Constants.ACTION_PREVIOUS));
		builder.addAction(generateAction(android.R.drawable.ic_media_rew, "Rewind", Constants.ACTION_REWIND));
		builder.addAction(action);
		builder.addAction(generateAction(android.R.drawable.ic_media_ff, "Fast Foward", Constants.ACTION_FAST_FORWARD));
		builder.addAction(generateAction(android.R.drawable.ic_media_next, "Next", Constants.ACTION_NEXT));

		//final TransportControls controls = m_objMediaSession.getController().getTransportControls();
		NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(1, builder.build());

	}

	@SuppressLint("NewApi")
	private Notification.Action generateAction(int icon, String title, String intentAction) {
		Intent intent = new Intent(getApplicationContext(), MediaPlayerService.class);
		intent.setAction(intentAction);
		PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 1, intent, 0);
		return new Notification.Action.Builder(icon, title, pendingIntent).build();

	}

	private PendingIntent retrievePlaybackAction(int which) {
		Intent action;
		PendingIntent pendingIntent;
		final ComponentName serviceName = new ComponentName(this, MediaPlayerService.class);
		switch (which) {
		case 1:
			// Play and pause
			action = new Intent(Constants.ACTION_PLAY);
			action.setComponent(serviceName);
			pendingIntent = PendingIntent.getService(this, 1, action, 0);
			return pendingIntent;
		case 2:
			// Skip tracks
			action = new Intent(Constants.ACTION_NEXT);
			action.setComponent(serviceName);
			pendingIntent = PendingIntent.getService(this, 2, action, 0);
			return pendingIntent;
		case 3:
			// Previous tracks
			action = new Intent(Constants.ACTION_PREVIOUS);
			action.setComponent(serviceName);
			pendingIntent = PendingIntent.getService(this, 3, action, 0);
			return pendingIntent;
		case 4:
			//fast forward tracks
			action = new Intent(Constants.ACTION_FAST_FORWARD);
			action.setComponent(serviceName);
			pendingIntent = PendingIntent.getService(this, 4, action, 0);
			return pendingIntent;
		case 5:
			//rewind tracks
			action = new Intent(Constants.ACTION_REWIND);
			action.setComponent(serviceName);
			pendingIntent = PendingIntent.getService(this, 5, action, 0);
			return pendingIntent;
		default:
			break;
		}
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (m_objMediaSessionManager == null) {
			initMediaSessions();
		}

		handleIntent(intent);
		return super.onStartCommand(intent, flags, startId);
	}

	@SuppressLint("NewApi")
	private void initMediaSessions() {

		m_objMediaPlayer = MusicUtility.getMediaPlayer();
		m_objMediaSessionManager = (MediaSessionManager) getSystemService(Context.MEDIA_SESSION_SERVICE);
		m_objMediaSession = new MediaSession(getApplicationContext(), "sample session");
		m_objMediaController = new MediaController(getApplicationContext(), m_objMediaSession.getSessionToken());
		m_objMediaSession.setActive(true);
		m_objMediaSession.setFlags(MediaSession.FLAG_HANDLES_MEDIA_BUTTONS |
				MediaSession.FLAG_HANDLES_TRANSPORT_CONTROLS);

		m_objMediaSession.setCallback(new Callback() {
			@Override
			public void onPlay() {
				super.onPlay();
				Log.e(Constants.LOG_TAG, "onPlay");
				m_objMediaPlayer.start();
				buildNotification(generateAction(android.R.drawable.ic_media_pause, "Pause", Constants.ACTION_PAUSE));
			}

			@Override
			public void onPause() {
				super.onPause();
				Log.e(Constants.LOG_TAG, "onPause");
				m_objMediaPlayer.pause();
				buildNotification(generateAction(android.R.drawable.ic_media_play, "Play", Constants.ACTION_PLAY));
			}

			@Override
			public void onSkipToNext() {
				super.onSkipToNext();
				Log.e(Constants.LOG_TAG, "onSkipToNext");
				buildNotification(generateAction(android.R.drawable.ic_media_pause, "Pause", Constants.ACTION_PAUSE));
			}

			@Override
			public void onSkipToPrevious() {
				super.onSkipToPrevious();
				Log.e(Constants.LOG_TAG, "onSkipToPrevious");
				buildNotification(generateAction(android.R.drawable.ic_media_pause, "Pause", Constants.ACTION_PAUSE));
			}

			@Override
			public void onFastForward() {
				super.onFastForward();
				Log.e(Constants.LOG_TAG, "onFastForward");
			}

			@Override
			public void onRewind() {
				super.onRewind();
				Log.e(Constants.LOG_TAG, "onRewind");
			}

			@Override
			public void onStop() {
				super.onStop();
				Log.e(Constants.LOG_TAG, "onStop");
				NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
				notificationManager.cancel(1);
				Intent intent = new Intent(getApplicationContext(), MediaPlayerService.class);
				stopService(intent);
			}

			@Override
			public void onSeekTo(long pos) {
				super.onSeekTo(pos);
			}

			@Override
			public void onSetRating(Rating rating) {
				super.onSetRating(rating);
			}
		});

	}

	@SuppressLint("NewApi")
	@Override
	public boolean onUnbind(Intent intent) {
		m_objMediaSession.release();
		return super.onUnbind(intent);
	}
}
