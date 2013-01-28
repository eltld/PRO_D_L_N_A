package com.brunjoy.manager;

import org.teleal.cling.model.ValidationException;

import android.content.Context;

import com.brunjoy.duanluo.imgdowner.MyLog;
import com.wireme.mediaserver.MediaServer;

public class ServiceManager {
    public enum STATUS {
        REFUSE, ACCESS
    };

    private MediaServer mMediaServer;
    private static ServiceManager mServiceManager;

    private ServiceManager(Context mContext) {
        try {
            mMediaServer = new MediaServer( mContext );
        } catch (ValidationException e) {
            e.printStackTrace( );
            MyLog.d( "ServiceManager", "ValidationException=" +e);
        }
    }

    public MediaServer getMediaServer() {
        return mMediaServer;
    }

    public static ServiceManager getInstance(Context mContext) {
        if (mServiceManager == null) {
            mServiceManager = new ServiceManager( mContext );
        }
        return mServiceManager;
    }

    /**
     * 设置为拒绝访问
     */
    public void setStatus(STATUS status)
    {
        mMediaServer.setStaus(status);
    }

    public void onDestory() {
        mMediaServer.stop( );
        mMediaServer = null;
        mServiceManager=null;

    }

}
