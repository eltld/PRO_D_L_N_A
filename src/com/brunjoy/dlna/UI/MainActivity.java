package com.brunjoy.dlna.UI;

import java.util.ArrayList;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.teleal.cling.model.meta.Icon;
import org.teleal.cling.support.model.DIDLObject;
import org.teleal.cling.support.model.Res;
import org.teleal.cling.support.model.item.ImageItem;
import org.teleal.cling.support.model.item.MusicTrack;
import org.teleal.cling.support.model.item.VideoItem;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;
import com.brunjoy.dialogs.SelectDialog;
import com.brunjoy.duanluo.imgdowner.ImageDownloader;
import com.brunjoy.duanluo.imgdowner.ImageDownloader.ImageDownCallBack;
import com.brunjoy.duanluo.imgdowner.MyLog;
import com.brunjoy.video.R;
import com.brunjoy.video.activity.DeviceItem;
import com.wireme.mediaserver.MediaServer;
import com.wireme.util.Utils;

public class MainActivity extends SherlockFragmentActivity {
    SelectDialog mSelectDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme( R.style.Theme_Sherlock_Light );
        getSupportActionBar( ).setIcon( R.drawable.sym_def_app_icon );
        super.onCreate( savedInstanceState );
        ListView listView = new ListView( this );
        listView.setCacheColorHint( Color.TRANSPARENT );
        listView.setAdapter( adapter );
        listView.setOnItemClickListener( new OnItemClickListener( ) {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                DLNAServer.getInstance( MainActivity.this ).selectContent( adapter.getItem( arg2 ), arg2, MainActivity.this );
            }
        } );
        listView.setOnItemLongClickListener( new OnItemLongClickListener( ) {

            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                DIDLObject mItem = adapter.getItem( arg2 );
                // MyLog.e( TAG, "LongClick=" + mItem.toString( ) );

                mSelectDialog.clearItem( );
                mSelectDialog.addItem( "Title:" + mItem.getTitle( ) );
                mSelectDialog.addItem( "Creator:" + mItem.getCreator( ) );
                // mSelectDialog.addItem( "WriteStatus：" + (mItem.getWriteStatus( ) == null ? "unknow" : mItem.getWriteStatus( )) );
                Res res = mItem.getFirstResource( );
                if (res != null) {
                    mSelectDialog.addItem( "Type:" + res.getProtocolInfo( ).getContentFormat( ) );
                    mSelectDialog.addItem( "Duration:" + res.getDuration( ) );
                    mSelectDialog.addItem( "Size:" + Utils.converSizeToString( res.getSize( ) ) );
                    mSelectDialog.addItem( "Resolution:" + res.getResolution( ) );
                    mSelectDialog.addItem( "Value:" + res.getValue( ) );
                    // mSelectDialog.addItem("Protection:"+ res.getProtection( ) );
                }

                String type = null;
                if (res != null && res.getProtocolInfo( ) != null) {
                    type = res.getProtocolInfo( ).getContentFormat( );
                }
                if (type != null && type.toLowerCase( Locale.getDefault( ) ).startsWith( "video/" )) {
                    VideoItem item = (VideoItem) mItem;
                    mSelectDialog.setTitle( "Video Property" );
                    mSelectDialog.setIcon( item.getAlbumArtURI( ), R.drawable.sym_def_app_icon );
                } else if (type != null && type.toLowerCase( Locale.getDefault( ) ).startsWith( "image/" )) {
                    ImageItem item = (ImageItem) mItem;
                    mSelectDialog.setTitle( "Image Property" );
                    mSelectDialog.setIcon( item.getAlbumArtURI( ), R.drawable.sym_def_app_icon );

                } else if (type != null && type.toLowerCase( Locale.getDefault( ) ).startsWith( "audio/" )) {
                    MusicTrack item = (MusicTrack) mItem;
                    mSelectDialog.setTitle( "Auto Property" );
                    mSelectDialog.setIcon( item.getAlbumArtURI( ), R.drawable.sym_def_app_icon );
                } else {
                    mSelectDialog.setIcon( R.drawable.sym_def_app_icon );
                    mSelectDialog.setTitle( "Folder Property" );
                }

                // item.getProperties( );
                mSelectDialog.show( );
                return false;
            }
        } );
        setContentView( listView );
        DLNAServer.getInstance( this ).addListener( mListener );
        mSelectDialog = new SelectDialog( MainActivity.this ) {

            @Override
            public void onSelected(int position, String msg) {

                MyLog.e( TAG, "onSelected=" + position + "  msg=" + msg );
            }
        };

    }

    // private void onSelectPage(int position) {
    // switch (position) {
    // case 0:
    // // FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
    // // ft.add( arg0, arg1 );
    // break;
    //
    // default:
    // break;
    // }
    // }

    private DLNAListener mListener = new DLNAListener( ) {

        @Override
        public void deviceUpdate(ArrayList<DeviceItem> deviceList, boolean isAddDevice) {
            if (deviceList.size( ) == 1) {
                DLNAServer.getInstance( getApplicationContext( ) ).selectDevice( 0 );
                setTitle( deviceList.get( 0 ).getDevice( ).getDetails( ).getFriendlyName( ) );
            }
            MyLog.e( TAG, "deviceList=" + deviceList );
        }

        // public void updateBack(java.util.ArrayList<String> qurestParentsIDs) {
        // MyLog.i( TAG, "=============================================" );
        // MyLog.i( TAG, "=============================================" );
        // MyLog.i( TAG, "qurestParentsIDs=" + qurestParentsIDs );
        //
        // MyLog.e( TAG, "=============================================" );
        // MyLog.e( TAG, "=============================================" );
        // MyLog.e( TAG, "canBack=" + DLNAServer.getInstance( getApplicationContext( ) ).canBack( ) );
        // MyLog.e( TAG, "=============================================" );
        // MyLog.e( TAG, "=============================================" );
        // }

        public void updateContent(java.util.ArrayList<org.teleal.cling.support.model.DIDLObject> containerList) {
            if (DLNAServer.getInstance( getApplicationContext( ) ).canBack( )) {
                // if (DLNAServer.getInstance( getApplicationContext( ) ).canBack( )) {
                getSupportActionBar( ).setDisplayHomeAsUpEnabled( true );
                // } else {

                // }
            } else {
                getSupportActionBar( ).setDisplayHomeAsUpEnabled( false );
            }
            MyLog.e( TAG, "=============================================" );
            MyLog.e( TAG, "=============================================" );
            MyLog.e( TAG, "canBack=" + DLNAServer.getInstance( getApplicationContext( ) ).canBack( ) );
            MyLog.e( TAG, "=============================================" );
            MyLog.e( TAG, "=============================================" );
            if (!containerList.isEmpty( )) {
                adapter.clear( );
                adapter.addAll( containerList );
            } else {
                adapter.clearNow( );
                Toast.makeText( MainActivity.this, "亲，没有内容", Toast.LENGTH_LONG ).show( );
            }
            adapter.notifyDataSetChanged( );
            mRotaAnimation.cancel( );

        }
    };

    private MyBaseAdapter<DIDLObject> adapter = new MyBaseAdapter<DIDLObject>( ) {

        @Override
        public View bindView(int position, DIDLObject t, View view) {
            if (view == null) {
                view = getLayoutInflater( ).inflate( R.layout.deviceitem, null );
            }
            getSupportActionBar( ).setDisplayHomeAsUpEnabled( true );
            ImageView image = (ImageView) view.findViewById( R.id.itemDevice_thunm );
            TextView name = (TextView) view.findViewById( R.id.itemDeviceName );

            image.setImageResource( R.drawable.sym_def_app_icon );

            name.setText( t.getTitle( ) );

            Res res = t.getFirstResource( );
            String type = null;
            if (res != null && res.getProtocolInfo( ) != null) {
                type = res.getProtocolInfo( ).getContentFormat( );
            }
            if (type != null && type.toLowerCase( Locale.getDefault( ) ).startsWith( "video/" )) {
                VideoItem item = (VideoItem) t;
                ImageDownloader.getInstacne( ).download( item.getAlbumArtURI( ), image );
            } else if (type != null && type.toLowerCase( Locale.getDefault( ) ).startsWith( "image/" )) {
                ImageItem item = (ImageItem) t;
                ImageDownloader.getInstacne( ).download( item.getAlbumArtURI( ), image );

            } else if (type != null && type.toLowerCase( Locale.getDefault( ) ).startsWith( "audio/" )) {
                MusicTrack item = (MusicTrack) t;
                ImageDownloader.getInstacne( ).download( item.getAlbumArtURI( ), image );
            } else {
            }

            return view;
        }
    };

    @Override
    protected void onDestroy() {
        DLNAServer.getInstance( this ).onDestory( );
        super.onDestroy( );
    }

    private Animation mRotaAnimation;
    String TAG = "main";

    protected void onRotaView(View view) {
        if (view == null)
            return;
        if (mRotaAnimation == null) {
            mRotaAnimation = AnimationUtils.loadAnimation( this, R.anim.rota360 );
        }
        mRotaAnimation.setRepeatCount( 100000 );

        mRotaAnimation.setRepeatMode( Animation.RESTART );
        view.clearAnimation( );
        view.startAnimation( mRotaAnimation );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.e( TAG, "==========================================" );
        Log.e( TAG, "onCreateOptionsMenu" );

        SubMenu subMenu = menu.addSubMenu( 3, 100001, 0, "刷新" );
        subMenu.getItem( ).setShowAsActionFlags( MenuItem.SHOW_AS_ACTION_IF_ROOM );
        subMenu.setIcon( R.drawable.sx );

        // =========================
        subMenu = menu.addSubMenu( 0, 11, 0, "设备" );
        MenuItem subMenu1Item = subMenu.getItem( );

        subMenu1Item.setShowAsActionFlags( MenuItem.SHOW_AS_ACTION_IF_ROOM );

        // SubMenu sub = menu.addSubMenu( 0, 501, 0, R.string.search_lan ).setIcon( android.R.drawable.ic_menu_search );
        menu.addSubMenu( 0, 501, 0, R.string.search_lan ).setIcon( android.R.drawable.ic_menu_search );
        menu.addSubMenu( 0, 502, 0, R.string.toggle_debug_logging ).setIcon( android.R.drawable.ic_menu_info_details );
        menu.addSubMenu( 0, 503, 0, R.string.recommendApp ).setIcon( android.R.drawable.ic_menu_myplaces );
        menu.addSubMenu( 0, 504, 0, "Exit" ).setIcon( android.R.drawable.ic_delete );
        return super.onCreateOptionsMenu( menu );
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        MyLog.e( TAG, "==========================================" + item.getItemId( ) );
        SubMenu subMenu = item.getSubMenu( );
        if (subMenu != null) {
            subMenu.clear( );
        }
        switch (item.getItemId( )) {
        case 100001:
            MyLog.e( TAG, "==item.getSubMenu( )=" + item.getSubMenu( ).getItem( ) );
            MyLog.e( TAG, "==item.item.getSubMenu( ).getItem( ).getActionView( )( )=" + item.getSubMenu( ).getItem( ).getActionView( ) );
            MyLog.e( TAG, "==item.getActionView( )" + item.getActionView( ) );
            onRotaView( item.getSubMenu( ).getItem( ).getActionView( ) );
            break;
        case 501:
            DLNAServer.getInstance( this ).searchNetwork( );
            break;
        case 502:
            Logger logger = Logger.getLogger( "org.teleal.cling" );
            if (logger.getLevel( ).equals( Level.FINEST )) {
                Toast.makeText( this, R.string.disabling_debug_logging, Toast.LENGTH_SHORT ).show( );
                logger.setLevel( Level.INFO );
            } else {
                Toast.makeText( this, R.string.enabling_debug_logging, Toast.LENGTH_SHORT ).show( );
                logger.setLevel( Level.FINEST );
            }
            break;
        case 503:

            break;
        case 504:

            finish( );
            break;
        case R.id.homeAsUp:// 16908332
        case 16908332:
            DLNAServer.getInstance( this ).back( );
            break;
        default:
            if (subMenu != null) {
                ArrayList<DeviceItem> dlist = DLNAServer.getInstance( this ).getDeviceList( );
                for (int i = 0; i < dlist.size( ); i++) {
                    org.teleal.cling.model.meta.Device mDevice = dlist.get( i ).getDevice( );

                    MyLog.e( TAG, " mDevice.getIcons( )=" + mDevice );
                    MyLog.e( TAG, " mDevice.getIcons( )=" + dlist.get( i ).getHostUrl( ) );
                    // DLNAServer.getInstance( MainActivity.this ).getRemoteAddressByDesUrl( mDevice.getIdentity( ) );
                    String name = mDevice.getDetails( ).getFriendlyName( );
                    final MenuItem menuItem = subMenu.add( 1, i, 0, name )/* .setIcon( icon ) */;
                    // menuItem.setic
                    Icon[] icons = mDevice.getIcons( );
                    if (icons != null && icons.length > 0) {

                        MyLog.e( TAG, "mDevice.getDetails( ).getBaseURL( )" + mDevice.getDetails( ).getBaseURL( ) );
                        MyLog.e( TAG, " mDevice.getIcons( )=" + dlist.get( i ).getHostUrl( ) + icons[0].getUri( ).toString( ) );
                        String url = dlist.get( i ).getHostUrl( );
                        if (url == null) {
                            url = MediaServer.LocalURL;
                        }
                        ImageDownloader.getInstacne( ).download( url + icons[0].getUri( ).toString( ), new ImageDownCallBack( ) {

                            @Override
                            public void downCompleted(Bitmap bitmap) {
                                if (bitmap == null)
                                    return;
                                BitmapDrawable bd = new BitmapDrawable( MainActivity.this.getResources( ), bitmap );
                                menuItem.setIcon( bd );
                            }
                        } );

                    }
                }
            } else {
                DLNAServer.getInstance( this ).selectDevice( item.getItemId( ) );
                DeviceItem mDeviceItem = DLNAServer.getInstance( this ).getDeviceList( ).get( item.getItemId( ) );

                Icon[] icons = mDeviceItem.getDevice( ).getIcons( );
                if (icons != null && icons.length > 0) {
                    MyLog.e( TAG, "===============================" );
                    String url = mDeviceItem.getHostUrl( );
                    if (url == null) {
                        url = MediaServer.LocalURL;
                    }
                    ImageDownloader.getInstacne( ).download( url + icons[0].getUri( ).toString( ), new ImageDownCallBack( ) {

                        @Override
                        public void downCompleted(Bitmap bitmap) {
                            if (bitmap == null)
                                return;
                            MyLog.e( TAG, "=========O(∩_∩)O=============" );
                            BitmapDrawable bd = new BitmapDrawable( MainActivity.this.getResources( ), bitmap );
                            getSupportActionBar( ).setIcon( bd );
                        }
                    } );
                    MyLog.e( TAG, "===============================" );
                }

                setTitle( mDeviceItem.getDevice( ).getDetails( ).getFriendlyName( ) );
            }
        }
        return super.onOptionsItemSelected( item );
    }

    private boolean back = false;

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {

        if (event.getAction( ) == KeyEvent.ACTION_DOWN && event.getKeyCode( ) == KeyEvent.KEYCODE_BACK) {
            if (back) {
                finish( );
            } else {
                back = true;
                Toast.makeText( this, "再返回一次推出", Toast.LENGTH_LONG ).show( );

            }
            return true;
        }
        back = false;
        do {
            if (event.getAction( ) != KeyEvent.ACTION_DOWN) {
                break;
            }
            switch (event.getKeyCode( )) {
            case KeyEvent.KEYCODE_MENU:
                MyLog.e( TAG, "KeyEvent.KEYCODE_MENUKeyEvent.KEYCODE_MENUKeyEvent.KEYCODE_MENU=KEYCODE_MENU" );
                getSupportActionBar( ).show( );
                break;
            default:
                break;
            }
        } while (false);
        return super.dispatchKeyEvent( event );
    }
}
