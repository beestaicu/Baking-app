package com.example.anthonybs.baking.ui;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.anthonybs.baking.R;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import data.Recipe;

public class StepDetailActivity extends AppCompatActivity implements View.OnClickListener, ExoPlayer.EventListener {

    private static final String TAG = StepDetailActivity.class.getName();
    private final String RESUME_WINDOW = "resumeWindow";
    private final String RESUME_POSITION = "resumePosition";
    private final String PLAYER_FULLSCREEN = "playerFullscreen";

    private boolean mPlayerFullscreen = false;
    private int mResumeWindow;
    private long mResumePosition;
    private ArrayList<Recipe.Step> mSteps = new ArrayList<Recipe.Step>();
    private int selectedPosition;
    private SimpleExoPlayer mExoPlayer;
    private MediaSession mMediaSession;
    private PlaybackState.Builder mStateBuilder;
    private ImageView mFullScreenIcon;
    private Dialog mFullScreenDialog;
    private Button mFullScreenButton;

    @BindView(R.id.playerView) SimpleExoPlayerView mPlayerView;
    @BindView(R.id.tv_step_instruction) TextView mStepInstruction;
    @BindView(R.id.prev_button) Button mPrevButton;
    @BindView(R.id.next_button) Button mNextButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_detail);

        ButterKnife.bind(this);

        Intent parentIntent = getIntent();
        if (parentIntent != null) {
            if (parentIntent.hasExtra(Intent.EXTRA_TEXT) || parentIntent.hasExtra("Steps")) {
                mSteps = parentIntent.getParcelableArrayListExtra("Steps");
                selectedPosition = parentIntent.getIntExtra(Intent.EXTRA_TEXT, -1);
                if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
                    setStepDetailView();
                else {
                    LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mPlayerView.getLayoutParams();
                    layoutParams.width = layoutParams.MATCH_PARENT;
                    layoutParams.height = layoutParams.MATCH_PARENT;
                }
            }
        }
    }

    private void setStepDetailView () {
        mStepInstruction.setText(mSteps.get(selectedPosition).getDescription());
        initializeMediaSession();
        if (mSteps.get(selectedPosition).getVideoUrl() != null)
            initializePlayer(Uri.parse(mSteps.get(selectedPosition).getVideoUrl()));
        else
            initializePlayer(Uri.parse(mSteps.get(selectedPosition).getThumbnailUrl()));

        if (selectedPosition == 0) {
            mPrevButton.setClickable(false);
        } else {
            mPrevButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Class destinationClass = StepDetailActivity.class;
                    Intent intentToStartPreviousStepActivity = new Intent(StepDetailActivity.this, destinationClass);
                    intentToStartPreviousStepActivity.putExtra(Intent.EXTRA_TEXT, selectedPosition - 1);
                    intentToStartPreviousStepActivity.putParcelableArrayListExtra("Steps", mSteps);
                    finish();
                    startActivity(intentToStartPreviousStepActivity);
                }
            });
        }

        if (selectedPosition == mSteps.size() - 1) {
            mNextButton.setClickable(false);

        } else {
            mNextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Class destinationClass = StepDetailActivity.class;
                    Intent intentToStartNextStepActivity = new Intent(StepDetailActivity.this, destinationClass);
                    intentToStartNextStepActivity.putExtra(Intent.EXTRA_TEXT, selectedPosition + 1);
                    intentToStartNextStepActivity.putParcelableArrayListExtra("Steps", mSteps);
                    finish();
                    startActivity(intentToStartNextStepActivity);
                }
            });
        }
    }

    private void initializePlayer(Uri stepVideoUri) {
        if (mExoPlayer == null) {
            //Create an instance of ExoPlayer
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(new DefaultRenderersFactory(this), trackSelector, loadControl);
            mPlayerView.setPlayer(mExoPlayer);

            //Set ExoPlayer.EventListener to this activity
            mExoPlayer.addListener(this);

            //Prepare media source
            String userAgent = Util.getUserAgent(this, "Baking");
            MediaSource mediaSource = new ExtractorMediaSource(stepVideoUri,
                    new DefaultDataSourceFactory(this, userAgent), new DefaultExtractorsFactory(),
                    null, null);

            boolean haveResumePosition = mResumeWindow != C.INDEX_UNSET;

            if (haveResumePosition) {
                mExoPlayer.seekTo(mResumeWindow, mResumePosition);
            }

            mExoPlayer.prepare(mediaSource);
            mExoPlayer.setPlayWhenReady(true);
        }
    }

    /**
     * Initializes the Media Session to be enabled with media buttons, transport controls, callbacks
     * and media controller.
     */
    private void initializeMediaSession() {

        // Create a MediaSessionCompat.
        mMediaSession = new MediaSession(this, TAG);

        // Enable callbacks from MediaButtons and TransportControls.
        mMediaSession.setFlags(
                MediaSession.FLAG_HANDLES_MEDIA_BUTTONS |
                        MediaSession.FLAG_HANDLES_TRANSPORT_CONTROLS);

        // Do not let MediaButtons restart the player when the app is not visible.
        mMediaSession.setMediaButtonReceiver(null);

        // Set an initial PlaybackState with ACTION_PLAY, so media buttons can start the player.
        mStateBuilder = new PlaybackState.Builder()
                .setActions(
                        PlaybackState.ACTION_PLAY |
                                PlaybackState.ACTION_PAUSE |
                                PlaybackState.ACTION_SKIP_TO_PREVIOUS |
                                PlaybackState.ACTION_PLAY_PAUSE);

        mMediaSession.setPlaybackState(mStateBuilder.build());

        // MySessionCallback has methods that handle callbacks from a media controller.
        mMediaSession.setCallback(new MySessionCallback());

        // Start the Media Session since the activity is active.
        mMediaSession.setActive(true);
    }

    //Release ExoPlayer
    private void releasePlayer() {
        mExoPlayer.stop();
        mExoPlayer.release();
        mExoPlayer = null;
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releasePlayer();
        mMediaSession.setActive(false);
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if((playbackState == Player.STATE_READY) && playWhenReady){
            mStateBuilder.setState(PlaybackState.STATE_PLAYING,
                    mExoPlayer.getCurrentPosition(), 1f);
        } else if((playbackState == Player.STATE_READY)){
            mStateBuilder.setState(PlaybackState.STATE_PAUSED,
                    mExoPlayer.getCurrentPosition(), 1f);
        }
        mMediaSession.setPlaybackState(mStateBuilder.build());
    }

    @Override
    public void onRepeatModeChanged(int repeatMode) {

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity() {

    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }

    /**
     * Media Session Callbacks, where all external clients control the player.
     */
    private class MySessionCallback extends MediaSession.Callback {
        @Override
        public void onPlay() {
            mExoPlayer.setPlayWhenReady(true);
        }

        @Override
        public void onPause() {
            mExoPlayer.setPlayWhenReady(false);
        }

        @Override
        public void onSkipToPrevious() {
            mExoPlayer.seekTo(0);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        outState.putInt(RESUME_WINDOW, mResumeWindow);
        outState.putLong(RESUME_POSITION, mResumePosition);
        outState.putBoolean(PLAYER_FULLSCREEN, mPlayerFullscreen);

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initializePlayer(Uri.parse(mSteps.get(selectedPosition).getVideoUrl()));

        if (mPlayerFullscreen) {
            ((ViewGroup) mPlayerView.getParent()).removeView(mPlayerView);
            mFullScreenDialog.addContentView(mPlayerView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            mFullScreenDialog.show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mPlayerView != null && mExoPlayer != null) {
            mResumeWindow = mExoPlayer.getCurrentWindowIndex();
            mResumePosition = Math.max(0, mExoPlayer.getContentPosition());

            mExoPlayer.release();
        }

        if (mFullScreenDialog != null)
            mFullScreenDialog.dismiss();
    }
}
