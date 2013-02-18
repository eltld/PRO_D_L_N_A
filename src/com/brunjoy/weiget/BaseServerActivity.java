package com.brunjoy.weiget;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.teleal.cling.android.AndroidUpnpService;
import org.teleal.cling.model.action.ActionException;
import org.teleal.cling.model.action.ActionInvocation;
import org.teleal.cling.model.message.UpnpResponse;
import org.teleal.cling.model.meta.Device;
import org.teleal.cling.model.meta.LocalDevice;
import org.teleal.cling.model.meta.RemoteDevice;
import org.teleal.cling.model.meta.Service;
import org.teleal.cling.model.types.ErrorCode;
import org.teleal.cling.registry.DefaultRegistryListener;
import org.teleal.cling.registry.Registry;
import org.teleal.cling.support.contentdirectory.callback.Browse;
import org.teleal.cling.support.model.BrowseFlag;
import org.teleal.cling.support.model.DIDLContent;
import org.teleal.cling.support.model.DIDLObject;
import org.teleal.cling.support.model.PersonWithRole;
import org.teleal.cling.support.model.Res;
import org.teleal.cling.support.model.SortCriterion;
import org.teleal.cling.support.model.WriteStatus;
import org.teleal.cling.support.model.container.Container;
import org.teleal.cling.support.model.item.ImageItem;
import org.teleal.cling.support.model.item.Item;
import org.teleal.cling.support.model.item.MusicTrack;
import org.teleal.cling.support.model.item.VideoItem;
import org.teleal.common.util.MimeType;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.provider.MediaStore.Video.Thumbnails;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;
import com.brunjoy.duanluo.imgdowner.BitmapUtil;
import com.brunjoy.duanluo.imgdowner.MyLog;
import com.brunjoy.duanluo.video.VideoViewActivity;
import com.brunjoy.manager.ServiceManager;
import com.brunjoy.recommend.RecommendAPPActivity;
import com.brunjoy.video.R;
import com.brunjoy.video.activity.ContentItem;
import com.brunjoy.video.activity.DeviceItem;
import com.brunjoy.video.activity.WireUpnpService;
import com.cattsoft.multimediaviewer.AudioPlayer;
import com.cattsoft.multimediaviewer.ImagePlayer;
import com.wireme.mediaserver.ContentNode;
import com.wireme.mediaserver.ContentTree;
import com.wireme.mediaserver.MediaServer;
import com.wireme.util.MyDataManager;
import com.wireme.util.MyDataManager.MyCallBacke;

public class BaseServerActivity extends SherlockFragmentActivity implements OnClickListener {

    // private MyArrayAdapter<DeviceItem> deviceListAdapter;
    // private MyArrayAdapter<ContentItem> contentListAdapter;
//    public static ImageDownloader imageDownloader = new ImageDownloader( );
    private AndroidUpnpService upnpService;
    private MediaServer mediaServer;
    private DeviceListRegistryListener deviceListRegistryListener;

    private static boolean serverPrepared = false;
    private final static String LOGTAG = "WireMe";
    private ServiceConnection serviceConnection = new ServiceConnection( ) {
        public void onServiceConnected(ComponentName className, IBinder service) {
            upnpService = (AndroidUpnpService) service;
            Log.v( LOGTAG, "Connected to UPnP Service" );
            runOnUiThread( new Runnable( ) {
                @Override
                public void run() {
                    if (mediaServer == null) {
                        // try {
                        mediaServer = ServiceManager.getInstance( BaseServerActivity.this ).getMediaServer( );
                        // new MediaServer( MainActivitySrcN.this );
                        upnpService.getRegistry( ).addDevice( mediaServer.getDevice( ) );
                        new AsyncTask<Void, Void, Void>( ) {
                            @Override
                            protected Void doInBackground(Void... params) {
                                prepareMediaServer( );
                                return null;
                            }

                            protected void onPostExecute(Void result) {
                                // deviceListAdapter.clear( );
                                upnpService.getRegistry( ).addListener( deviceListRegistryListener );
                                for (Device<?, ?, ?> device : upnpService.getRegistry( ).getDevices( )) {
                                    deviceListRegistryListener.deviceAdded( new DeviceItem( device ) );
                                }
                                upnpService.getControlPoint( ).search( 5 );
                            }
                        }.execute( );

                    }
                }
            } );

        }

        public void onServiceDisconnected(ComponentName className) {
            upnpService = null;
        }
    };

    // boolean exit = false;

    // @Override
    // public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
    // switch (keyCode) {
    // case KeyEvent.KEYCODE_BACK:
    // if (getCurrentItemId( ) == 1) {
    // if (!back( )) {
    // setCurrentItem( 0 );
    // }
    // return true;
    // } else if (getCurrentItemId( ) != 0) {
    // setCurrentItem( 0 );
    // return true;
    // } else {
    // if (exit) {
    // finish( );
    // break;
    // }
    // Toast.makeText( this, "在点击一次   即可退出", Toast.LENGTH_SHORT ).show( );
    // exit = true;
    // return true;
    // }
    // case KeyEvent.KEYCODE_MENU:
    // // this.mMyOnLineDataAdapter.startDown( );
    // // BitmapUtil.clearLoacalCache( );
    // default:
    // exit = false;
    // break;
    // }
    // return super.onKeyDown( keyCode, event );
    //
    // }

    // private boolean back() {
    // int size = parents.size( );
    // MyLog.d( "hai", "size=============" + size );
    // if (size == 0) {
    // return false;
    // }
    // parents.remove( size - 1 );
    // size = parents.size( );
    // MyLog.d( "hai", "size=============" + size );
    // if (size > 0) {
    // selectContent( parents.get( size - 1 ), -1 );
    // } else if (size == 0) {
    // if (currentDeviceIndex < 0) {
    // Toast.makeText( this, "对方设备可能掉线了，请重新选择设备", Toast.LENGTH_SHORT ).show( );
    // return false;
    // }
    // // Device<?, ?, ?> device = deviceListAdapter.getItem( currentDeviceIndex ).getDevice( );
    // // Service<?, ?> service = device.findService( new UDAServiceType( "ContentDirectory" ) );
    // // upnpService.getControlPoint( ).execute( new ContentBrowseActionCallback( service, createRootContainer( service ) ) );
    // }
    // return true;
    // // }
    //
    // public void onClick(View v) {
    // switch (v.getId( )) {
    // case R.id.btn_topMenuLeft:
    // back( );
    // break;
    // default:
    // break;
    // }
    //
    // };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );

        //
        // YouKukuAnaly youKu=new YouKukuAnaly( );
        // youKu.getDefault( );

        deviceListRegistryListener = new DeviceListRegistryListener( );
        // mMyOnLineDataAdapter = new MyOnLineDataAdapter( this );
        // deviceListAdapter = new MyArrayAdapter<DeviceItem>( this ) {
        // @Override
        // public View getView(int position, View view, ViewGroup parent) {
        // if (view == null) {
        // view = getLayoutInflater( ).inflate( R.layout.deviceitem, null );
        // }
        // DeviceItem item = getItem( position );
        // ImageView image = (ImageView) view.findViewById( R.id.itemDevice_thunm );
        // TextView name = (TextView) view.findViewById( R.id.itemDeviceName );
        //
        // image.setImageResource( R.drawable.sym_def_app_icon );
        // if (item.getHostUrl( ) != null) {
        // imageDownloader.download( item.getHostUrl( ) + item.getIconStr( ), image );
        // name.setText( item.toString( ) );
        // } else {
        // name.setText( item.toString( ) + "(" + getString( R.string.localDevice ) + ")" );
        // }
        //
        // return view;
        // }
        // };// =//= new ArrayAdapter<DeviceItem>( this, android.R.layout.simple_list_item_1 );
        bindService( new Intent( this, WireUpnpService.class ), serviceConnection, Context.BIND_AUTO_CREATE );
        initListener( );
    }

    // Button btnBack;
    //
    // @Override
    // public View onCreateDefView(int index, LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    // switch (index) {
    //
    // case 0:
    // View view = getLayoutInflater( ).inflate( R.layout.baseitem, null );
    // ListView listView = (ListView) view.findViewById( R.id.listViewContent );
    // titleDevice = (TextView) view.findViewById( R.id.tvBaseTitle );
    // view.findViewById( R.id.progressBarTitileRight ).setVisibility( View.GONE );
    // listView.setCacheColorHint( Color.TRANSPARENT );
    // listView.setAdapter( deviceListAdapter );
    // listView.setOnItemClickListener( deviceItemClickListener );
    // return view;
    // case 1:
    // return onCreatDeviceLayout( );
    // case 2:
    //
    // return getLayoutInflater( ).inflate( R.layout.online_baselayout, null );
    //
    // default:
    // break;
    // }
    // return null;
    // }

    // private MyOnLineDataAdapter mMyOnLineDataAdapter;
    // private TextView titleContent, titleDevice;
    // private ProgressBar mProgressBarContent;

    // private View onCreatDeviceLayout() {
    // View view = getLayoutInflater( ).inflate( R.layout.baseitem, null );
    // btnBack = (Button) view.findViewById( R.id.btn_topMenuLeft );
    // btnBack.setOnClickListener( this );
    // ListView listView = (ListView) view.findViewById( R.id.listViewContent );
    // titleContent = (TextView) view.findViewById( R.id.tvBaseTitle );
    // mProgressBarContent = (ProgressBar) view.findViewById( R.id.progressBarTitileRight );
    //
    // listView.setCacheColorHint( Color.TRANSPARENT );
    // contentListAdapter = MyDataListManager.getInstance( this ).getMyArrayAdapter( );
    // listView.setAdapter( contentListAdapter );
    // listView.setOnItemClickListener( contentItemClickListener );
    // return view;
    // }

    @Override
    protected void onDestroy() {
        if (upnpService != null) {
            // 在这里不要shutdown 因为他要发送 退出 广播，等待一段时间例如1s后shutdown 并清理内存
            upnpService.getRegistry( ).removeListener( deviceListRegistryListener );
            upnpService.getRegistry( ).removeAllLocalDevices( );
            upnpService.getRegistry( ).removeAllRemoteDevices( );
        }
        // contentListAdapter.clear( );
        // contentListAdapter = null;
        // this.deviceListAdapter = null;
        unbindService( serviceConnection );
        if (mBroadcastReceiver != null) {
            unregisterReceiver( mBroadcastReceiver );
            mBroadcastReceiver = null;
        }
        BitmapUtil.clearLoacalCache( );// 清楚缓存文件
        new Thread( new Runnable( ) {
            @Override
            public void run() {
                try {
                    Thread.sleep( 1000 );
                    if (upnpService != null) {
                        upnpService.getRegistry( ).shutdown( );
                        upnpService.getConfiguration( ).shutdown( );
                        upnpService.get( ).shutdown( );
                        // upnpService = null;
                    }
                    if (mediaServer != null) {

                        ServiceManager.getInstance( BaseServerActivity.this ).onDestory( );
                        mediaServer = null;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace( );
                }

            }
        } ).start( );
        super.onDestroy( );
    }

    // @Override
    // public boolean onCreateOptionsMenu(Menu menu) {
    // menu.add( 0, 0, 0, R.string.search_lan ).setIcon( android.R.drawable.ic_menu_search );
    // menu.add( 0, 1, 0, R.string.toggle_debug_logging ).setIcon( android.R.drawable.ic_menu_info_details );
    // menu.add( 0, 2, 0, R.string.recommendApp ).setIcon( android.R.drawable.ic_menu_myplaces ).setIntent( new Intent( this, RecommendAPPActivity.class ) );
    // return true;
    // }

    // @Override
    // public boolean onOptionsItemSelected(MenuItem item) {
    // switch (item.getItemId( )) {
    // case 0:
    // searchNetwork( );
    // break;
    // case 1:
    // Logger logger = Logger.getLogger( "org.teleal.cling" );
    // if (logger.getLevel( ).equals( Level.FINEST )) {
    // Toast.makeText( this, R.string.disabling_debug_logging, Toast.LENGTH_SHORT ).show( );
    // logger.setLevel( Level.INFO );
    // } else {
    // Toast.makeText( this, R.string.enabling_debug_logging, Toast.LENGTH_SHORT ).show( );
    // logger.setLevel( Level.FINEST );
    // }
    // break;
    //
    // }
    // return false;
    // }
    @Override
    public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
        SubMenu sub = menu.addSubMenu( 0, 0, 0, "menu" );
        // sub.add(0, R.style.Theme_Sherlock, 0, "Default");
        // sub.add(0, R.style.Theme_Sherlock_Light, 0, "Light");
        // sub.add(0, R.style.Theme_Sherlock_Light_DarkActionBar, 0, "Light (Dark Action Bar)");
        sub.add( 1, 1, 0, R.string.search_lan ).setIcon( android.R.drawable.ic_menu_search );
        sub.add( 1, 2, 0, R.string.toggle_debug_logging ).setIcon( android.R.drawable.ic_menu_info_details );
        sub.add( 1, 3, 0, R.string.recommendApp ).setIcon( android.R.drawable.ic_menu_myplaces ).setIntent( new Intent( this, RecommendAPPActivity.class ) );
        sub.getItem( ).setShowAsAction( MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT );
        return super.onCreateOptionsMenu( menu );
    }

    @Override
    public boolean onMenuItemSelected(int featureId, com.actionbarsherlock.view.MenuItem item) {
        switch (item.getItemId( )) {
        case 1:
            searchNetwork( );
            break;
        case 2:
            Logger logger = Logger.getLogger( "org.teleal.cling" );
            if (logger.getLevel( ).equals( Level.FINEST )) {
                Toast.makeText( this, R.string.disabling_debug_logging, Toast.LENGTH_SHORT ).show( );
                logger.setLevel( Level.INFO );
            } else {
                Toast.makeText( this, R.string.enabling_debug_logging, Toast.LENGTH_SHORT ).show( );
                logger.setLevel( Level.FINEST );
            }
            break;

        }
        return super.onMenuItemSelected( featureId, item );
    }

    /**
     * 搜索网络
     */
    protected void searchNetwork() {
        if (upnpService == null)
            return;

        Toast.makeText( this, R.string.searching_lan, Toast.LENGTH_SHORT ).show( );
        upnpService.getRegistry( ).removeAllRemoteDevices( );
        upnpService.getControlPoint( ).search( 5 );
    }

    /**
     * @param service
     * @return
     */
    protected Container createRootContainer(Service<?, ?> service) {
        Container rootContainer = new Container( );
        rootContainer.setId( "0" );
        rootContainer.setTitle( service.getDevice( ) != null ? "Content Directory on " + service.getDevice( ).getDisplayString( ) : "Unknow device name" );
        return rootContainer;
    }

    private int currentDeviceIndex = 0;

    // void onSelectedDevice(int position) {
    // if (deviceListAdapter.getCount( ) <= 0) {
    // Toast.makeText( this, R.string.no_find_device, Toast.LENGTH_LONG ).show( );
    // return;
    // }
    // currentDeviceIndex = position;
    // titleDevice.setText( "当前选择设备：" + deviceListAdapter.getItem( position ).toString( ) );
    // setTitle( "当前选择设备：" + deviceListAdapter.getItem( position ).toString( ) );
    // Device<?, ?, ?> device = deviceListAdapter.getItem( position ).getDevice( );
    //
    // Service<?, ?> service = device.findService( new UDAServiceType( "ContentDirectory" ) );
    // upnpService.getControlPoint( ).execute( new ContentBrowseActionCallback( service, createRootContainer( service ) ) );
    // contentListAdapter.clear( );
    // setCurrentItem( 1 );
    // }

    // private OnItemClickListener deviceItemClickListener = new OnItemClickListener( ) {
    // @Override
    // public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
    // parents.clear( );
    // onSelectedDevice( position );
    //
    // }
    // };
    // private static ArrayList<ContentItem> parents = new ArrayList<ContentItem>( );
    // private OnItemClickListener contentItemClickListener = new OnItemClickListener( ) {
    //
    // @Override
    // public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
    // ContentItem content = contentListAdapter.getItem( position );
    // if (content.isContainer( )) {
    // parents.add( content );
    // }
    // selectContent( content, position );
    // }
    // };

    public boolean checkIsInstallApkByName(String pk) {

        try {
            PackageInfo mPackageInfo = getPackageManager( ).getPackageInfo( pk, 0 );

            return mPackageInfo == null ? false : true;
        } catch (NameNotFoundException e) {

            e.printStackTrace( );

        }
        return false;
    }

    public void selectContent(final ContentItem content, int positon) {
        // titleContent.setText( "../" + content == null || content.getContainer( ) == null ? "" : content.getContainer( ).getTitle( ) );
        if (content.isContainer( )) {
            // mProgressBarContent.setVisibility( View.VISIBLE );
            upnpService.getControlPoint( ).execute( new ContentBrowseActionCallback( content.getService( ), content.getContainer( ) ) );
        } else {
            Res res = content.getItem( ).getFirstResource( );
            final String type = res.getProtocolInfo( ).getContentFormat( );
            MyDataListManager.getInstance( this ).setPosition( positon );
            if (type != null && type.toLowerCase( ).startsWith( "video/" )) {
                if (true) {
                    Intent intent = new Intent( );
                    intent.setDataAndType( Uri.parse( content.getItem( ).getFirstResource( ).getValue( ) ), type );
                    intent.setClass( this, VideoViewActivity.class );
                    startActivity( intent );
                    return;
                }
                if (checkIsInstallApkByName( "com.brunjoy" )) {
                    try {
                        Intent intent = new Intent( );
                        intent.setDataAndType( Uri.parse( content.getItem( ).getFirstResource( ).getValue( ) ), type );
                        intent.setComponent( new ComponentName( "com.brunjoy", "com.brunjoy.duanluo.video.VideoViewActivity" ) );
                        startActivity( intent );
                    } catch (Exception e) {
                        startVideoActivity( content.getItem( ).getFirstResource( ).getValue( ), type );
                    }

                } else {
                    startVideoActivity( content.getItem( ).getFirstResource( ).getValue( ), type );
                }

            } else if (type != null && type.toLowerCase( ).startsWith( "image/" )) {
                Intent intent = new Intent( this, ImagePlayer.class );
                intent.putExtra( "name", content.getItem( ).getTitle( ) );
                intent.putExtra( "path", content.getItem( ).getFirstResource( ).getValue( ) );
                startActivity( intent );
            } else if (type != null && type.toLowerCase( ).startsWith( "audio/" )) {
                Intent intent = new Intent( this, AudioPlayer.class );
                if (content.getItem( ) instanceof MusicTrack) {
                    MusicTrack item = (MusicTrack) content.getItem( );
                    intent.putExtra( "alumbPhoto", item.getAlbumArtURI( ) == null ? "" : item.getAlbumArtURI( ).toString( ) );
                    intent.putExtra( "name", item.getTitle( ) );
                    intent.putExtra( "artist", item.getCreator( ) );
                }

                intent.putExtra( "path", content.getItem( ).getFirstResource( ).getValue( ) );
                startActivity( intent );
            } else {
                Intent intent = new Intent( Intent.ACTION_VIEW );
                intent.setDataAndType( Uri.parse( content.getItem( ).getFirstResource( ).getValue( ) ), type );

                try {
                    startActivity( intent );
                } catch (Exception e) {
                    Toast.makeText( BaseServerActivity.this, "没有找到打开程序", Toast.LENGTH_SHORT ).show( );
                }
            }

        }
    }

    public void startVideoActivity(final String path, final String type) {
        new AlertDialog.Builder( this ).setTitle( R.string.question_1 ).setMessage( R.string.content_1 ).setPositiveButton( R.string.ok, new DialogInterface.OnClickListener( ) {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                showDownProgressBar( BaseServerActivity.this, "http://duanluo.b0.upaiyun.com/apk/DuanLuo_brunjoy_1_21.apk" );

            }

        } ).setNeutralButton( R.string.other, new DialogInterface.OnClickListener( ) {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent( Intent.ACTION_VIEW );
                intent.setDataAndType( Uri.parse( path ), type );
                try {
                    startActivity( intent );
                } catch (Exception e1) {
                    Toast.makeText( BaseServerActivity.this, "没有找到打开程序", Toast.LENGTH_SHORT ).show( );
                }

            }
        } ).setNegativeButton( R.string.cancle, null ).create( ).show( );
    }

    public class DeviceListRegistryListener extends DefaultRegistryListener {

        @Override
        public void remoteDeviceDiscoveryStarted(Registry registry, RemoteDevice device) {
            Log.e( LOGTAG, "remoteDeviceDiscoveryStarted  RemoteDevice=" + device );
        }

        @Override
        public void remoteDeviceDiscoveryFailed(Registry registry, final RemoteDevice device, final Exception ex) {
            Log.e( LOGTAG, "remoteDeviceDiscoveryStarted  RemoteDevice=" + device );
            Log.e( LOGTAG, "remoteDeviceDiscoveryStarted  Exception=" + ex.getMessage( ) );
        }

        @Override
        public void remoteDeviceAdded(Registry registry, RemoteDevice device) {
            Log.e( LOGTAG, "remoteDeviceAdded  RemoteDevice=" + device.getIdentity( ).getDescriptorURL( ) );
            String address = getRemoteAddressByDesUrl( device.getIdentity( ).getDescriptorURL( ) );
            Log.e( LOGTAG, "remoteDeviceAdded  address=" + address );
            device.getIdentity( ).getDescriptorURL( );
            if (device.getType( ).getNamespace( ).equals( "schemas-upnp-org" ) && device.getType( ).getType( ).equals( "MediaServer" )) {
                final DeviceItem display = new DeviceItem( device, device.getDetails( ).getFriendlyName( ), device.getDisplayString( ), "(REMOTE) "
                        + device.getType( ).getDisplayString( ) );
                display.setHostUrl( address );
                deviceAdded( display );

            }
        }

        @Override
        public void remoteDeviceRemoved(Registry registry, RemoteDevice device) {
            final DeviceItem display = new DeviceItem( device, device.getDisplayString( ) );
            Log.e( LOGTAG, "remoteDeviceAdded  remoteDeviceRemoved=" + device );
            deviceRemoved( display );
        }

        @Override
        public void localDeviceAdded(Registry registry, LocalDevice device) {
            final DeviceItem display = new DeviceItem( device, device.getDetails( ).getFriendlyName( ), device.getDisplayString( ), "(REMOTE) "
                    + device.getType( ).getDisplayString( ) );
            Log.e( LOGTAG, "localDeviceAdded  RemoteDevice=" + device.getIdentity( ).getUdn( ) );
            display.setHostUrl( null );
            deviceAdded( display );
        }

        @Override
        public void localDeviceRemoved(Registry registry, LocalDevice device) {
            final DeviceItem display = new DeviceItem( device, device.getDisplayString( ) );
            deviceRemoved( display );
        }

        public void deviceAdded(final DeviceItem di) {
            runOnUiThread( new Runnable( ) {
                public void run() {
                    // for (int i = deviceListAdapter.getCount( ) - 1; i >= 0; i--) {
                    // DeviceItem item = deviceListAdapter.getItem( i );
                    // if (item.getDevice( ).getDetails( ).getSerialNumber( ).equalsIgnoreCase( di.getDevice( ).getDetails( ).getSerialNumber( ) )) {
                    // deviceListAdapter.remove( item );
                    // deviceListAdapter.insert( di, i );
                    MyLog.d( LOGTAG, "本地设备  Added  "+di );
                    // return;
                    // }
                    // }
                    // deviceListAdapter.add( di );

                }
            } );
        }

        public void deviceRemoved(final DeviceItem di) {
            runOnUiThread( new Runnable( ) {
                public void run() {
                    MyLog.d( LOGTAG, "本地设备 Removed "+di );
                    // int position = deviceListAdapter.getPosition( di );
                    // if (position < currentDeviceIndex) {
                    // if (position > 0)
                    // currentDeviceIndex -= 1;
                    // else {
                    // currentDeviceIndex = -1;
                    // }
                    // } else if (position == currentDeviceIndex) {
                    // currentDeviceIndex = -1;
                    // }
                    // deviceListAdapter.remove( di );
                }
            } );
        }
    }

    class ContentBrowseActionCallback extends Browse {

        private Service<?, ?> service;
        private Container container;

        // private ArrayAdapter<ContentItem> listAdapter;

        public ContentBrowseActionCallback(Service<?, ?> service, Container container/* , ArrayAdapter<ContentItem> listadapter */) {
            super( service, container.getId( ), BrowseFlag.DIRECT_CHILDREN, "*", 0, 0, new SortCriterion( true, "dc:title" ) );
            this.service = service;
            this.container = container;
            // this.listAdapter = listadapter;
        }

        public void received(final ActionInvocation actionInvocation, final DIDLContent didl) {
            runOnUiThread( new Runnable( ) {
                public void run() {
                    try {
                        // contentListAdapter.clear( );
                        // Containers first
                        if (didl.getContainers( ).size( ) > 0)
                            for (Container childContainer : didl.getContainers( )) {
                                Log.e( "ContentBrowseActionCallback", "add child container " + childContainer.getTitle( ) );
                                // for(childContainer.get)
                                // contentListAdapter.add( new ContentItem( childContainer, service ) );
                            }

                        // Now items
                        if (didl.getItems( ).size( ) > 0)
                            for (Item childItem : didl.getItems( )) {
                                Log.e( "ContentBrowseActionCallback", "add child item" + childItem.getTitle( ) );
                                if (childItem instanceof VideoItem) {
                                    Log.e( "ContentBrowseActionCallback", "-----------=========== path item" + ((VideoItem) childItem).getAlbumArtURI( ) );
                                }
                                Log.e( "ContentBrowseActionCallback", childItem.toString( ) );
                                // contentListAdapter.add( new ContentItem( childItem, service ) );
                            }

                    } catch (Exception ex) {
                        Log.e( "ContentBrowseActionCallback", "Creating DIDL tree nodes failed: " + ex );
                        actionInvocation.setFailure( new ActionException( ErrorCode.ACTION_FAILED, "Can't create list childs: " + ex, ex ) );
                        failure( actionInvocation, null );
                    }
                    // mProgressBarContent.setVisibility( View.GONE );
                    // String mParentID = didl.getFirstContainer( ) == null ? null : didl.getFirstContainer( ).getParentID( );

                    // MyLog.d( "ContentBrowseActionCallback", "mParentID" + mParentID );
                    // if (parents.size( ) < 1) {
                    // btnBack.setVisibility( View.GONE );
                    // titleContent.setText( "已经是顶级了" );
                    // } else {
                    // btnBack.setVisibility( View.VISIBLE );
                    //
                    // }
                    // mProgressBarDevice.setVisibility( View.GONE );
                }
            } );
        }

        public void updateStatus(final Status status) {
        }

        @Override
        public void failure(ActionInvocation invocation, UpnpResponse operation, final String defaultMsg) {
        }
    }

    private void prepareMediaServer() {
        if (serverPrepared)
            return;
        ContentNode rootNode = ContentTree.getRootNode( );
        Cursor cursor;
        Container videoContainer = new Container( );
        videoContainer.setClazz( new DIDLObject.Class( "object.container" ) );
        videoContainer.setId( ContentTree.VIDEO_ID );
        videoContainer.setParentID( ContentTree.ROOT_ID );
        videoContainer.setTitle( getString( R.string.video ) );
        videoContainer.setRestricted( true );
        videoContainer.setSearchable( true );
        videoContainer.setWriteStatus( WriteStatus.NOT_WRITABLE );
        videoContainer.setChildCount( 0 );
        rootNode.getContainer( ).addContainer( videoContainer );
        rootNode.getContainer( ).setChildCount( rootNode.getContainer( ).getChildCount( ) + 1 );
        ContentTree.addNode( ContentTree.VIDEO_ID, new ContentNode( ContentTree.VIDEO_ID, videoContainer ) );
        String[] videoColumns = { MediaStore.Video.Media._ID, MediaStore.Video.Media.TITLE, MediaStore.Video.Media.DATA, MediaStore.Video.Media.ARTIST,
                MediaStore.Video.Media.MIME_TYPE, MediaStore.Video.Media.SIZE, MediaStore.Video.Media.DURATION, MediaStore.Video.Media.RESOLUTION };
        cursor = managedQuery( MediaStore.Video.Media.EXTERNAL_CONTENT_URI, videoColumns, null, null, null );
        if (cursor != null && cursor.moveToFirst( )) {
            do {
                int _id = cursor.getInt( cursor.getColumnIndex( MediaStore.Video.Media._ID ) );
                String title = cursor.getString( cursor.getColumnIndexOrThrow( MediaStore.Video.Media.TITLE ) );
                String creator = cursor.getString( cursor.getColumnIndexOrThrow( MediaStore.Video.Media.ARTIST ) );
                String filePath = cursor.getString( cursor.getColumnIndexOrThrow( MediaStore.Video.Media.DATA ) );
                String mimeType = cursor.getString( cursor.getColumnIndexOrThrow( MediaStore.Video.Media.MIME_TYPE ) );
                long size = cursor.getLong( cursor.getColumnIndexOrThrow( MediaStore.Video.Media.SIZE ) );
                long duration = cursor.getLong( cursor.getColumnIndexOrThrow( MediaStore.Video.Media.DURATION ) );
                String id = ContentTree.VIDEO_PREFIX + _id;
                int lastIndex = filePath.lastIndexOf( '.' );
                id += lastIndex > 0 ? filePath.substring( lastIndex ) : "";
                String resolution = cursor.getString( cursor.getColumnIndexOrThrow( MediaStore.Video.Media.RESOLUTION ) );
                Res res = new Res( new MimeType( mimeType.substring( 0, mimeType.indexOf( '/' ) ), mimeType.substring( mimeType.indexOf( '/' ) + 1 ) ), size, "/" + id );
                res.setDuration( duration / (1000 * 60 * 60) + ":" + (duration % (1000 * 60 * 60)) / (1000 * 60) + ":" + (duration % (1000 * 60)) / 1000 );
                res.setResolution( resolution );
                String albumUrl = getVideoColumnData( filePath, title );
                Log.e( LOGTAG, "albumUrl=" + albumUrl );
                VideoItem videoItem = new VideoItem( id, ContentTree.VIDEO_ID, title, creator, res );
                if (albumUrl != null)
                    videoItem.setAlbumArtURI( albumUrl );
                videoContainer.addItem( videoItem );

                videoContainer.setChildCount( videoContainer.getChildCount( ) + 1 );
                ContentTree.addNode( id, new ContentNode( id, videoItem, filePath ) );

                Log.v( LOGTAG, "added video item title=" + title + "from " + filePath );
            } while (cursor.moveToNext( ));
        }
        // Audio Container
        Container audioContainer = new Container( ContentTree.AUDIO_ID, ContentTree.ROOT_ID, getString( R.string.audio ), "GNaP MediaServer", new DIDLObject.Class(
                "object.container" ), 0 );
        audioContainer.setRestricted( true );
        audioContainer.setSearchable( true );
        audioContainer.setWriteStatus( WriteStatus.NOT_WRITABLE );
        rootNode.getContainer( ).addContainer( audioContainer );
        rootNode.getContainer( ).setChildCount( rootNode.getContainer( ).getChildCount( ) + 1 );
        ContentTree.addNode( ContentTree.AUDIO_ID, new ContentNode( ContentTree.AUDIO_ID, audioContainer ) );

        String[] audioColumns = { MediaStore.Audio.Media._ID, MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.MIME_TYPE, MediaStore.Audio.Media.SIZE, MediaStore.Audio.Media.DURATION, MediaStore.Audio.Media.ALBUM };
        cursor = managedQuery( MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, audioColumns, null, null, null );
        if (cursor != null && cursor.moveToFirst( )) {
            do {
                long _id = cursor.getLong( cursor.getColumnIndex( MediaStore.Audio.Media._ID ) );
                String id = ContentTree.AUDIO_PREFIX + _id;
                String title = cursor.getString( cursor.getColumnIndexOrThrow( MediaStore.Audio.Media.TITLE ) );
                String creator = cursor.getString( cursor.getColumnIndexOrThrow( MediaStore.Audio.Media.ARTIST ) );
                String filePath = cursor.getString( cursor.getColumnIndexOrThrow( MediaStore.Audio.Media.DATA ) );
                String mimeType = cursor.getString( cursor.getColumnIndexOrThrow( MediaStore.Audio.Media.MIME_TYPE ) );
                long size = cursor.getLong( cursor.getColumnIndexOrThrow( MediaStore.Audio.Media.SIZE ) );
                long duration = cursor.getLong( cursor.getColumnIndexOrThrow( MediaStore.Audio.Media.DURATION ) );
                String album = cursor.getString( cursor.getColumnIndexOrThrow( MediaStore.Audio.Media.ALBUM ) );
                // id += "." + mimeType.substring( mimeType.indexOf( '/' ) + 1 );
                int lastIndex = filePath.lastIndexOf( '.' );
                id += lastIndex > 0 ? filePath.substring( lastIndex ) : "";

                Res res = new Res( new MimeType( mimeType.substring( 0, mimeType.indexOf( '/' ) ), mimeType.substring( mimeType.indexOf( '/' ) + 1 ) ), size, "/" + id );
                res.setDuration( duration / (1000 * 60 * 60) + ":" + (duration % (1000 * 60 * 60)) / (1000 * 60) + ":" + (duration % (1000 * 60)) / 1000 );
                // Music Track must have `artist' with role field, or
                // DIDLParser().generate(didl) will throw nullpointException
                MusicTrack musicTrack = new MusicTrack( id, ContentTree.AUDIO_ID, title, creator, album, new PersonWithRole( creator, "Performer" ), res );
                audioContainer.addItem( musicTrack );
                String path = getAudioPhoto( _id, filePath );
                if (path != null) {
                    MyLog.d( "hai", "getAudioPhoto=" + path );
                    musicTrack.setAlbumArtURI( /* "http://" + mediaServer.getAddress( ) + */path );
                }
                audioContainer.setChildCount( audioContainer.getChildCount( ) + 1 );
                ContentTree.addNode( id, new ContentNode( id, musicTrack, filePath ) );
                Log.v( LOGTAG, "added audio item " + title + "from " + filePath );
            } while (cursor.moveToNext( ));
        }
        // Image Container
        Container imageContainer = new Container( ContentTree.IMAGE_ID, ContentTree.ROOT_ID, getString( R.string.image ), "GNaP MediaServer", new DIDLObject.Class(
                "object.container" ), 0 );
        imageContainer.setRestricted( true );
        imageContainer.setSearchable( true );
        imageContainer.setWriteStatus( WriteStatus.NOT_WRITABLE );
        rootNode.getContainer( ).addContainer( imageContainer );
        rootNode.getContainer( ).setChildCount( rootNode.getContainer( ).getChildCount( ) + 1 );
        ContentTree.addNode( ContentTree.IMAGE_ID, new ContentNode( ContentTree.IMAGE_ID, imageContainer ) );

        String[] imageColumns = { MediaStore.Images.Media._ID, MediaStore.Images.Media.TITLE, MediaStore.Images.Media.DATA, MediaStore.Images.Media.MIME_TYPE,
                MediaStore.Images.Media.SIZE };
        cursor = managedQuery( MediaStore.Images.Media.EXTERNAL_CONTENT_URI, imageColumns, null, null, null );
        int count = 0;
        Log.d( "hai", "imageContain  count=" + cursor.getCount( ) );
        if (cursor != null && cursor.moveToFirst( )) {
            do {
                count++;
                if (count > 999)
                    break;
                long _id = cursor.getLong( cursor.getColumnIndex( MediaStore.Images.Media._ID ) );
                String id = ContentTree.IMAGE_PREFIX + _id;
                String title = cursor.getString( cursor.getColumnIndexOrThrow( MediaStore.Images.Media.TITLE ) );
                String creator = "From " + Build.DEVICE;
                String filePath = cursor.getString( cursor.getColumnIndexOrThrow( MediaStore.Images.Media.DATA ) );
                String mimeType = cursor.getString( cursor.getColumnIndexOrThrow( MediaStore.Images.Media.MIME_TYPE ) );
                long size = cursor.getLong( cursor.getColumnIndexOrThrow( MediaStore.Images.Media.SIZE ) );
                // id += "." + mimeType.substring( mimeType.indexOf( '/' ) + 1 );
                int lastIndex = filePath.lastIndexOf( '.' );
                id += lastIndex > 0 ? filePath.substring( lastIndex ) : "";
                Res res = new Res( new MimeType( mimeType.substring( 0, mimeType.indexOf( '/' ) ), mimeType.substring( mimeType.indexOf( '/' ) + 1 ) ), size, "/" + id );
                ImageItem imageItem = new ImageItem( id, ContentTree.IMAGE_ID, title, creator, res );
                imageContainer.addItem( imageItem );

                // String alumb = getImageThumb( filePath, _id );
                // if (alumb != null) {
                // imageItem.setAlbumArtURI( alumb );
                // }
                // MyLog.d( LOGTAG, " image alumb=" + alumb );

                imageContainer.setChildCount( imageContainer.getChildCount( ) + 1 );
                ContentTree.addNode( id, new ContentNode( id, imageItem, filePath ) );

                Log.v( LOGTAG, "added image item " + title + "from " + filePath + "  mimeType=" + mimeType );
            } while (cursor.moveToNext( ));
        }
        // Video Container

        serverPrepared = true;
    }

    public String getRemoteAddressByDesUrl(URL descriptorURL) {
        if (descriptorURL != null) {
            MyLog.e( "descriptorURL", "descriptorURL=" + descriptorURL );
            return "http://" + descriptorURL.getHost( ) + ":" + descriptorURL.getPort( );
        }
        return null;
    }

    private String getVideoColumnData(String videoPath, String title) {
        String image_path = null;
        if (BitmapUtil.isExistCache( videoPath )) {
            return BitmapUtil.getCachePath( videoPath );
        }
        image_path = BitmapUtil.saveCacheName( ThumbnailUtils.createVideoThumbnail( videoPath, Thumbnails.MICRO_KIND ), title );
        if (image_path == null)
            return null;

        return /* "http://" + mediaServer.getAddress( ) + */image_path;
    }

    private ProgressDialog mProgressDialog;

    private StringBody getMyStringBody(String value) {
        StringBody body = null;
        try {
            body = new StringBody( value + "", Charset.forName( "utf-8" ) );
        } catch (UnsupportedEncodingException e) {

        }
        return body;
    }

    private void downAPK() {
        MultipartEntity params = new MultipartEntity( );
        params.addPart( "extraInfo", getMyStringBody( "package:" + getPackageName( ) ) );

        MyDataManager.getInstance( this ).addTask( "system/getAPK", params, new MyCallBacke( ) {

            @Override
            public void resultMsg(String resultMSG) {
                showDownProgressBar( BaseServerActivity.this, "http://duanluo.b0.upaiyun.com/apk/DuanLuo_brunjoy_1_21.apk" );
            }

            @Override
            public void failureMsg(String msg) {

            }
        } );
    }

    private void showDownProgressBar(final Context mContext, String downUrl) {
        mProgressDialog = new ProgressDialog( mContext );
        mProgressDialog.setTitle( "正在为您下载" );
        mProgressDialog.setProgressStyle( ProgressDialog.STYLE_HORIZONTAL );
        mProgressDialog.setMax( 100 );
        mProgressDialog.setIcon( R.drawable.icon );
        mProgressDialog.show( );

        new MyAsynDownFileTask( new MyAsynDownFileTask.ProgressInterface( ) {

            @Override
            public void success(String msg) {
                File file = new File( msg );
                if (file.exists( )) {
                    Intent intent = new Intent( Intent.ACTION_VIEW );
                    intent.setDataAndType( Uri.parse( "file://" + msg ), "application/vnd.android.package-archive" );
                    mContext.startActivity( intent );
                } else {
                    Toast.makeText( mContext, "文件下载失败，请稍后再试", Toast.LENGTH_LONG ).show( );
                }
                mProgressDialog.dismiss( );
                mProgressDialog = null;
            }

            @Override
            public void progresss(int progresss) {
                mProgressDialog.setProgress( progresss > 100 ? 100 : progresss );
                MyLog.d( "progress", "progresss=" + progresss );
            }

            @Override
            public void failure(String failure) {
                mProgressDialog.dismiss( );
                mProgressDialog = null;
                Toast.makeText( mContext, "文件下载失败，请稍后再试", Toast.LENGTH_LONG ).show( );
            }
        } ).execute( downUrl );
    }

    private final String CONNECTIVITY_CHANGE_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver( ) {

        @Override
        public void onReceive(Context context, Intent intent) {
            MyLog.d( LOGTAG, "网络发生变法" );
            if (mediaServer != null)
                mediaServer.setLocalAddress( getLocalIpAddress( ) );
            searchNetwork( );
        }
    };

    private InetAddress getLocalIpAddress() {
        WifiManager wifiManager = (WifiManager) getSystemService( Context.WIFI_SERVICE );
        WifiInfo wifiInfo = wifiManager.getConnectionInfo( );
        int ipAddress = wifiInfo.getIpAddress( );
        try {
            return InetAddress.getByName( String.format( "%d.%d.%d.%d", (ipAddress & 0xff), (ipAddress >> 8 & 0xff), (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff) ) );
        } catch (UnknownHostException e) {
            e.printStackTrace( );
            return null;
        }
    }

    private void initListener() {
        IntentFilter filter = new IntentFilter( );
        filter.addAction( CONNECTIVITY_CHANGE_ACTION );
        filter.setPriority( 1000 );
        registerReceiver( mBroadcastReceiver, filter );
    }

    private String getAudioPhoto(long songid, String filePath) {
        if (BitmapUtil.isExistCache( filePath )) {
            return BitmapUtil.getCachePath( filePath );
        }

        Bitmap mBitmap = getArtworkFromFile( songid );
        if (mBitmap != null) {
            return BitmapUtil.saveCacheName( mBitmap, filePath );
        }
        return null;

    }

    private String getImageThumb(String filePath, long id) {
        String[] projection = { MediaStore.Images.Thumbnails._ID, MediaStore.Images.Thumbnails.DATA };
        try {
            Cursor cursor = getContentResolver( ).query( MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, projection, MediaStore.Images.Thumbnails._ID + "=" + id, null, null );
            if (cursor != null) {
                cursor.moveToFirst( );
                String path = cursor.getString( cursor.getColumnIndexOrThrow( MediaStore.Images.Thumbnails.DATA ) );
                cursor.close( );
                return "http://" + mediaServer.getAddress( ) + path;
            } else {
                cursor.close( );
            }
        } catch (Exception e) {
            MyLog.d( LOGTAG, "getImageThumb Exception=" + e.getMessage( ) + "\n" + id + "\n" + filePath );
        }
        return null;
    }

    /**
     * 从文件中读取专辑图片,如果不存在则使用默认图片
     * 
     * @param context
     * @param songid
     * @return
     */
    private Bitmap getArtworkFromFile(long songid) {
        Cursor cur = getContentResolver( ).query( MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, new String[] { MediaStore.Audio.Media.ALBUM_ID }, "_id=?",
                new String[] { songid + "" }, null );
        cur.moveToFirst( );
        int albumId = cur.getInt( 0 );
        Bitmap bm = null;
        if (albumId < 0 && songid < 0) {
            throw new IllegalArgumentException( "Must specify an album id or a song id" );
        }
        if (albumId < 0 && songid > 0) {
            // 直接从文件读取专辑图片
            try {

                BitmapFactory.Options options = new BitmapFactory.Options( );

                FileDescriptor fd = null;
                Uri uri = Uri.parse( "content://media/external/audio/media/" + songid + "/albumart" );
                ParcelFileDescriptor pfd = getContentResolver( ).openFileDescriptor( uri, "r" );
                if (pfd != null) {
                    fd = pfd.getFileDescriptor( );
                }
                MyLog.d( "getArtworkFromFile", "-----------uri.getPath( )=" + uri.getPath( ) );
                options.inSampleSize = 1;
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFileDescriptor( fd, null, options );
                options.inSampleSize = 500;
                options.inJustDecodeBounds = false;
                options.inDither = false;
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;

                bm = BitmapFactory.decodeFileDescriptor( fd, null, options );
            } catch (FileNotFoundException ex) {

            }
            if (bm != null) {
                return bm;
            }
        }
        // 从MediaStore数据库中读取专辑图片
        System.out.println( "从MediaStore数据库中读取专辑图片" );
        ContentResolver res = getContentResolver( );
        Uri uri = ContentUris.withAppendedId( Uri.parse( "content://media/external/audio/albumart" ), albumId );

        if (uri != null) {
            InputStream in = null;
            try {
                in = res.openInputStream( uri );
                BitmapFactory.Options options = new BitmapFactory.Options( );
                options.inSampleSize = 1;
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeStream( in, null, options );
                options.inSampleSize = computeSampleSize( options, 180 );
                options.inJustDecodeBounds = false;
                options.inDither = false;
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                MyLog.d( "getArtworkFromFile", "-----------uri.getPath( )=" + uri.getPath( ) );
                in = res.openInputStream( uri );
                return BitmapFactory.decodeStream( in, null, options );
            } catch (FileNotFoundException ex) {
                // bm = getDefaultArtwork( context );
                // return bm;
                return null;

            } finally {
                try {
                    if (in != null) {
                        in.close( );
                    }
                } catch (IOException ex) {
                }
            }
        }
        return bm;
    }

    /**
     * 计算专辑图片大小
     * 
     * @param options
     * @param target
     * @return
     */
    public int computeSampleSize(BitmapFactory.Options options, int target) {
        int w = options.outWidth;
        int h = options.outHeight;
        int candidateW = w / target;
        int candidateH = h / target;
        int candidate = Math.max( candidateW, candidateH );
        if (candidate == 0)
            return 1;
        if (candidate > 1) {
            if ((w > target) && (w / candidate) < target)
                candidate -= 1;
        }
        if (candidate > 1) {
            if ((h > target) && (h / candidate) < target)
                candidate -= 1;
        }
        Log.v( "ADW", "candidate:" + candidate );
        return candidate;
    }

    @Override
    public void onClick(View arg0) {
        // TODO Auto-generated method stub

    }

}