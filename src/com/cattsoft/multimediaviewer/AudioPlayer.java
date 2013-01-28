package com.cattsoft.multimediaviewer;

import java.io.IOException;

import org.teleal.cling.support.model.item.Item;
import org.teleal.cling.support.model.item.MusicTrack;

import android.app.Activity;
import android.app.Service;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.brunjoy.duanluo.imgdowner.ImageDownloader;
import com.brunjoy.duanluo.imgdowner.MyLog;
import com.brunjoy.video.R;
import com.brunjoy.weiget.BaseServerActivity;
import com.brunjoy.weiget.MyDataListManager;

public class AudioPlayer extends Activity {

    private static final int UPDATE_DURATION = 0;
    private MediaPlayer mMediaPlayer;
    private AudioManager mAudioManager;
    private ButtonClickListener listener;
    private int current_position;
    private boolean isStop = false;

    private TextView musicname; //
    private TextView artist; //
    private ImageView albumphoto;
    private TextView duration; //
    private TextView playtime; //
    private Button volume; //
    private Button mode; //
    private ImageButton play; //
    private ImageButton last; //
    private ImageButton next; //
    private SeekBar seekbar; //

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        requestWindowFeature( Window.FEATURE_NO_TITLE );
        setContentView( R.layout.activity_audio_play );
    }

    private void loadData() {
        init( );
        try {
            mMediaPlayer.reset( );
            mMediaPlayer.setDataSource( getIntent( ).getStringExtra( "path" ) );
            mMediaPlayer.prepare( );
        } catch (IllegalArgumentException e) {
            e.printStackTrace( );
        } catch (IllegalStateException e) {
            e.printStackTrace( );
        } catch (IOException e) {
            e.printStackTrace( );
        }
    }

    private void initView() {
        // musicname.setText( getIntent( ).getStringExtra( "name" ) );
        // artist.setText( getIntent( ).getStringExtra( "artist" ) );
        // MainActivitySrcN.imageDownloader.download( getIntent( ).getStringExtra( "alumbPhoto" ), albumphoto );
        seekbar.setMax( mMediaPlayer.getDuration( ) );
        setValue( getIntent( ).getStringExtra( "name" ), getIntent( ).getStringExtra( "artist" ), getIntent( ).getStringExtra( "alumbPhoto" ) );
    }

    private void setValue(String name, String artistStr, String albumphotoStr) {
        musicname.setText( name );
        artist.setText( artistStr );
        albumphoto.setImageResource( R.drawable.ablum_deflaut );
        ImageDownloader.getInstacne( ).download( albumphotoStr + ImageDownloader.SOURCE_PIC, albumphoto );

    }

    void playNext() {
        Item item = MyDataListManager.getInstance( AudioPlayer.this ).getNext( MyDataListManager.FILE_TYPE.AUDIO );
        if (item == null)
            return;
        final MusicTrack musicTrack = (MusicTrack) item;

        runOnUiThread( new Runnable( ) {

            @Override
            public void run() {
                play( musicTrack );
            }
        } );
    }

    void playLast() {
        Item item = MyDataListManager.getInstance( AudioPlayer.this ).getLast( MyDataListManager.FILE_TYPE.AUDIO );
        if (item == null) {
            return;
        }

        final MusicTrack musicTrack = (MusicTrack) item;
        runOnUiThread( new Runnable( ) {

            @Override
            public void run() {
                play( musicTrack );
            }
        } );

    }

    private void init() {
        mMediaPlayer = new MediaPlayer( );
        mMediaPlayer.setOnCompletionListener( new OnCompletionListener( ) {

            @Override
            public void onCompletion(MediaPlayer mp) {
                playNext( );
            }
        } );
        mAudioManager = (AudioManager) getSystemService( Service.AUDIO_SERVICE );
        listener = new ButtonClickListener( );
        play = (ImageButton) findViewById( R.id.playBtn );
        play.setOnClickListener( listener );
        last = (ImageButton) findViewById( R.id.lastOne );
        last.setOnClickListener( listener );
        next = (ImageButton) findViewById( R.id.nextOne );
        next.setOnClickListener( listener );
        volume = (Button) findViewById( R.id.volume );
        volume.setOnClickListener( listener );
        mode = (Button) findViewById( R.id.mode );
        mode.setOnClickListener( listener );
        musicname = (TextView) findViewById( R.id.musicname );
        artist = (TextView) findViewById( R.id.artist );
        albumphoto = (ImageView) findViewById( R.id.albumPic );
        playtime = (TextView) findViewById( R.id.playtime );
        duration = (TextView) findViewById( R.id.duration );
        seekbar = (SeekBar) findViewById( R.id.seekbar );

        seekbar.setOnSeekBarChangeListener( new OnSeekBarChangeListener( ) {

            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mMediaPlayer.seekTo( progress );
                }
            }
        } );
    }

    protected void play(MusicTrack musicTrack) {
        MyLog.d( "AudioPlayer", "AudioPlayer=" + musicTrack.getFirstResource( ).getValue( ) );
        try {
            setValue( musicTrack.getTitle( ), musicTrack.getCreator( ), musicTrack.getAlbumArtURI( ) == null ? "" : musicTrack.getAlbumArtURI( ).toString( ) );
            mMediaPlayer.stop( );
            mMediaPlayer.reset( );
            mMediaPlayer.setDataSource( musicTrack.getFirstResource( ).getValue( ) );
            mMediaPlayer.prepare( );

            mMediaPlayer.start( );
            duration.setText( toTime( mMediaPlayer.getDuration( ) ) );
            myHandler.sendEmptyMessage( UPDATE_DURATION );
            seekbar.setMax( mMediaPlayer.getDuration( ) );
        } catch (IllegalArgumentException e) {
            e.printStackTrace( );
        } catch (IllegalStateException e) {
            e.printStackTrace( );
            Toast.makeText( this, "不能播放该音乐", Toast.LENGTH_LONG ).show( );
        } catch (IOException e) {
            if (musicTrack.getFirstResource( ).getValue( ).startsWith( "http://" ))
                Toast.makeText( this, "可能因为网络的原因，播放失败", Toast.LENGTH_LONG ).show( );
            e.printStackTrace( );
        }
    }

    @Override
    protected void onStart() {
        runOnUiThread( new Runnable( ) {
            @Override
            public void run() {
                try {
                    loadData( );
                    initView( );
                    play.setBackgroundResource( R.drawable.pause_selecor );
                    myHandler.sendEmptyMessage( UPDATE_DURATION );
                    mMediaPlayer.start( );
                    duration.setText( toTime( mMediaPlayer.getDuration( ) ) );
                } catch (Exception e) {
                    init( );
                    Toast.makeText( AudioPlayer.this, "播放失败", Toast.LENGTH_SHORT ).show( );
                }
            }
        } );

        super.onStart( );
    }

    @Override
    protected void onDestroy() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release( );
        }
        myHandler.removeMessages( UPDATE_DURATION );
        myHandler = null;
        super.onDestroy( );
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mMediaPlayer != null) {
                mMediaPlayer.stop( );
                isStop = true;
            }
            AudioPlayer.this.finish( );
        }
        return super.onKeyDown( keyCode, event );
    }

    Handler myHandler = new Handler( ) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage( msg );
            try {
                if (msg.what == UPDATE_DURATION) {
                    if (mMediaPlayer != null && !isStop) {
                        current_position = mMediaPlayer.getCurrentPosition( );
                    }
                    playtime.setText( toTime( current_position ) );
                    seekbar.setProgress( current_position );
                    // sendEmptyMessage( UPDATE_DURATION );
                    sendEmptyMessageDelayed( UPDATE_DURATION, 200 );
                }
            } catch (Exception e) {
                e.printStackTrace( );
            }
        }
    };

    /**
     * 
     * 
     * @param time
     * @return
     */
    public String toTime(long time) {

        time /= 1000;
        long minute = time / 60;
        long second = time % 60;
        minute %= 60;
        return String.format( "%02d:%02d", minute, second );
    }

    /**
     * 
     * 
     * @author Administrator
     * 
     */
    private class ButtonClickListener implements View.OnClickListener {

        public void onClick(View v) {
            switch (v.getId( )) {

            case R.id.playBtn:
                if (mMediaPlayer.isPlaying( )) {
                    play.setBackgroundResource( R.drawable.play_selecor );
                    mMediaPlayer.pause( );
                    myHandler.removeMessages( UPDATE_DURATION );
                } else {
                    play.setBackgroundResource( R.drawable.pause_selecor );
                    myHandler.sendEmptyMessage( UPDATE_DURATION );
                    mMediaPlayer.start( );
                }
                break;
            case R.id.lastOne:
                playLast( );
                // Toast.makeText( AudioPlayer.this, "……", Toast.LENGTH_SHORT ).show( );
                break;
            case R.id.nextOne:
                playNext( );
                // Toast.makeText( AudioPlayer.this, "……", Toast.LENGTH_SHORT ).show( );
                break;
            case R.id.volume:
                mAudioManager.adjustStreamVolume( AudioManager.STREAM_MUSIC, AudioManager.ADJUST_SAME, AudioManager.FLAG_SHOW_UI );
                break;
            case R.id.mode:
                Toast.makeText( AudioPlayer.this, "……", Toast.LENGTH_SHORT ).show( );
                break;
            }
        }
    }
}
