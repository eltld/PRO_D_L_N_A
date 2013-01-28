package com.brunjoy.weiget;

import com.brunjoy.weiget.LKCoverView.OnLKCoverViewListener;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

public class LKSlideView extends FrameLayout implements OnLKCoverViewListener, Runnable {

    private static final String TAG = "LKSlideView";
    Context mContext;

    public LKSlideView(Context context) {
        super( context );
        // TODO Auto-generated constructor stub
        this.mContext = context;
        LayoutParams layoutParams = new LayoutParams( LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT );
        this.setLayoutParams( layoutParams );

        leftMenu = new FrameLayout( context );
        LayoutParams leftlp = new LayoutParams( LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT );
        leftMenu.setLayoutParams( leftlp );
        leftMenu.setVisibility( View.INVISIBLE );
        leftMenu.setBackgroundColor( Color.BLACK );
        addView( leftMenu );

        rightMenu = new FrameLayout( context );
        LayoutParams rightlp = new LayoutParams( LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT );
        rightMenu.setLayoutParams( rightlp );
        rightMenu.setVisibility( View.INVISIBLE );
        addView( rightMenu );
        rightMenu.setBackgroundColor( Color.BLACK );

        mainView = new LKCoverView( mContext );
        /**
         * 当界面运行时 执行事件 要不然 getWidth() 为0
         */
        this.post( this );
    }

    public void run() {
        // TODO Auto-generated method stub
        mainView.initLKCoverView( getWidth( ) );
        mainView.setOnCoverViewListener( this );
        addView( mainView );
    }

    private FrameLayout leftMenu;
    private FrameLayout rightMenu;

    public FrameLayout getLeftMenu() {
        return leftMenu;
    }

    private LKCoverView mainView;

    public FrameLayout getMainView() {
        return mainView.getContentView( );
    }

    public FrameLayout getRightMenu() {
        return rightMenu;
    }

    /**
     * 把主界面 移动到右边 然后 执行事件 再返回 .用于切换界面
     */
    public void startViewSwitcher(int direction, Runnable runnable) {
        switch (direction) {
        case Direction_PushLeft:
            mainView.smoothScrollTo( 0, 0 );
            break;
        case Direction_PushRight:
            mainView.smoothScrollTo( mainView.getPageWidth( ) * 2, 0 );
            break;
        }
        this.switcheEvent = runnable;
    }

    private Runnable switcheEvent;
    public static final int Direction_PushLeft = 1;
    public static final int Direction_PushRight = 2;

    public void transLeftShow() {
        if (mainView.isShowMenu( )) {
            mainView.resetView( );
        } else {
            rightMenu.setVisibility( View.INVISIBLE );
            leftMenu.setVisibility( View.VISIBLE );
            mainView.showLeftPage( );
        }
    }

    public void resetShow() {
        mainView.resetView( );
    }

    public void transRightShow() {
        if (mainView.isShowMenu( )) {
            mainView.resetView( );
        } else {
            rightMenu.setVisibility( View.VISIBLE );
            leftMenu.setVisibility( View.INVISIBLE );
            mainView.showRightPage( );
        }
    }

    public void hideMenu() {
        leftMenu.setVisibility( View.INVISIBLE );
        rightMenu.setVisibility( View.INVISIBLE );
    }

    public void onScrollFinished(LKCoverView view) {
        // TODO Auto-generated method stub
        if (!mainView.isShowMenu( )) {
            Log.d( TAG, " Hide Menu" );
            hideMenu( );
        }
        if (switcheEvent != null) {
            this.switcheEvent.run( );
            this.mainView.resetView( );
            this.switcheEvent = null;
        }

    }

}
