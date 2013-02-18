package com.brunjoy.dlna.UI;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;

import org.teleal.cling.android.AndroidUpnpService;
import org.teleal.cling.model.action.ActionException;
import org.teleal.cling.model.action.ActionInvocation;
import org.teleal.cling.model.message.UpnpResponse;
import org.teleal.cling.model.meta.Device;
import org.teleal.cling.model.meta.LocalDevice;
import org.teleal.cling.model.meta.RemoteDevice;
import org.teleal.cling.model.meta.Service;
import org.teleal.cling.model.types.ErrorCode;
import org.teleal.cling.model.types.UDAServiceType;
import org.teleal.cling.registry.DefaultRegistryListener;
import org.teleal.cling.registry.Registry;
import org.teleal.cling.support.contentdirectory.callback.Browse;
import org.teleal.cling.support.model.BrowseFlag;
import org.teleal.cling.support.model.BrowseResult;
import org.teleal.cling.support.model.DIDLContent;
import org.teleal.cling.support.model.DIDLObject;
import org.teleal.cling.support.model.PersonWithRole;
import org.teleal.cling.support.model.Res;
import org.teleal.cling.support.model.SortCriterion;
import org.teleal.cling.support.model.WriteStatus;
import org.teleal.cling.support.model.container.Container;
import org.teleal.cling.support.model.item.ImageItem;
import org.teleal.cling.support.model.item.MusicTrack;
import org.teleal.cling.support.model.item.VideoItem;
import org.teleal.common.util.MimeType;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.provider.MediaStore.Video.Thumbnails;
import android.util.Log;

import com.brunjoy.duanluo.imgdowner.BitmapUtil;
import com.brunjoy.duanluo.imgdowner.MyLog;
import com.brunjoy.duanluo.video.VideoViewActivity;
import com.brunjoy.manager.ServiceManager;
import com.brunjoy.video.R;
import com.brunjoy.video.activity.DeviceItem;
import com.brunjoy.video.activity.WireUpnpService;
import com.brunjoy.weiget.MyDataListManager;
import com.cattsoft.multimediaviewer.AudioPlayer;
import com.cattsoft.multimediaviewer.ImagePlayer;
import com.wireme.mediaserver.ContentNode;
import com.wireme.mediaserver.ContentTree;
import com.wireme.mediaserver.MediaServer;

public class DLNAServer {
    String LOGTAG = "DLNAServer";
    private AndroidUpnpService upnpService;
    private MediaServer mediaServer;
    private Context mContext;
    private DeviceListRegistryListener deviceListRegistryListener;
    private static DLNAServer mDLNAServer;

    public synchronized static DLNAServer getInstance(Context mContext) {
        if (mDLNAServer == null) {
            mDLNAServer = new DLNAServer( mContext );
            MyLog.e( "first", "create  frament" );
        }
        return mDLNAServer;
    }

    private DLNAServer(Context context) {
      
        this.mContext = context.getApplicationContext( );
        initListener( );
        deviceListRegistryListener = new DeviceListRegistryListener( );
        mContext.bindService( new Intent( mContext, WireUpnpService.class ), serviceConnection, Context.BIND_AUTO_CREATE );
    
    }

    private Handler mHandler = new Handler( ) {
        public void handleMessage(android.os.Message msg) {

        }
    };

    private void runOnUiThread(Runnable mRunnable) {
        mHandler.post( mRunnable );
    }

    private ServiceConnection serviceConnection = new ServiceConnection( ) {
        public void onServiceConnected(ComponentName className, IBinder service) {
            upnpService = (AndroidUpnpService) service;
            Log.v( LOGTAG, "Connected to UPnP Service" );
            runOnUiThread( new Runnable( ) {
                @Override
                public void run() {
                    if (mediaServer == null) {
                        if (mDLNAListener != null) {
                            mDLNAListener.onPreparing( );
                        }
                        // try {
                        mediaServer = ServiceManager.getInstance( mContext ).getMediaServer( );
                        upnpService.getRegistry( ).addDevice( mediaServer.getDevice( ) );
                        new AsyncTask<Void, Void, Void>( ) {

                            @Override
                            protected Void doInBackground(Void... params) {
                                mDLNAListener.onInfomation( "Service 正在初始化本地数据" );
                                prepareMediaServer( );
                                return null;
                            }

                            protected void onPostExecute(Void result) {

                                upnpService.getRegistry( ).addListener( deviceListRegistryListener );
                                for (Device<?, ?, ?> device : upnpService.getRegistry( ).getDevices( )) {
                                    deviceListRegistryListener.deviceAdded( new DeviceItem( device ) );
                                }
                                upnpService.getControlPoint( ).search( 5 );
                                if (mDLNAListener != null) {
                                    mDLNAListener.ready( );
                                }
                                mDLNAListener.onInfomation( "Service  初始化完成" );
                            }
                        }.execute( );

                    } else {
                        mDLNAListener.onInfomation( "ServiceManager 已经创建不许要重复创建了" );
                    }
                }
            } );

        }

        public void onServiceDisconnected(ComponentName className) {
            upnpService = null;
        }
    };

    /**
     * ==========================================<BR>
     * 功能： 搜索网络<BR>
     * 时间：2013-1-25 下午3:31:19 <BR>
     * 参数： <BR>
     * ==========================================
     */
    public void searchNetwork() {
        if (upnpService == null)
            return;
        upnpService.getRegistry( ).removeAllRemoteDevices( );
        upnpService.getControlPoint( ).search( 5 );
    }

    public String getRemoteAddressByDesUrl(URL descriptorURL) {
        if (descriptorURL != null) {
            MyLog.e( "descriptorURL", "descriptorURL=" + descriptorURL );
            return "http://" + descriptorURL.getHost( ) + ":" + descriptorURL.getPort( );
        }
        return null;
    }

    private static boolean serverPrepared = false;

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
                // Log.e( LOGTAG, "albumUrl=" + albumUrl );
                VideoItem videoItem = new VideoItem( id, ContentTree.VIDEO_ID, title, creator, res );
                if (albumUrl != null)
                    videoItem.setAlbumArtURI( albumUrl );
                videoContainer.addItem( videoItem );

                videoContainer.setChildCount( videoContainer.getChildCount( ) + 1 );
                ContentTree.addNode( id, new ContentNode( id, videoItem, filePath ) );

                // Log.v( LOGTAG, "added video item title=" + title + "from " + filePath );
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
                    // MyLog.d( "hai", "getAudioPhoto=" + path );
                    musicTrack.setAlbumArtURI( /* "http://" + mediaServer.getAddress( ) + */path );
                }
                audioContainer.setChildCount( audioContainer.getChildCount( ) + 1 );
                ContentTree.addNode( id, new ContentNode( id, musicTrack, filePath ) );
                // Log.v( LOGTAG, "added audio item " + title + "from " + filePath );
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

                // Log.v( LOGTAG, "added image item " + title + "from " + filePath + "  mimeType=" + mimeType );
            } while (cursor.moveToNext( ));
        }
        // Video Container

        serverPrepared = true;
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

    private Bitmap getArtworkFromFile(long songid) {
        Cursor cur = managedQuery( MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, new String[] { MediaStore.Audio.Media.ALBUM_ID }, "_id=?", new String[] { songid + "" }, null );
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
                ParcelFileDescriptor pfd = mContext.getContentResolver( ).openFileDescriptor( uri, "r" );
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
        ContentResolver res = mContext.getContentResolver( );
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

    private String getString(int resId) {
        return mContext.getString( resId );
    }

    private Cursor managedQuery(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor c = mContext.getContentResolver( ).query( uri, projection, selection, selectionArgs, sortOrder );

        return c;
        // return mContext.getContentResolver( ).query( uri, projection, selection, selectionArgs, sortOrder );
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
            display.setHostUrl( MediaServer.LocalURL );
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
                    synchronized (deviceList) {
                        int index = deviceList.indexOf( di );
                        if (index >= 0 && index < deviceList.size( )) {
                            deviceList.remove( index );
                            deviceList.add( index, di );
                        } else {
                            deviceList.add( di );
                        }
                    }
                    MyLog.d( LOGTAG, "设备  deviceAdded =" + di );
                    if (mDLNAListener != null) {
                        mDLNAListener.deviceUpdate( deviceList, true );
                    }
                }
            } );
        }

        public void deviceRemoved(final DeviceItem di) {
            runOnUiThread( new Runnable( ) {
                public void run() {

                    synchronized (deviceList) {
                        deviceList.remove( di );
                    }
                    if (mDLNAListener != null) {
                        mDLNAListener.deviceUpdate( deviceList, false );
                    }
                    MyLog.d( LOGTAG, "设备  deviceRemoved =" + di );

                }
            } );
        }
    }

    private DeviceItem currentDevice;

    public boolean selectDevice(int position) {
        if (position < 0 || position >= deviceList.size( )) {
            return false;
        }
        qurestParentsIDs.clear( );
        currentDevice = deviceList.get( position );
        // Device<?, ?, ?> device = deviceListAdapter.getItem( position ).getDevice( );
        Service<?, ?> service = getCurrentService( );
        if (service == null) {
            MyLog.e( LOGTAG, "===============selectDevice=service==null================" );
            return false;
        }
        upnpService.getControlPoint( ).execute( new ContentBrowseActionCallback( service ) );
        return true;

    }

    private Service<?, ?> getCurrentService() {
        return currentDevice.getDevice( ).findService( new UDAServiceType( "ContentDirectory" ) );
    }

    private ArrayList<String> qurestParentsIDs = new ArrayList<String>( );

    
    
    /**
     * ==========================================<BR>
     * 功能：浏览文件是否可以返回上一级 <BR>
     * 时间：2013-1-28 下午12:30:27 <BR>
     * 参数：<BR>
     * ==========================================
     */
    public boolean canBack() {
        return qurestParentsIDs.size( ) > 1;
    }

    public void back() {
        if (canBack( )) {
            String id = qurestParentsIDs.remove( qurestParentsIDs.size( ) - 2);
            Service<?, ?> service = getCurrentService( );
            if (service == null) {
                MyLog.e( LOGTAG, "==========back=====selectDevice=service==null================" );
                return;
            }
            upnpService.getControlPoint( ).execute( new ContentBrowseActionCallback( service, id ) );
        }
    }
    
    class ContentBrowseActionCallback extends Browse {

        // private Service<?, ?> service;
        // private Container container;
        // ContentItem mContentItem;

        public ContentBrowseActionCallback(Service<?, ?> service, Container container) {
            super( service, container.getId( ), BrowseFlag.DIRECT_CHILDREN, "*", 0, 0, new SortCriterion( true, "dc:title" ) );
            qurestParentsIDs.add( container.getId( ) );
            MyLog.d( LOGTAG, "container.getId( )=" + container.getId( ) );
            MyLog.d( LOGTAG, "qurestParentsIDs" + qurestParentsIDs );
            // this.container = container;
            // ContentItem mContentItem;
        }

        /**
         * 这个留给返回使用
         * 
         * @param service
         * @param id
         */
        public ContentBrowseActionCallback(Service<?, ?> service, String id) {
            super( service, id, BrowseFlag.DIRECT_CHILDREN, "*", 0, 0, new SortCriterion( true, "dc:title" ) );
            // MyLog.d( LOGTAG, "container.getId( )=" + container.getId( ) );
            // this.container = container;
            // ContentItem mContentItem;
        }

        public ContentBrowseActionCallback(Service<?, ?> service2) {
            this( service2, createRootContainer( service2 ) );
        }

        @Override
        public boolean receivedRaw(ActionInvocation actionInvocation, BrowseResult browseResult) {

            // MyLog.e( "receivedRaw", "actionInvocation=" + actionInvocation );
            // MyLog.e( "receivedRaw", "BrowseResult=" + browseResult );
            return super.receivedRaw( actionInvocation, browseResult );
        }

        public void received(final ActionInvocation actionInvocation, final DIDLContent didl) {
            runOnUiThread( new Runnable( ) {
                public void run() {
                    ArrayList<DIDLObject> containerList = new ArrayList<DIDLObject>( );// 结果
                    try {
                        // Containers first
                        // if (didl.getContainers( ).size( ) > 0)
                        // for (Container childContainer : didl.getContainers( )) {
                        // Log.e( "ContentBrowseActionCallback", "add child container " + childContainer.getTitle( ) );
                        // containerList
                        // }
                        containerList.addAll( didl.getContainers( ) );
                        // Now items
                        // if (didl.getItems( ).size( ) > 0)
                        // for (Item childItem : didl.getItems( )) {
                        // Log.e( "ContentBrowseActionCallback", "add child item" + childItem.getTitle( ) );
                        // if (childItem instanceof VideoItem) {
                        // Log.e( "ContentBrowseActionCallback", "-----------=========== path item" + ((VideoItem) childItem).getAlbumArtURI( ) );
                        // }
                        // Log.e( "ContentBrowseActionCallback", childItem.toString( ) );
                        // }
                        containerList.addAll( didl.getItems( ) );
                        if (mDLNAListener != null) {
                            mDLNAListener.updateContent( containerList );
//                            mDLNAListener.updateBack( qurestParentsIDs );
                        }
                    } catch (Exception ex) {
                        Log.e( "ContentBrowseActionCallback", "Creating DIDL tree nodes failed: " + ex );
                        actionInvocation.setFailure( new ActionException( ErrorCode.ACTION_FAILED, "Can't create list childs: " + ex, ex ) );
                        failure( actionInvocation, null );
                    }
                }
            } );
        }

        public void updateStatus(final Status status) {
            MyLog.e( "updateStatus", "status=" + status );
            // Looper.prepare( );

            // Toast.makeText( mContext, ""+status.getDefaultMessage( ), Toast.LENGTH_LONG ).show();
            // Looper.loop( );
        }

        @Override
        public void failure(ActionInvocation invocation, UpnpResponse operation, final String defaultMsg) {
            if (mDLNAListener != null) {
                mDLNAListener.onInfomation( "检索内容失败！  " + defaultMsg );
            }

        }
    }

    public void openDir(Container mContainer) {
        // if (content.isContainer( )) {
        // mProgressBarContent.setVisibility( View.VISIBLE );
        upnpService.getControlPoint( ).execute( new ContentBrowseActionCallback( getCurrentService( ), mContainer ) );
        // }
    }

    /**
     * ==========================================<BR>
     * 功能： 播放多媒体文件 <BR>
     * 时间：2013-1-25 下午6:04:35 <BR>
     * 参数：<BR>
     * ==========================================
     * 
     * @param content
     * @param positon
     * @param mContext
     *            当前Activity
     */
    public void selectContent(final DIDLObject content, int positon, Context mContext) {

        MyLog.e( LOGTAG, "content.getParentID( )=" + content.getParentID( ) );
        // titleContent.setText( "../" + content == null || content.getContainer( ) == null ? "" : content.getContainer( ).getTitle( ) );
        if (content instanceof Container) {
            Container mContainer = (Container) content;
            // mProgressBarContent.setVisibility( View.VISIBLE );
            upnpService.getControlPoint( ).execute( new ContentBrowseActionCallback( getCurrentService( ), mContainer ) );
        } else {
            Res res = content.getFirstResource( );
            final String type = res.getProtocolInfo( ).getContentFormat( );
            MyDataListManager.getInstance( mContext ).setPosition( positon );
            if (type != null && type.toLowerCase( ).startsWith( "video/" )) {
                // if (true) {
                Intent intent = new Intent( );
                intent.setDataAndType( Uri.parse( content.getFirstResource( ).getValue( ) ), type );
                intent.setClass( mContext, VideoViewActivity.class );
                mContext.startActivity( intent );
                return;
            } else if (type != null && type.toLowerCase( ).startsWith( "image/" )) {
                Intent intent = new Intent( mContext, ImagePlayer.class );
                intent.putExtra( "name", content.getTitle( ) );
                intent.putExtra( "path", content.getFirstResource( ).getValue( ) );
                mContext.startActivity( intent );
            } else if (type != null && type.toLowerCase( ).startsWith( "audio/" )) {
                Intent intent = new Intent( mContext, AudioPlayer.class );
                if (content instanceof MusicTrack) {
                    MusicTrack item = (MusicTrack) content;
                    intent.putExtra( "alumbPhoto", item.getAlbumArtURI( ) == null ? "" : item.getAlbumArtURI( ).toString( ) );
                    intent.putExtra( "name", item.getTitle( ) );
                    intent.putExtra( "artist", item.getCreator( ) );
                }
                intent.putExtra( "path", content.getFirstResource( ).getValue( ) );
                mContext.startActivity( intent );
            } else {
                Intent intent = new Intent( Intent.ACTION_VIEW );
                intent.setDataAndType( Uri.parse( content.getFirstResource( ).getValue( ) ), type );

                try {
                    mContext.startActivity( intent );
                } catch (Exception e) {
                    // Toast.makeText( BaseServerActivity.this, "没有找到打开程序", Toast.LENGTH_SHORT ).show( );
                }
            }

        }
    }

    protected Container createRootContainer(Service<?, ?> service) {
        Container rootContainer = new Container( );
        rootContainer.setId( "0" );
        rootContainer.setTitle( service.getDevice( ) != null ? "Content Directory on " + service.getDevice( ).getDisplayString( ) : "Unknow device name" );
        return rootContainer;
    }

    private ArrayList<DeviceItem> deviceList = new ArrayList<DeviceItem>( );

    /**
     * ==========================================<BR>
     * 功能： 获取设备列表<BR>
     * 时间：2013-1-25 下午3:54:05 <BR>
     * 参数：<BR>
     * ==========================================
     * 
     * @return
     */
    public ArrayList<DeviceItem> getDeviceList() {
        return deviceList;
    }

    private DLNAListener mDLNAListener;

    public void addListener(DLNAListener mDLNAListener) {
        this.mDLNAListener = mDLNAListener;
    }

    private void initListener() {
        IntentFilter filter = new IntentFilter( );
        filter.addAction( CONNECTIVITY_CHANGE_ACTION );
        filter.setPriority( 1000 );
        mContext.registerReceiver( mBroadcastReceiver, filter );
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
        WifiManager wifiManager = (WifiManager) mContext.getSystemService( Context.WIFI_SERVICE );

        try {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo( );
            int ipAddress = wifiInfo.getIpAddress( );
            return InetAddress.getByName( String.format( "%d.%d.%d.%d", (ipAddress & 0xff), (ipAddress >> 8 & 0xff), (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff) ) );
        } catch (UnknownHostException e) {
            e.printStackTrace( );
            return null;
        }
    }

    public void onDestory() {
        if (upnpService != null) {
            // 在这里不要shutdown 因为他要发送 退出 广播，等待一段时间例如1s后shutdown 并清理内存
            upnpService.getRegistry( ).removeListener( deviceListRegistryListener );
            upnpService.getRegistry( ).removeAllLocalDevices( );
            upnpService.getRegistry( ).removeAllRemoteDevices( );
        }
        // contentListAdapter.clear( );
        // contentListAdapter = null;
        // this.deviceListAdapter = null;
        mContext.unbindService( serviceConnection );
        if (mBroadcastReceiver != null) {
            mContext.unregisterReceiver( mBroadcastReceiver );
            mBroadcastReceiver = null;
        }
        mDLNAServer = null;
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

                        ServiceManager.getInstance( mContext ).onDestory( );
                        mediaServer = null;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace( );
                }

            }
        } ).start( );
    }

}
