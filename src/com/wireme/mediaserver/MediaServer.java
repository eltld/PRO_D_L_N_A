package com.wireme.mediaserver;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;

import org.teleal.cling.binding.annotations.AnnotationLocalServiceBinder;
import org.teleal.cling.model.DefaultServiceManager;
import org.teleal.cling.model.ValidationException;
import org.teleal.cling.model.meta.DeviceDetails;
import org.teleal.cling.model.meta.DeviceIdentity;
import org.teleal.cling.model.meta.Icon;
import org.teleal.cling.model.meta.LocalDevice;
import org.teleal.cling.model.meta.LocalService;
import org.teleal.cling.model.meta.ManufacturerDetails;
import org.teleal.cling.model.meta.ModelDetails;
import org.teleal.cling.model.types.DeviceType;
import org.teleal.cling.model.types.UDADeviceType;
import org.teleal.cling.model.types.UDN;
import org.teleal.common.io.IO;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.brunjoy.duanluo.imgdowner.BitmapUtil;
import com.brunjoy.duanluo.imgdowner.MyLog;
import com.brunjoy.manager.ServiceManager.STATUS;
import com.brunjoy.video.R;

public class MediaServer {

    private UDN udn = UDN.uniqueSystemIdentifier( "HaizhuAndroidMediaServer" );
    private LocalDevice localDevice;

    private final static String deviceType = "MediaServer";
    private final static int version = 1;
    private final static String LOGTAG = "GNaP-MediaServer";
    private final static int port = 8192;
    private static InetAddress localAddress;
    private HttpServer mHttpServer;

    public MediaServer(Context mContext) throws ValidationException {
        InetAddress localAddress = getLocalIpAddress( mContext );

        DeviceType type = new UDADeviceType( deviceType, version );
        DeviceDetails details = new DeviceDetails( android.os.Build.MODEL + "_Android", new ManufacturerDetails( "Brunjoy.Haizhu", "http://www.duanluo.com" ), new ModelDetails(
                "MediaServer", "MediaServer for Android", "1.01" ), BitmapUtil.getMD5Str( localAddress.toString( ) ), udn.toString( ) );
        LocalService service = new AnnotationLocalServiceBinder( ).read( ContentDirectoryService.class );
        service.setManager( new DefaultServiceManager<ContentDirectoryService>( service, ContentDirectoryService.class ) );
        Icon icon = null;
        String path = null;
        if (!BitmapUtil.isExistCache( "def_device_android_icon" )) {
            // path = BitmapUtil.saveCacheName( BitmapFactory.decodeResource( mContext.getResources( ), R.drawable.sym_def_app_icon ), "def_device_android_icon" );
//            Bitmap mBitmap = BitmapUtil.transform( new Matrix( ), BitmapFactory.decodeResource( mContext.getResources( ), R.drawable.sym_def_app_icon ), 72, 72, true );
            path = BitmapUtil.saveCacheName( BitmapFactory.decodeResource( mContext.getResources( ), R.drawable.sym_def_app_icon ), "def_device_android_icon" );
        } else {
            path = BitmapUtil.getCachePath( "def_device_android_icon" );
        }
        MediaServer.localAddress = localAddress;
        LocalURL = "http://" + getAddress( );
        try {
            icon = new Icon( "image/png", 72, 72, 32, URI.create( path ), IO.readBytes( new File( path ) ) );
//            MyLog.d( "mediaServer", "11111111icon=" + icon.toString( ) );
            localDevice = new LocalDevice( new DeviceIdentity( udn ), type, details, icon, service );
//            MyLog.d( "mediaServer", "22222222icon=" + icon.toString( ) );
        } catch (Exception e) {
            e.printStackTrace( );
            MyLog.e( "mediaServer", "Exception  e=" + e.getMessage( ) );
            localDevice = new LocalDevice( new DeviceIdentity( udn ), type, details, service );
        }
        Log.v( LOGTAG, "MediaServer device created: " );
        Log.v( LOGTAG, "friendly name: " + details.getFriendlyName( ) );
        Log.v( LOGTAG, "manufacturer: " + details.getManufacturerDetails( ).getManufacturer( ) );
        Log.v( LOGTAG, "model: " + details.getModelDetails( ).getModelName( ) );
        // start http server
        try {
            mHttpServer = new HttpServer( port );
        } catch (IOException ioe) {
            System.err.println( "Couldn't start server:\n" + ioe );
            System.exit( -1 );
        }

        Log.v( LOGTAG, "Started Http Server on port " + port );
    }

    // FIXME: now only can get wifi address
    private InetAddress getLocalIpAddress(Context mContext) {
        WifiManager wifiManager = (WifiManager) mContext.getSystemService( Context.WIFI_SERVICE );
        WifiInfo wifiInfo = wifiManager.getConnectionInfo( );
        int ipAddress = wifiInfo.getIpAddress( );
        try {
            return InetAddress.getByName( String.format( "%d.%d.%d.%d", (ipAddress & 0xff), (ipAddress >> 8 & 0xff), (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff) ) );
        } catch (UnknownHostException e) {
            e.printStackTrace( );
            return null;

        }
    }

    public void stop() {
        if (mHttpServer != null) {
            mHttpServer.stop( );
            mHttpServer = null;
        }
       
    }

    public LocalDevice getDevice() {
        return localDevice;
    }

    public static String LocalURL;

    public String getAddress() {
        return localAddress.getHostAddress( ) + ":" + port;
    }

    public void setLocalAddress(InetAddress localIpAddress) {
        MediaServer.localAddress = localIpAddress;
        LocalURL = "http://" + getAddress( );
    }
   
    public void setStaus(STATUS status) {
        if(mHttpServer!=null)
        {
            mHttpServer.setStaus(status);
        }
        
    }
}
