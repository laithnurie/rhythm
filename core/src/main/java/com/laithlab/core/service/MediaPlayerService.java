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
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;
import com.laithlab.core.R;
import com.laithlab.core.musicutil.MusicUtility;


public class MediaPlayerService extends Service {


	private static final String ACTION_REQUEST_SONG_DETAILS = "songDetailsRequest";
	private static final String ACTION_REMOVE_SERVICE = "removeService";
	private MediaSessionManager m_objMediaSessionManager;
	private MediaSession m_objMediaSession;
	private MediaController m_objMediaController;
	private MediaPlayer m_objMediaPlayer;

	private Notification notificationCompat;
	private NotificationManager notificationManager;
	private RemoteViews notiLayoutBig;

	public static final int NOTIFICATION_ID = 104;


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

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (m_objMediaSessionManager == null) {
			initMediaSessions();
			setNotificationPlayer(false);
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
			}

			@Override
			public void onPause() {
				super.onPause();
				Log.e(Constants.LOG_TAG, "onPause");
				m_objMediaPlayer.pause();
			}

			@Override
			public void onSkipToNext() {
				super.onSkipToNext();
				Log.e(Constants.LOG_TAG, "onSkipToNext");
			}

			@Override
			public void onSkipToPrevious() {
				super.onSkipToPrevious();
				Log.e(Constants.LOG_TAG, "onSkipToPrevious");
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

	private void setNotificationPlayer(boolean stop) {
		if (stop)
			notificationCompat = createBuiderNotificationRemovable().build();
		else
			notificationCompat = createBuiderNotification().build();
		notiLayoutBig = new RemoteViews(getPackageName(), R.layout.notification_layout);
		if (Build.VERSION.SDK_INT >= 16) {
			notificationCompat.bigContentView = notiLayoutBig;
			notificationCompat.bigContentView.setImageViewResource(R.id.noti_play_button,
					R.drawable.ic_play_arrow_white);
		}
		notificationCompat.priority = Notification.PRIORITY_MAX;
		notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		startForeground(NOTIFICATION_ID, notificationCompat);
		notificationManager.notify(NOTIFICATION_ID, notificationCompat);
	}

	private NotificationCompat.Builder createBuiderNotification() {
		Intent notificationIntent = new Intent();
		notificationIntent.setAction(MediaPlayerService.ACTION_REQUEST_SONG_DETAILS);
		PendingIntent contentIntent = PendingIntent.getBroadcast(MediaPlayerService.this, 0, notificationIntent, 0);
		Intent deleteIntent = new Intent();
		deleteIntent.setAction(MediaPlayerService.ACTION_REMOVE_SERVICE);
		PendingIntent deletePendingIntent = PendingIntent.getBroadcast(MediaPlayerService.this, 0, deleteIntent, 0);
		return new NotificationCompat.Builder(this)
				.setOngoing(true)
				.setSmallIcon(R.drawable.ic_action_playback_shuffle)
				.setContentIntent(contentIntent)
				.setDeleteIntent(deletePendingIntent);
	}

	private NotificationCompat.Builder createBuiderNotificationRemovable() {
		Intent notificationIntent = new Intent();
		notificationIntent.setAction(MediaPlayerService.ACTION_REQUEST_SONG_DETAILS);
		PendingIntent contentIntent = PendingIntent.getActivity(MediaPlayerService.this, 0, notificationIntent, 0);
		Intent deleteIntent = new Intent();
		deleteIntent.setAction(MediaPlayerService.ACTION_REMOVE_SERVICE);
		PendingIntent deletePendingIntent = PendingIntent.getBroadcast(MediaPlayerService.this, 0, deleteIntent, 0);
		return new NotificationCompat.Builder(this)
				.setOngoing(false)
				.setSmallIcon(R.drawable.ic_action_playback_repeat)
				.setContentIntent(contentIntent)
				.setDeleteIntent(deletePendingIntent);
	}

	@SuppressLint("NewApi")
	@Override
	public boolean onUnbind(Intent intent) {
		m_objMediaSession.release();
		return super.onUnbind(intent);
	}
}
