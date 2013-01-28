package com.brunjoy.weiget;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;

import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;

import com.brunjoy.duanluo.imgdowner.BitmapUtil;
import com.brunjoy.duanluo.imgdowner.MyLog;

public class MyAsynDownFileTask extends AsyncTask<String, Integer, String> {
    String TAG = "MyAsynDownFileTask";

    public interface ProgressInterface {
        void success(String msg);

        void failure(String failure);

        void progresss(int progress);
    }

    private ProgressInterface progressInterface;

    public MyAsynDownFileTask(ProgressInterface progressInterface) {
        this.progressInterface = progressInterface;

    }

    private String fileName;

    public MyAsynDownFileTask(String fileName, ProgressInterface progressInterface) {
        this.progressInterface = progressInterface;
        this.fileName = fileName;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {

        super.onProgressUpdate( values );
        if (progressInterface == null) {
            return;
        }
        progressInterface.progresss( values[0] );
    }

    @Override
    protected String doInBackground(String... params) {
        String url = params[0];
        if (url == null)
            return null;

        HttpGet httpRequest = new HttpGet( url );
        AndroidHttpClient httpclient = AndroidHttpClient.newInstance( "Android" );
        // httpRequest.setHeader( "Accept-Encoding", "gzip" );
//        httpRequest.setHeader( "Content-type", "text/html" );
//        httpRequest.setHeader( "User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows 2000)" );
        try {
            synchronized (httpclient) {
                HttpResponse httpResponse = httpclient.execute( httpRequest );
                // 请求成功

                MyLog.I( TAG, "user get getDataByGet " );
                if (httpResponse.getStatusLine( ).getStatusCode( ) == HttpStatus.SC_OK) {
                    HttpEntity entity = httpResponse.getEntity( );
                    if (entity != null) {
                        long lg = entity.getContentLength( );
                        lg = lg > 0 ? lg : 3400000;
                        publishProgress( 0 );
                        InputStream is = entity.getContent( );
                        // GZIPInputStream gis = new GZIPInputStream(is);
                        BufferedInputStream bis = new BufferedInputStream( is );

                        File file = new File( BitmapUtil.rootSaveFilePath + "/" + fileName == null ? url.substring( url.lastIndexOf( '/' ) ) : fileName );
                        File fp = file.getParentFile( );
                        if (!fp.exists( )) {
                            fp.mkdirs( );
                        }
                        BufferedOutputStream bos = new BufferedOutputStream( new FileOutputStream( file ) );
                        int temp = -1;
                        long currentLog = 0;
                        byte[] buffer = new byte[8 * 1024];
                        MyLog.d( TAG, "down****************" );

                        while ((temp = bis.read( buffer )) != -1) {
                            bos.write( buffer, 0, temp );
                            bos.flush( );
                            currentLog += temp;
                            int progress = (int) (((currentLog * 1.0) / lg) * 100f);
                            MyLog.e( TAG, "down ing  **************** currentLog=" + currentLog + "   lg=" + lg + "  progress" );
                            // MyLog.d(TAG,
                            // "down ing  **************** progress="+progress);
                            publishProgress( progress );
                        }
                        MyLog.d( TAG, "down  complete ****************" );
                        bos.close( );
                        bis.close( );
                        is.close( );
                        return file.getPath( );
                    }

                } else {
                    MyLog.I( TAG, "  " + httpResponse.getStatusLine( ).getStatusCode( ) );
                }
            }
        } catch (ClientProtocolException e) {
            MyLog.I( TAG, "ClientProtocolException" + e.getMessage( ) );
        } catch (IOException e) {
            MyLog.I( TAG, "IOException" + e.getMessage( ) );
        } catch (Exception e) {
            MyLog.I( TAG, "Exception" + e.getMessage( ) );
        } finally {
            httpclient.close( );
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        if (progressInterface == null) {
            return;
        }
        if (result != null) {
            progressInterface.success( result );
        } else {
            progressInterface.failure( "" );
        }
    }

}
