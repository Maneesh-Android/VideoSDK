package com.mani.videoplayersdk;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.View;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.audio.AudioRendererEventListener;
import com.google.android.exoplayer2.decoder.DecoderCounters;
import com.google.android.exoplayer2.drm.DrmSessionManager;
import com.google.android.exoplayer2.ext.ima.ImaAdsLoader;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.ads.AdsMediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.video.VideoRendererEventListener;


public class MainActivity extends Activity {

    private SimpleExoPlayer player;
    private PlayerView playerView;
    ImaAdsLoader adsLoader;
    private int status;

    private DataSource.Factory dataFactory;

    //SharedPreferences pref;
    int videoType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //ComponentListener componentListener = new ComponentListener();
        playerView = findViewById(R.id.video_view);
        adsLoader = new ImaAdsLoader(this, Uri.parse(getString(R.string.ad_tag_url)));
        initializePlayer();
        status = getIntent().getIntExtra("videoType", 100);

        /*pref = getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        status = pref.getInt("status",0);
        SharedPreferences.Editor editor = pref.edit();
        if (status == 0) { // Manifest
            videoType = 0;
            editor.putInt("status",1);
            editor.apply();
        }else if (status == 1) { // M3U8
            videoType = 0;
            editor.putInt("status",0);
            editor.commit();
        }*/ /*else if (status == 2) {
            videoType = 3;
            editor.putInt("status",0);
            editor.commit();
        }*/
    }

    /*private void initializePlayer() {

        trackSelectionFactory = new AdaptiveTrackSelection.Factory(BANDWIDTH_METER);
        trackSelector = new DefaultTrackSelector(trackSelectionFactory);

        player = ExoPlayerFactory.newSimpleInstance(this, trackSelector);
        playerView.setPlayer(player);

    }*/

    private void initializePlayer() {
        // Create a SimpleExoPlayer and set is as the player for content and ads.
        player = new SimpleExoPlayer.Builder(this).build();
        adsLoader.setPlayer(player);
        playerView.setPlayer(player);

        DataSource.Factory dataSourceFactory =
                new DefaultDataSourceFactory(this, Util.getUserAgent(this, getString(R.string.app_name)));

        // Create the MediaSource for the content you wish to play. To load MP4

//        ProgressiveMediaSource.Factory mediaSourceFactory =
//                new ProgressiveMediaSource.Factory(dataSourceFactory);

//        MediaSource mediaSource =
//                mediaSourceFactory.createMediaSource(Uri.parse(getString(R.string.content_url)));

        if (status == 0) {
            MediaSource mediaSource = new DashMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(Uri.parse("https://bitmovin-a.akamaihd.net/content/MI201109210084_1/mpds/f08e80da-bf1d-4e3d-8899-f0f6155f6efa.mpd"));
            AdsMediaSource adsMediaSource =
                    new AdsMediaSource(mediaSource, dataSourceFactory, adsLoader, playerView);
            player.prepare(adsMediaSource);
        }else if (status == 1){
            Log.e("VideoUrl", "113    "+ getIntent().getStringExtra("videoUrl"));
            HlsMediaSource hlsMediaSource =
                    //new HlsMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse("https://bitdash-a.akamaihd.net/content/sintel/hls/playlist.m3u8"));
                    //new HlsMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse("https://cph-p2p-msl.akamaized.net/hls/live/2000341/test/master.m3u8")); // Live strmming
                    //new HlsMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse("http://server.siddhran.in:8200/api/vid?id=3&q=O"));

                    new HlsMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse(getIntent().getStringExtra("videoUrl")));
            AdsMediaSource adsMediaSource =
                    new AdsMediaSource(hlsMediaSource, dataSourceFactory, adsLoader, playerView);
            player.prepare(adsMediaSource);
        }else if (status == 2) {
            ProgressiveMediaSource.Factory mediaSourceFactory =
                new ProgressiveMediaSource.Factory(dataSourceFactory);

        MediaSource mediaSource =
                mediaSourceFactory.createMediaSource(Uri.parse(getString(R.string.content_url)));
        player.prepare(mediaSource);
        //https://cph-p2p-msl.akamaized.net/hls/live/2000341/test/master.m3u8 -- > Live strrming
        }else if (status == 3) {
            ProgressiveMediaSource.Factory mediaSourceFactory =
                    new ProgressiveMediaSource.Factory(dataSourceFactory);

            MediaSource mediaSource =
                    mediaSourceFactory.createMediaSource(Uri.parse(getString(R.string.content_url)));
            player.prepare(mediaSource);
        }
//        PlaybackParameters playbackParameters = new PlaybackParameters(1f);
//        player.setPlaybackParameters(playbackParameters);
        player.setPlayWhenReady(true);
    }

    private void releasePlayer() {

        if (player != null) {
            //adsLoader.setPlayer(null);
            playerView.setPlayer(null);
            player.release();
            player = null;
        }
    }

    @SuppressLint("InlinedApi")
    private void hideSystemUi() {
        playerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    private static class ComponentListener extends Player.DefaultEventListener implements
            VideoRendererEventListener, AudioRendererEventListener {

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            String stateString;
            switch (playbackState) {
                case Player.STATE_IDLE:
                    stateString = "ExoPlayer.STATE_IDLE      -";
                    Log.e("ThestatusofPlayer", stateString);
                    break;
                case Player.STATE_BUFFERING:
                    stateString = "ExoPlayer.STATE_BUFFERING -";
                    Log.e("ThestatusofPlayer", stateString);
                    break;
                case Player.STATE_READY:
                    stateString = "ExoPlayer.STATE_READY     -";
                    Log.e("ThestatusofPlayer", stateString);
                    break;
                case Player.STATE_ENDED:
                    stateString = "ExoPlayer.STATE_ENDED     -";
                    Log.e("ThestatusofPlayer", stateString);
                    break;
                default:
                    stateString = "UNKNOWN_STATE             -";
                    Log.e("ThestatusofPlayer", stateString);
                    break;
            }
            String TAG = "MainActivity";
            Log.e(TAG, "changed state to " + stateString + " playWhenReady: " + playWhenReady);
        }

        // Implementing VideoRendererEventListener.

        @Override
        public void onVideoEnabled(DecoderCounters counters) {
            // Do nothing.
        }

        @Override
        public void onVideoDecoderInitialized(String decoderName, long initializedTimestampMs, long initializationDurationMs) {
            // Do nothing.
        }

        @Override
        public void onVideoInputFormatChanged(Format format) {
            // Do nothing.
        }

        @Override
        public void onDroppedFrames(int count, long elapsedMs) {
            // Do nothing.
        }

        @Override
        public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
            // Do nothing.
        }

        @Override
        public void onRenderedFirstFrame(Surface surface) {
            // Do nothing.
        }

        @Override
        public void onVideoDisabled(DecoderCounters counters) {
            // Do nothing.
        }

        // Implementing AudioRendererEventListener.

        @Override
        public void onAudioEnabled(DecoderCounters counters) {
            // Do nothing.
        }

        @Override
        public void onAudioSessionId(int audioSessionId) {
            // Do nothing.
        }

        @Override
        public void onAudioDecoderInitialized(String decoderName, long initializedTimestampMs, long initializationDurationMs) {
            // Do nothing.
        }

        @Override
        public void onAudioInputFormatChanged(Format format) {
            // Do nothing.
        }

        @Override
        public void onAudioSinkUnderrun(int bufferSize, long bufferSizeMs, long elapsedSinceLastFeedMs) {
            // Do nothing.
        }

        @Override
        public void onAudioDisabled(DecoderCounters counters) {
            // Do nothing.
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        /*if (Util.SDK_INT > 23) {
            initializePlayer();
        }*/
        if (Util.SDK_INT > 23) {
            initializePlayer();
            if (playerView != null) {
                playerView.onResume();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        /*hideSystemUi();
        if ((Util.SDK_INT <= 23 || player == null)) {
            initializePlayer();
        }*/

        if (Util.SDK_INT <= 23 || player == null) {
            initializePlayer();
            if (playerView != null) {
                playerView.onResume();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        /*if (Util.SDK_INT <= 23) {
            releasePlayer();
        }*/
        if (Util.SDK_INT <= 23) {
            if (playerView != null) {
                playerView.onPause();
            }
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        /*if (Util.SDK_INT > 23) {
            releasePlayer();
        }*/

        if (Util.SDK_INT > 23) {
            if (playerView != null) {
                playerView.onPause();
            }
            releasePlayer();
        }

        //contentPosition = player.getContentPosition();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //adsLoader.release();
        //imaAdsLoader.release();
    }

    private MediaSource createLeafMediaSource(
            Uri uri, String extension, DrmSessionManager<?> drmSessionManager) {
        @C.ContentType int type = Util.inferContentType(uri, extension);
        switch (type) {
            case C.TYPE_DASH:
                return new DashMediaSource.Factory(dataFactory)
                        .setDrmSessionManager(drmSessionManager)
                        .createMediaSource(uri);
            case C.TYPE_SS:
                return new SsMediaSource.Factory(dataFactory)
                        .setDrmSessionManager(drmSessionManager)
                        .createMediaSource(uri);
            case C.TYPE_HLS:
                return new HlsMediaSource.Factory(dataFactory)
                        .setDrmSessionManager(drmSessionManager)
                        .createMediaSource(uri);
            case C.TYPE_OTHER:
                return new ProgressiveMediaSource.Factory(dataFactory)
                        .setDrmSessionManager(drmSessionManager)
                        .createMediaSource(uri);
            default:
                throw new IllegalStateException("Unsupported type: " + type);
        }
    }
}
