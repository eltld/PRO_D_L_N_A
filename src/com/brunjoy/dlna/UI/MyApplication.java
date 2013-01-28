package com.brunjoy.dlna.UI;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.teleal.common.logging.LoggingUtil;

import android.app.Application;

import com.wireme.util.FixedAndroidHandler;

public class MyApplication extends Application {
//    private String LOGTAG = "MyApplication";

    @Override
    public void onCreate() {
        super.onCreate( );
        LoggingUtil.resetRootHandler( new FixedAndroidHandler( ) );
        Logger.getLogger( "org.teleal.cling" ).setLevel( Level.OFF );
        Logger logger = Logger.getLogger( "org.teleal.cling" );
        logger.setLevel( Level.INFO );

        // bindService( new Intent( this, WireUpnpService.class ), serviceConnection, Context.BIND_AUTO_CREATE );
    }

}
