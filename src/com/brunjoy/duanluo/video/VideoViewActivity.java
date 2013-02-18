package com.brunjoy.duanluo.video;

import android.app.Activity;

public class VideoViewActivity extends Activity
{
//implements OnClickListener, OnBufferingUpdateListener, OnCompletionListener, OnPreparedListener, OnVideoSizeChangedListener, SurfaceHolder.Callback, OnErrorListener,
//        OnSeekBarChangeListener {
//
//    private String path = null;
//
//    String TAG = "VideoViewActivity";
//    private int mVideoWidth;
//    private int mVideoHeight;
//    private MediaPlayer mMediaPlayer;
//    private SurfaceView mPreview;
//    private SurfaceHolder holder;
//    private boolean mIsVideoSizeKnown = false;
//    private boolean mIsVideoReadyToBePlayed = false;
//    public static final int VIDEO_LAYOUT_ORIGIN = 0;
//    public static final int VIDEO_LAYOUT_SCALE = 1;
//    public static final int VIDEO_LAYOUT_STRETCH = 2;
//    public static final int VIDEO_LAYOUT_ZOOM = 3;
//    int mVideoLayout = VIDEO_LAYOUT_SCALE;
//    float mAspectRatio = 0;
//
//    // private Button mQuickTip;
//
//    @Override
//    public void onCreate(Bundle icicle) {
//        super.onCreate( icicle );
//        getWindow( ).setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN );
//        path = getIntent( ).getStringExtra( "path" );
//        if (path == null) {
//            Uri uri = getIntent( ).getData( );
//            path = uri.toString( );
//            if (path == null) {
//                finish( );
//                return;
//            }
//        }
//        // io.vov.utils.Log.sTag = null;
//        MyLog.e( TAG, "-----------------------path=" + path );
//        if (Vitamio.isInitialized( this )) {
//            setContentView( R.layout.video_view );
//            initView( path, getIntent( ).getStringExtra( "name" ) );
//        } else {
//            final ProgressDialog mPD = new ProgressDialog( VideoViewActivity.this );
//
//            new AsyncTask<Object, Object, Boolean>( ) {
//                @Override
//                protected void onPreExecute() {
//
//                    mPD.setCancelable( false );
//                    mPD.setMessage( getString( R.string.vitamio_init_decoders ) );
//                    mPD.show( );
//                }
//
//                @Override
//                protected Boolean doInBackground(Object... params) {
//                    return Vitamio.initialize( VideoViewActivity.this );
//                }
//
//                @Override
//                protected void onPostExecute(Boolean inited) {
//                    mPD.dismiss( );
//                    MyLog.e( TAG, "---------------onPostExecute------------  inited=" + inited );
//                    MyLog.e( TAG, "-----------------------path=" + path );
//                    if (inited) {
//                        setContentView( R.layout.video_view );
//                        initView( path, getIntent( ).getStringExtra( "name" ) );
//
//                    } else {
//                        finish( );
//                    }
//                }
//
//            }.execute( );
//        }
//
//    }
//
//    public void setVideoLayout(int layout, float aspectRatio) {
//        LayoutParams lp = mPreview.getLayoutParams( );
//        DisplayMetrics disp = getResources( ).getDisplayMetrics( );
//        int windowWidth = disp.widthPixels, windowHeight = disp.heightPixels;
//        float windowRatio = windowWidth / (float) windowHeight;
//        float videoRatio = aspectRatio <= 0.01f ? mMediaPlayer.getVideoAspectRatio( ) : aspectRatio;
//        int mSurfaceHeight = mVideoHeight;
//        int mSurfaceWidth = mVideoWidth;
//        if (VIDEO_LAYOUT_ORIGIN == layout && mSurfaceWidth < windowWidth && mSurfaceHeight < windowHeight) {
//            lp.width = (int) (mSurfaceHeight * videoRatio);
//            lp.height = mSurfaceHeight;
//        } else if (layout == VIDEO_LAYOUT_ZOOM) {
//            lp.width = windowRatio > videoRatio ? windowWidth : (int) (videoRatio * windowHeight);
//            lp.height = windowRatio < videoRatio ? windowHeight : (int) (windowWidth / videoRatio);
//        } else {
//            boolean full = layout == VIDEO_LAYOUT_STRETCH;
//            lp.width = (full || windowRatio < videoRatio) ? windowWidth : (int) (videoRatio * windowHeight);
//            lp.height = (full || windowRatio > videoRatio) ? windowHeight : (int) (windowWidth / videoRatio);
//        }
//
//        holder.setFixedSize( mSurfaceWidth, mSurfaceHeight );
//        MyLog.d( "VIDEO: %dx%dx%f, Surface: %dx%d, LP: %dx%d, Window: %dx%dx%f", mVideoWidth, mVideoHeight, mAspectRatio, mSurfaceWidth, mSurfaceHeight, lp.width, lp.height,
//                windowWidth, windowHeight, windowRatio );
//        mVideoLayout = layout;
//        mAspectRatio = aspectRatio;
//        mPreview.setLayoutParams( lp );
//    }
//
//    private void playVideo() {
//        doCleanUp( );
//        try {
//            mMediaPlayer = new MediaPlayer( this );
//            // mMediaPlayer.setBufferSize( 1*1024*1024 );
//            mMediaPlayer.setDataSource( this, Uri.parse( path ) );
//            mMediaPlayer.setDisplay( holder );
//            mMediaPlayer.prepareAsync( );
//            mMediaPlayer.setScreenOnWhilePlaying( true );
//            mMediaPlayer.setOnBufferingUpdateListener( this );
//            mMediaPlayer.setOnCompletionListener( this );
//            mMediaPlayer.setOnPreparedListener( new OnPreparedListener( ) {
//
//                @Override
//                public void onPrepared(MediaPlayer arg0) {
//                    Log.e( TAG, "video  height=" + mMediaPlayer.getVideoHeight( ) );
//                    Log.e( TAG, "video   width=" + mMediaPlayer.getVideoWidth( ) );
//                    Log.d( TAG, "onPrepared called" );
//                    setVideoLayout( mVideoLayout, mAspectRatio );
//                    mIsVideoReadyToBePlayed = true;
//                    if (mIsVideoReadyToBePlayed && mIsVideoSizeKnown) {
//                        startVideoPlayback( );
//                    }
//
//                    rePlayNum = 0;
//                    show( 3 );
//
//                }
//            } );
//            mMediaPlayer.setOnVideoSizeChangedListener( this );
//            mMediaPlayer.setOnErrorListener( this );
//            mMediaPlayer.setVideoQuality( MediaPlayer.VIDEOQUALITY_HIGH );
//
//        } catch (Exception e) {
//        }
//    }
//
//    public void onBufferingUpdate(MediaPlayer arg0, int arg1) {
//        if (arg1 > 90) {
//
//            findViewById( R.id.video_loading ).setVisibility( View.GONE );
//            setDuration( mMediaPlayer.getDuration( ) );
//        }
//        mHandler.sendEmptyMessage( STARTPLAYING );
//        MyLog.e( TAG, "-------------------onBufferingUpdate----arg1=" + arg1 + " w=" + mMediaPlayer.getVideoWidth( ) + "  h=" + mMediaPlayer.getVideoHeight( ) );
//    }
//
//    final static int STARTPLAYING = 1;
//    final static int SEEKTO = 2;
//    Handler mHandler = new Handler( ) {
//        public void handleMessage(android.os.Message msg) {
//            switch (msg.what) {
//            case STARTPLAYING:
//                removeMessages( STARTPLAYING );
//                setCurrent( mMediaPlayer.getCurrentPosition( ) );
//                sendEmptyMessageDelayed( STARTPLAYING, 1000 );
//                break;
//            case SEEKTO:
//                mMediaPlayer.seekTo( msg.arg1 * 1000 );
//                mHandler.removeMessages( SEEKTO );
//                mHandler.removeMessages( STARTPLAYING );
//                mHandler.sendEmptyMessageDelayed( STARTPLAYING, 3000 );
//                break;
//            default:
//                break;
//            }
//        }
//    };
//
//    public void onCompletion(MediaPlayer arg0) {
//        Log.d( TAG, "onCompletion called" );
//        mHandler.removeMessages( STARTPLAYING );
//        // finish( );
//    }
//
//    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
//        if (width == 0 || height == 0) {
//            Log.e( TAG, "invalid video width(" + width + ") or height(" + height + ")" );
//            return;
//        }
//        Log.e( TAG, "onVideoSizeChanged  width(" + width + ") or height(" + height + ")" );
//        mIsVideoSizeKnown = true;
//        mVideoWidth = width;
//        mVideoHeight = height;
//        setVideoLayout( mVideoLayout, mAspectRatio );
//        if (mIsVideoReadyToBePlayed && mIsVideoSizeKnown) {
//            startVideoPlayback( );
//        }
//    }
//
//    @Override
//    public void onPrepared(MediaPlayer mediaplayer) {
//
//        Log.d( TAG, "onPrepared called" );
//        setVideoLayout( mVideoLayout, mAspectRatio );
//        mIsVideoReadyToBePlayed = true;
//        if (mIsVideoReadyToBePlayed && mIsVideoSizeKnown) {
//            startVideoPlayback( );
//        }
//
//        rePlayNum = 0;
//        show( 3 );
//    }
//
//    public void surfaceChanged(SurfaceHolder surfaceholder, int i, int j, int k) {
//        Log.d( TAG, "surfaceChanged called" + i + "  " + j + "   " + k );
//
//    }
//
//    public void surfaceDestroyed(SurfaceHolder surfaceholder) {
//        Log.d( TAG, "surfaceDestroyed called" );
//    }
//
//    public void surfaceCreated(SurfaceHolder holder) {
//        Log.e( TAG, "surfaceCreated " );
//        playVideo( );
//    }
//
//    private void releaseMediaPlayer() {
//        mHandler.removeMessages( STARTPLAYING );
//        mHandler.removeMessages( SEEKTO );
//        if (mMediaPlayer != null) {
//            mMediaPlayer.release( );
//            mMediaPlayer = null;
//        }
//    }
//
//    private void doCleanUp() {
//        mVideoWidth = 0;
//        mVideoHeight = 0;
//        mIsVideoReadyToBePlayed = false;
//        mIsVideoSizeKnown = false;
//    }
//
//    private void startVideoPlayback() {
//        holder.setFixedSize( mVideoWidth, mVideoHeight );
//        mMediaPlayer.start( );
//
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause( );
//        releaseMediaPlayer( );
//        doCleanUp( );
//    }
//
//    @Override
//    public boolean dispatchTouchEvent(MotionEvent event) {
//        if (!signUpFlag) {
//
//            MyLog.d( TAG, "11111111111111 isShowing=" + isShowing );
//            if (!isShowing) {
//                show( 3 );
//            }
//        }
//        return super.dispatchTouchEvent( event );
//    }
//
//    private byte rePlayNum = 1;
//
//    // private Animation mAnimationFade_in, mAnimationFade_out;
//    private Button btnBack, btnSignUp, btnStartPause;
//    private TextView tvTip, tvName, tvCurrent, tvDuration;// tvTip 显示加载信息
//    private VolumeManager mVolumeManager;
//
//    // ViewControllerManager navigationController;
//    // LKSlideView panel;
//    // private LKSlideView slideView;
//    // private ViewControllerManager manager;
//    private SeekBar mVolumeSeekBar, mVideoSeekBar;
//
//    private void initView(String path2, String name) {
//
//        mVolumeManager = VolumeManager.getInstance( this );
//
//        tvName = (TextView) findViewById( R.id.tv_video_name );
//        tvCurrent = (TextView) findViewById( R.id.tv_video_current );
//        tvDuration = (TextView) findViewById( R.id.tv_video_duration );
//        tvTip = (TextView) findViewById( R.id.video_tip );
//        btnBack = (Button) findViewById( R.id.btn_video_left );
//        // btnSignUp = (ImageButton) findViewById( R.id.btn_video_rigth );
//        btnStartPause = (Button) findViewById( R.id.btn_video_startOrPause );
//        mPreview = (SurfaceView) findViewById( R.id.mSurfaceView );
//
//        mVolumeSeekBar = (SeekBar) findViewById( R.id.volunme_seekbar );
//        mVolumeManager.setSeekBar( mVolumeSeekBar );
//        mVideoSeekBar = (SeekBar) findViewById( R.id.video_seekbar );
//        mVideoSeekBar.setOnSeekBarChangeListener( this );
//        // setContentView( panel );
//        // slideView = panel;
//        // manager = navigationController;
//        mVolumeSeekBar.setOnSeekBarChangeListener( this );
//        this.holder = mPreview.getHolder( );
//        holder.addCallback( this );
//        btnBack.setOnClickListener( this );
//        // btnSignUp.setOnClickListener( this );
//        btnStartPause.setOnClickListener( this );
//        tvName.setText( name );
//
//    }
//
//    int perValue = 60 * 1000;
//
//    void setCurrent(long current) {
//        current = (long) (current * 0.001);
//        tvCurrent.setText( getTime( current ) );
//        mVideoSeekBar.setProgress( (int) current );
//    }
//
//    // SimpleDateFormat sdf = new SimpleDateFormat( "HH:mm:ss" );
//
//    private String getTime(long time) {
//        int m = (int) (time / 60);
//        int s = (int) ((time - m * 60));
//        // MyLog.d( TAG, "time  =" + m + ":" + s );
//        return (m < 10 ? ("0" + m) : m) + ":" + (s < 10 ? ("0" + s) : s);
//        // return sdf.format( time );
//    }
//
//    void setDuration(long duration) {
//        duration = (long) (duration * 0.001);
//        MyLog.d( TAG, "duration  =" + duration );
//        tvDuration.setText( getTime( duration ) );
//        mVideoSeekBar.setMax( (int) duration );
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy( );
//        if (mVolumeManager != null)
//            mVolumeManager.clear( );
//        if (fileName != null) {
//            File file = new File( fileName );
//            if (file.exists( ))
//                file.delete( );
//        }
//        releaseMediaPlayer( );
//        doCleanUp( );
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume( );
//
//    }
//
//    private boolean isShowing = false;
//
//    public void hide() {
//        isShowing = false;
//        findViewById( R.id.video_menu ).setVisibility( View.INVISIBLE );
//
//    }
//
//    public boolean isShowing() {
//        return isShowing;
//    }
//
//    public void show() {
//        if (!isShowing) {
//            findViewById( R.id.video_menu ).setVisibility( View.VISIBLE );
//        }
//        View view = findViewById( R.id.video_menu );
//        // view.bringToFront( );
//        view.requestFocus( );
//        isShowing = true;
//
//    }
//
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//
//        switch (keyCode) {
//        case KeyEvent.KEYCODE_VOLUME_DOWN:
//            mVolumeManager.VolumeDown( );
//            return true;
//        case KeyEvent.KEYCODE_VOLUME_UP:
//            mVolumeManager.VolumeUp( );
//            return true;
//        case KeyEvent.KEYCODE_BACK:
//            if (signUpFlag) {
//                resetShow( );
//                return true;
//            }
//
//        default:
//            return super.onKeyDown( keyCode, event );
//
//        }
//    }
//
//    public void show(int timeout) {
//        if (!isShowing) {
//            findViewById( R.id.video_menu ).setVisibility( View.VISIBLE );
//
//            getWindow( ).clearFlags( WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON );
//        }
//        View view = findViewById( R.id.video_menu );
//        // view.bringToFront( );
//        view.requestFocus( );
//        MyLog.d( TAG, "222222222222 isShowing=" + isShowing );
//        isShowing = true;
//
//    }
//
//    private static boolean signUpFlag = false;
//
//    void capture() {
//
//        if (!mMediaPlayer.isPlaying( )) {
//
//            return;
//
//        }
//        runOnUiThread( new Runnable( ) {
//            @Override
//            public void run() {
//                Bitmap bitmap = mMediaPlayer.getCurrentFrame( );
//                if (bitmap == null) {
//                    return;
//                }
//                if (fileName != null) {
//                    File file = new File( fileName );
//                    if (file.exists( ))
//                        file.delete( );
//                }
//                fileName = BitmapUtil.saveUserName( bitmap );
//            }
//        } );
//
//    }
//
//    void resetShow() {
//        signUpFlag = false;
//        Animation mAnimation = AnimationUtils.loadAnimation( this, R.anim.push_out_right );
//        mAnimation.setFillAfter( true );
//        // mAnimation.setFillAfter( true );
//        // mAnimation = AnimationUtils.loadAnimation( this, R.anim.push_out_right );
//        show( 3 );
//        // MyHandler.getInstance( ).sendMyEmptyMessageDelayed( 88, 1000, mCallBack );
//
//        // mHorizontalScrollView.scrollTo( findViewById( R.id.quick_layout ).getWidth( ), 0 );
//    }
//
//    private String fileName;
//
//    @Override
//    public void onClick(View v) {
//        getWindow( ).addFlags( WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON );
//        switch (v.getId( )) {
//        case R.id.btn_video_left://
//            finish( );
//            break;
//
//        case R.id.btn_video_startOrPause:
//            if (mMediaPlayer == null)
//                break;
//            if (mMediaPlayer.isPlaying( )) {
//                mMediaPlayer.pause( );
//                show( );
//            } else {
//                mMediaPlayer.start( );
//            }
//
//            if (mMediaPlayer.isPlaying( )) {
//                btnStartPause.setText( "暂停" );
//            } else {
//                btnStartPause.setText( "播放" );
//            }
//            break;
//
//        default:
//            break;
//        }
//
//    }
//
//    @Override
//    public boolean onError(MediaPlayer arg0, int arg1, int arg2) {
//        Log.d( TAG, "surfaceChanged rePlayNum=" + rePlayNum );
//        if (rePlayNum <= 2) {
//            try {
//                mMediaPlayer.setDataSource( this, Uri.parse( path ) );
//                mMediaPlayer.prepareAsync( );
//                mMediaPlayer.start( );
//            } catch (IllegalArgumentException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace( );
//            } catch (IllegalStateException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace( );
//            } catch (IOException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace( );
//            }
//        }
//        rePlayNum++;
//        return false;
//    }
//
//    @Override
//    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//    }
//
//    @Override
//    public void onStartTrackingTouch(SeekBar seekBar) {
//
//    }
//
//    @Override
//    public void onStopTrackingTouch(SeekBar seekBar) {
//        switch (seekBar.getId( )) {
//        case R.id.video_seekbar:
//            mHandler.removeMessages( SEEKTO );
//            mHandler.removeMessages( STARTPLAYING );
//            Message msg = Message.obtain( );
//            msg.what = SEEKTO;
//            msg.arg1 = seekBar.getProgress( );
//            mHandler.sendMessageDelayed( msg, 200 );
//            break;
//
//        default:
//            break;
//        }
//    }

}
