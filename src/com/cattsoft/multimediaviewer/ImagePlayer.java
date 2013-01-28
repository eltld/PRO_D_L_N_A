package com.cattsoft.multimediaviewer;

import org.teleal.cling.support.model.item.Item;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.brunjoy.duanluo.imgdowner.BitmapUtil;
import com.brunjoy.duanluo.imgdowner.ImageDownloader;
import com.brunjoy.duanluo.imgdowner.MyLog;
import com.brunjoy.video.R;
import com.brunjoy.weiget.BaseServerActivity;
import com.brunjoy.weiget.MyAsynDownFileTask;
import com.brunjoy.weiget.MyDataListManager;

public class ImagePlayer extends Activity implements OnTouchListener, OnClickListener {
    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;
    private int currentMode = NONE;
    private static final float MAX_SCALE = 4f;
    private float minScale;
    private Bitmap bitmap;
    private ImageView imageview;
    private Matrix matrix = new Matrix( );
    private Matrix savedMatrix = new Matrix( );
    private DisplayMetrics dm;
    private PointF pre = new PointF( );
    private PointF mid = new PointF( );
    private float dist = 1f;
    private TextView tvProgress/*, tvPhotoName*/;
    private View ShowProgress;
    private Button btnSliderPlay;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_image_play );
//        btnSliderPlay = (Button) findViewById( R.id.image_btnSlide );
        btnSliderPlay.setOnClickListener( this );
        imageview = (ImageView) findViewById( R.id.image_area );
        imageview.setOnTouchListener( this );
        tvProgress = (TextView) findViewById( R.id.tvProgress );
//        tvPhotoName = (TextView) findViewById( R.id.tvPhotoTitle );
        ShowProgress = findViewById( R.id.showProgress );
        dm = new DisplayMetrics( );
        getWindowManager( ).getDefaultDisplay( ).getMetrics( dm );
        ImageDownloader.getInstacne( ).cancelTasks( );
        Bundle bundle = this.getIntent( ).getExtras( );
        String path = bundle.getString( "path" );
//        tvPhotoName.setText( bundle.getString( "name" ) );
        MyLog.d( "path", "path=" + path );
        downPic( path );
    }

    @Override
    protected void onPause() {
        ImageDownloader.getInstacne( ).cancelTasks( );
        super.onPause( );
    }

    @Override
    protected void onStart() {
        super.onStart( );
    }

    private void downPic(String path) {

        new MyAsynDownFileTask( new MyAsynDownFileTask.ProgressInterface( ) {
            @Override
            public void success(String msg) {
                try {
                    bitmap = BitmapFactory.decodeFile( msg );
                } catch (OutOfMemoryError e) {
                    Toast.makeText( ImagePlayer.this, "因为此图片过大，故经过压缩处理", Toast.LENGTH_LONG ).show( );
                    try {
                        bitmap = BitmapUtil.makeBitmap( msg, 1024 * 1024 );
                    } catch (Exception e1) {
                        bitmap = BitmapUtil.makeBitmap( msg, 512 * 1024 );
                        Toast.makeText( ImagePlayer.this, "图片太大", Toast.LENGTH_LONG ).show( );
                    }
                } catch (Exception e) {
                    Toast.makeText( ImagePlayer.this, "解析图片错误", Toast.LENGTH_LONG ).show( );
                }
                if (bitmap == null) {
                    finish( );
                    return;
                }
                imageview.setImageBitmap( bitmap );
                matrix.reset( );
                savedMatrix.reset( );
                minZoom( );
                checkView( );
                imageview.setImageMatrix( matrix );
                ShowProgress.setVisibility( View.GONE );
            }

            @Override
            public void progresss(int progress) {
                tvProgress.setText( (progress > 100 ? 100 : progress) + "%" );
                MyLog.d( "progress", "progress" + progress );
            }

            @Override
            public void failure(String failure) {
                // mPD.dismiss( );
                ShowProgress.setVisibility( View.GONE );
                finish( );
            }
        } ).execute( path );
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        switch (event.getKeyCode( )) {
        case KeyEvent.KEYCODE_BACK:
            finish( );
            break;
        default:
            break;
        }
        return super.dispatchKeyEvent( event );
    }

    private void checkView() {
        float p[] = new float[9];
        matrix.getValues( p );
        if (currentMode == ZOOM) {
            if (p[0] < minScale) {
                matrix.setScale( minScale, minScale );
            }
            if (p[0] > MAX_SCALE) {
                matrix.set( savedMatrix );
            }
        }
        center( );
    }

    private void minZoom() {
        if (bitmap == null) {
            return;
        }
        minScale = Math.min( (float) dm.widthPixels / (float) bitmap.getWidth( ), (float) dm.heightPixels / (float) bitmap.getHeight( ) );
        if (minScale < 1.0) {
            matrix.postScale( minScale, minScale );
        }
    }

    private void center() {
        center( true, true );
    }

    protected void center(boolean horizontal, boolean vertical) {
        Matrix m = new Matrix( );
        m.set( matrix );
        RectF rect = new RectF( 0, 0, bitmap.getWidth( ), bitmap.getHeight( ) );
        m.mapRect( rect );

        float height = rect.height( );
        float width = rect.width( );

        float deltaX = 0, deltaY = 0;

        if (vertical) {
            int screenHeight = dm.heightPixels;
            if (height < screenHeight) {
                deltaY = (screenHeight - height) / 2 - rect.top;
            } else if (rect.top > 0) {
                deltaY = -rect.top;
            } else if (rect.bottom < screenHeight) {
                deltaY = imageview.getHeight( ) - rect.bottom;
            }
        }
        if (horizontal) {
            int screenWidth = dm.widthPixels;
            if (width < screenWidth) {
                deltaX = (screenWidth - width) / 2 - rect.left;
            } else if (rect.left > 0) {
                deltaX = -rect.left;
            } else if (rect.right < screenWidth) {
                deltaX = screenWidth - rect.right;
            }
        }

        MyLog.d( "ImagePlayer", "deltaX=" + deltaX + "   deltaY=" + deltaY );
        matrix.postTranslate( deltaX, deltaY );
        if (currentMode == NONE) {
            if ((lastX - deltaX) > dm.widthPixels * 0.5) {
                MyLog.e( "ImagePlayer", "left  last" );
                playNext( true );
            } else if ((deltaX - lastX) > dm.widthPixels * 0.5) {
                MyLog.e( "ImagePlayer", "rith  next" );
                playLast( );
            }
            MyLog.e( "ImagePlayer", "deltaX=" + deltaX + "   lastX=" + lastX + "  (lastX-deltaX)= " + (lastX - deltaX) );
            lastX = 0;
        } else
            lastX = deltaX;
    }

    private void playLast() {
        final Item item = MyDataListManager.getInstance( ImagePlayer.this ).getLast( MyDataListManager.FILE_TYPE.IMAGE );
        if (item == null)
            return;
        runOnUiThread( new Runnable( ) {

            @Override
            public void run() {
                play( item, true );
            }
        } );
    }

    private void playNext(final boolean isShowLoading) {
        final Item item = MyDataListManager.getInstance( ImagePlayer.this ).getNext( MyDataListManager.FILE_TYPE.IMAGE );
        if (item == null)
            return;
        runOnUiThread( new Runnable( ) {

            @Override
            public void run() {
                play( item, isShowLoading );
            }
        } );
    }

    protected void play(Item item, boolean isShowLoading) {
        if (isShowLoading) {
            ShowProgress.setVisibility( View.VISIBLE );
        }
        downPic( item.getFirstResource( ).getValue( ) );
//        tvPhotoName.setText( item.getTitle( ) );
    }

    private float lastX = 0;

    private float distance(MotionEvent event) {
        float x = event.getX( 0 ) - event.getX( 1 );
        float y = event.getY( 0 ) - event.getY( 1 );
        return FloatMath.sqrt( x * x + y * y );
    }

    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX( 0 ) + event.getX( 1 );
        float y = event.getY( 0 ) + event.getY( 1 );
        point.set( x / 2, y / 2 );
    }

    private static final int SLIDER_PLAY = 1;
    private Handler mHandler = new Handler( ) {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
            case SLIDER_PLAY:
                removeMessages( SLIDER_PLAY );
                playNext( false );
                sendEmptyMessageDelayed( SLIDER_PLAY, 3000 );
                break;

            default:
                break;
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId( )) {
//        case R.id.image_btnSlide:// 自动播放图片
//            mHandler.removeMessages( SLIDER_PLAY );
//            mHandler.sendEmptyMessageDelayed( SLIDER_PLAY, 3000 );
//            break;

        default:
            break;
        }

    }
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction( ) & MotionEvent.ACTION_MASK) {

        case MotionEvent.ACTION_DOWN:
            savedMatrix.set( matrix );
            pre.set( event.getX( ), event.getY( ) );
            currentMode = DRAG;
            break;

        case MotionEvent.ACTION_POINTER_DOWN:
            dist = distance( event );
            if (dist > 10f) {
                savedMatrix.set( matrix );
                midPoint( mid, event );
                currentMode = ZOOM;
            }
            break;

        case MotionEvent.ACTION_UP:
        case MotionEvent.ACTION_POINTER_UP:
            currentMode = NONE;
            break;

        case MotionEvent.ACTION_MOVE:
            if (currentMode == DRAG) {
                matrix.set( savedMatrix );
                matrix.postTranslate( event.getX( ) - pre.x, event.getY( ) - pre.y );
            } else if (currentMode == ZOOM) {
                float newDist = distance( event );
                if (newDist > 10f) {
                    matrix.set( savedMatrix );
                    float tScale = newDist / dist;
                    matrix.postScale( tScale, tScale, mid.x, mid.y );
                }
            }
            break;
        }

        imageview.setImageMatrix( matrix );
        checkView( );
        return false;
    }
}
