package com.laithlab.rhythm.service;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;


public class WearMessageService extends WearableListenerService {

    private static final String WEARABLE_DATA_PATH = "/wearable_data";

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        playerNotification(messageEvent.getPath());
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        DataMap dataMap;
        for (DataEvent event : dataEvents) {
            // Check the data type
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                // Check the data path
                String path = event.getDataItem().getUri().getPath();
                if (path.equals(WEARABLE_DATA_PATH)) {
                    dataMap = DataMapItem.fromDataItem(event.getDataItem()).getDataMap();
                    Log.v("myTag", "DataMap received on watch: " + dataMap);
                    wearBroadcast(dataMap);
                }
            }
        }
    }

    private void playerNotification(String action) {
        Intent intent = new Intent(getApplicationContext(), MediaPlayerService.class);
        intent.setAction(action);
        startService(intent);
    }

    private void wearBroadcast(DataMap dataMap) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra("wear_data", dataMap.toBundle());
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
