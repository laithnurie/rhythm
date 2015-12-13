package com.laithlab.rhythm;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.wearable.view.WatchViewStub;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataItemBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import com.laithlab.core.service.Constants;

import java.util.List;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageActivity extends Activity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final long CONNECTION_TIME_OUT_MS = 100;

    private GoogleApiClient client;

    private final WearMessageReceiver messageReceiver = new WearMessageReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        initApi();

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                setupWidgets();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        client.connect();
        IntentFilter messageFilter = new IntentFilter(Intent.ACTION_SEND);
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, messageFilter);
    }

    protected void onStop() {
        if (client != null && client.isConnected()) {
            client.disconnect();
        }

        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReceiver);

        super.onStop();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Wearable.DataApi.getDataItems(client).setResultCallback(resultCallback);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    class WearMessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle dataBundle = intent.getBundleExtra("wear_data");
            TextView songTitle = (TextView) findViewById(R.id.wear_song_played_title);
            songTitle.setText(dataBundle.getString("song_title"));
            byte[] songCover = dataBundle.getByteArray("song_cover");

            CircleImageView circleImageView = (CircleImageView) findViewById(R.id.wear_song_cover);
            if (songCover != null) {
                Bitmap bmp = BitmapFactory.decodeByteArray(songCover, 0, songCover.length);
                circleImageView.setImageBitmap(bmp);
            } else {
                circleImageView.setImageResource(android.R.color.transparent);
            }

        }
    }

    /**
     * Initializes the GoogleApiClient and gets the Node ID of the connected device.
     */
    private void initApi() {
        client = getGoogleApiClient(this);
    }

    /**
     * Sets up the button for handling click events.
     */
    private void setupWidgets() {
        findViewById(R.id.wear_pause).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendCommand(Constants.ACTION_PAUSE);
            }
        });
        findViewById(R.id.wear_play).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendCommand(Constants.ACTION_PLAY);
            }
        });
        findViewById(R.id.wear_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendCommand(Constants.ACTION_NEXT);
            }
        });

        findViewById(R.id.wear_previous).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendCommand(Constants.ACTION_PREVIOUS);
            }
        });
    }

    private GoogleApiClient getGoogleApiClient(Context context) {
        return new GoogleApiClient.Builder(context)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    private void sendCommand(final String command) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                client.blockingConnect(CONNECTION_TIME_OUT_MS, TimeUnit.MILLISECONDS);
                NodeApi.GetConnectedNodesResult result =
                        Wearable.NodeApi.getConnectedNodes(client).await();
                List<Node> nodes = result.getNodes();
                for (Node node : nodes) {
                    Wearable.MessageApi.sendMessage(client, node.getId(), command, null);
                }
                client.disconnect();
            }
        }).start();
    }

    private final ResultCallback<DataItemBuffer> resultCallback = new ResultCallback<DataItemBuffer>() {
        @Override
        public void onResult(DataItemBuffer dataItems) {
            if (dataItems.getCount() != 0) {
                DataMap dataMap = DataMapItem.fromDataItem(dataItems.get(0)).getDataMap();

            }

            dataItems.release();
        }
    };
}
