package com.laithlab.core.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;
import com.laithlab.core.R;
import com.laithlab.core.musicutil.MusicUtility;


public class MediaPlayerService extends Service {


	private static final String ACTION_REQUEST_SONG_DETAILS = "songDetailsRequest";
	private static final String ACTION_REMOVE_SERVICE = "removeService";
	private MediaPlayer m_objMediaPlayer;

	private Notification notificationCompat;
	private NotificationManager notificationManager;
	private RemoteViews notiLayoutBig;

	public static final int NOTIFICATION_ID = 104;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (m_objMediaPlayer == null) {
			m_objMediaPlayer = MusicUtility.getMediaPlayer();
		}

		handleIntent(intent);
		return super.onStartCommand(intent, flags, startId);
	}

	@SuppressLint("NewApi")
	private void handleIntent(Intent intent) {
		if (intent == null || intent.getAction() == null)
			return;

		String action = intent.getAction();

		if (action.equalsIgnoreCase(Constants.ACTION_PLAY)) {
			if (!m_objMediaPlayer.isPlaying()) {
				setNotificationPlayer(false);
				m_objMediaPlayer.start();
			} else {
				m_objMediaPlayer.pause();
			}
		} else if (action.equalsIgnoreCase(Constants.ACTION_PAUSE)) {
			setNotificationPlayer(true);
			m_objMediaPlayer.pause();
			notificationCompat.bigContentView.setImageViewResource(R.id.noti_play_button,
					R.drawable.ic_play_arrow_white);
		}
	}

	private void setNotificationPlayer(boolean pause) {
		Intent intent = new Intent(this, MediaPlayerService.class);
		if (pause) {
			intent.setAction(Constants.ACTION_PLAY);
			notificationCompat = createBuiderNotificationRemovable().build();
		} else {
			intent.setAction(Constants.ACTION_PAUSE);
			notificationCompat = createBuiderNotification().build();
		}
		notiLayoutBig = new RemoteViews(getPackageName(), R.layout.notification_layout);

		notiLayoutBig.setOnClickPendingIntent(R.id.noti_play_button,
				PendingIntent.getService(this, 0, intent, 0));
		if (Build.VERSION.SDK_INT >= 16) {
			notificationCompat.bigContentView = notiLayoutBig;
			notificationCompat.bigContentView.setImageViewResource(R.id.noti_play_button,
					pause ? R.drawable.ic_pause_white : R.drawable.ic_play_arrow_white);
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
				.setSmallIcon(R.drawable.ic_play_arrow_white)
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
				.setSmallIcon(R.drawable.ic_pause_white)
				.setContentIntent(contentIntent)
				.setDeleteIntent(deletePendingIntent);
	}

	@SuppressLint("NewApi")
	@Override
	public boolean onUnbind(Intent intent) {
		m_objMediaPlayer.release();
		m_objMediaPlayer.reset();
		m_objMediaPlayer = null;
		return super.onUnbind(intent);
	}
}
