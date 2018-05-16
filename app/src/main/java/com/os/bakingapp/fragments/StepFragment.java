package com.os.bakingapp.fragments;


import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.looptech.bakingapp.R;
import com.os.bakingapp.models.Step;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class StepFragment extends Fragment {
    private static final String STEP_PARAM = "stepParam";
    private static final String PLAYBACK_POSITION_KEY = "playbackPosition";
    private static final String CURRENT_WINDOW_KEY = "currentFrame";
    private static final String PLAY_WHEN_READY_KEY = "playWhenReady";

    @Nullable
    @BindView(R.id.stepDescription)
    TextView stepDescription;

    @BindView(R.id.videoPlayerView)
    PlayerView videoPlayerView;

    @Nullable
    @BindView(R.id.videoPlaceHolderImage)
    ImageView videoPlaceHolder;

    private Step step;
    private ExoPlayer exoPlayer;
    private int currentWindow = 0;
    private long playbackPosition = 0;
    private boolean playWhenReady = false;

    public StepFragment() { }

    public static StepFragment newInstance(Step step) {
        StepFragment fragment = new StepFragment();
        Bundle args = new Bundle();
        args.putParcelable(STEP_PARAM, step);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            step = getArguments().getParcelable(STEP_PARAM);
        }

        if (step == null) {
            throw new IllegalArgumentException("Step can't be null!");
        }

        if (savedInstanceState != null) {
            playbackPosition = savedInstanceState.getLong(PLAYBACK_POSITION_KEY);
            currentWindow = savedInstanceState.getInt(CURRENT_WINDOW_KEY);
            playWhenReady = savedInstanceState.getBoolean(PLAY_WHEN_READY_KEY);
        }

        Timber.d("Step: %s", step);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_step, container, false);
        ButterKnife.bind(this, view);

        if (stepDescription != null) { // null if in landscape mode
            stepDescription.setText(step.description);
        }

        int orientation = getResources().getConfiguration().orientation;
        Timber.d("Configuration: %d", orientation);
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            hideSystemUi();
        }


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (Util.SDK_INT > 23) {
            initializePlayer();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if ((Util.SDK_INT <= 23 || exoPlayer == null)) {
            initializePlayer();
        }
    }

    @Override
    public void onPause() {
        savePlayerState();

        super.onPause();
        if (Util.SDK_INT <= 23) {
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            releasePlayer();
        }
    }

    private void initializePlayer() {
        Timber.d("Initializing Player window=%d, playbackPosition=%d, playWhenReady=%s", currentWindow, playbackPosition, playWhenReady);
        exoPlayer = ExoPlayerFactory.newSimpleInstance(
                new DefaultRenderersFactory(getActivity()),
                new DefaultTrackSelector(),
                new DefaultLoadControl()
        );

        videoPlayerView.setPlayer(exoPlayer);

        exoPlayer.seekTo(currentWindow, playbackPosition);
        exoPlayer.setPlayWhenReady(playWhenReady);

        Uri uri = Uri.parse(step.videoUrl);

        if (videoPlaceHolder != null && TextUtils.isEmpty(step.videoUrl)) {
            setupVideoPlaceHolder();
        }

        MediaSource mediaSource = buildMediaSource(uri);
        exoPlayer.prepare(mediaSource, false, false);
    }

    private void setupVideoPlaceHolder() {
        videoPlaceHolder.setVisibility(View.VISIBLE);
        if (!TextUtils.isEmpty(step.thumbnailUrl)) {
            Timber.d("step thumbnail is not null or empty, using it!");

            Picasso.get().load(step.thumbnailUrl).into(videoPlaceHolder);
        }
    }

    private MediaSource buildMediaSource(Uri uri) {
        String userAgent = Util.getUserAgent(getActivity(), "BakingTime");
        return new ExtractorMediaSource.Factory(new DefaultDataSourceFactory(getActivity(), userAgent))
                .createMediaSource(uri);
    }

    private void hideSystemUi() {
        videoPlayerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (!isVisibleToUser) {
            stopPlayer();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putLong(PLAYBACK_POSITION_KEY, playbackPosition);
        outState.putInt(CURRENT_WINDOW_KEY, currentWindow);
        outState.putBoolean(PLAY_WHEN_READY_KEY, playWhenReady);
    }

    public void stopPlayer() {
        Timber.d("Stopping player of step (%d)", step == null ? -1 : step.id);
        if (exoPlayer != null) {
            exoPlayer.setPlayWhenReady(false);
        }
    }

    public void startPlayer() {
        Timber.d("Starting player of step (%d)", step == null ? -1 : step.id);
        if (exoPlayer != null) {
            exoPlayer.setPlayWhenReady(true);
        }
    }

    private void releasePlayer() {
        if (exoPlayer != null) {
            exoPlayer.release();
            exoPlayer = null;
        }
    }

    private void savePlayerState() {
        if (exoPlayer != null) {
            playbackPosition = exoPlayer.getCurrentPosition();
            currentWindow = exoPlayer.getCurrentWindowIndex();
            playWhenReady = exoPlayer.getPlayWhenReady();
            Timber.d("Releasing Player window=%d, playbackPosition=%d, playWhenReady=%s", currentWindow, playbackPosition, playWhenReady);
        }
    }
}
