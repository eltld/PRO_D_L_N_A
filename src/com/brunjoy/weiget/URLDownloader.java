package com.brunjoy.weiget;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import android.os.Environment;

import com.brunjoy.duanluo.imgdowner.MyLog;

public class URLDownloader {
    private String urlStr, fileName;

    public URLDownloader(String url, String fileName) {
        this.urlStr = url;
        this.fileName = fileName;
    }

    public void start() {

        try {
            BufferedInputStream in;
            FileOutputStream fos;
            
            File file = new File( Environment.getExternalStorageDirectory( ).getPath( ) + "/download/Video/" + fileName );
            if (file.exists( )) {
                return;
//                file.delete( );
            } else {
                if (!file.getParentFile( ).exists( )) {
                    file.getParentFile( ).mkdirs( );
                }
            }
            file.createNewFile( );
            URL url = new URL( urlStr );
            URLConnection urlConnection = url.openConnection( );
            urlConnection.addRequestProperty( "Content-type", "text/html" );
            urlConnection.addRequestProperty( "User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows 2000)" );
            urlConnection.setAllowUserInteraction( true );
            
            in = new BufferedInputStream( urlConnection.getInputStream( ) );
            
            fos = new FileOutputStream( file );
            byte[] buffer = new byte[8 * 1024];
            int flag = -1;
            MyLog.d( "urlDowner", "==========start=============="+file.getName( ) );
            while ((flag = in.read( buffer )) != -1) {
                fos.write( buffer, 0, flag );
                fos.flush( );
                MyLog.d( "urlDowner", "---");
            }
            MyLog.d( "urlDowner", "==========end=============="+file.getPath() );
            fos.close( );
            in.close( );

        } catch (MalformedURLException e) {

            e.printStackTrace( );
        } catch (IOException e) {

            e.printStackTrace( );
        }

    }
}
