package com.wireme.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.params.ConnManagerPNames;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.scheme.SocketFactory;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;

import com.brunjoy.duanluo.imgdowner.MyLog;

public class MyDataManager {
    String TAG = "DataManager";
    private static MyDataManager mDataManager = null;
    private Context mContext;
    public static String ROOTPATH = "http://192.168.1.19:8080/duanluo-api/";//"http://api.duanluo.com/";
    private final int MAXTASKNUM = 2;
    // private ConcurrentHashMap<String, DataCallBack> taskMap = new
    // ConcurrentHashMap<String, DataCallBack>();
    private ArrayList<TaskHolder> taskList = new ArrayList<TaskHolder>( );

    private MyDataManager(Context mContext) {
        this.mContext = mContext;
    }

    public void getApkList(MyCallBacke mMyCallBacke) {
        addTask( "system/getAPKs", mMyCallBacke );
    }

   
    public static synchronized MyDataManager getInstance(Context mContext) {
        if (mDataManager == null) {
            mDataManager = new MyDataManager( mContext.getApplicationContext( ) );
        }
        return mDataManager;
    }

    public interface MyCallBacke {
        public void resultMsg(String resultMSG);

        public void failureMsg(String msg);

    }

    public void addTask(String url, MultipartEntity params, MyCallBacke mDataCallBack) {
        if (url == null) {
            return;
        }
        TaskHolder holder = new TaskHolder( url, mDataCallBack, params );
        MyLog.E( TAG, "任务列表里面有该任务，移除并添加最近任务" + taskList.size( ) );
        if (taskList.remove( holder )) {
            MyLog.E( TAG, "任务列表里面有该任务，移除并添加最近任务" );
        }
        if (isRunning && runningTaskNum > 3) {
            taskList.add( 0, holder );
            MyLog.e( TAG, "正在运行,添加到任务列表中 url=" + (ROOTPATH + url) );
        } else {
            new DataAsyncTask( holder.url, holder.mDataCallBack ).execute( holder.params );
            MyLog.e( TAG, "没有运行,现在开始执行  url=" + (ROOTPATH + url) );
        }
        checkTask( );
    }

    private void checkTask() {
        synchronized (taskList) {
            for (int i = taskList.size( ) - 1; i >= MAXTASKNUM; i--) {
                taskList.remove( i );
            }
        }
    }

    public void addTask(String url, MyCallBacke mDataCallBack) {
        addTask( url, null, mDataCallBack );
        MyLog.e( TAG, "removeTask  url=" + ROOTPATH + url );
    }

    public void removeTask(String key) {
        int lg = taskList.size( );
        for (int i = lg - 1; i >= 0; i--) {
            if (taskList.get( i ).url.equals( key )) {
                taskList.remove( i );
                MyLog.e( TAG, "removeTask  url=" + key );
            }
        }
        lg = taskList.size( );
        if (isRunning && runningTaskNum > 3) {
            MyLog.e( TAG, "removeTask  还在运行返回，url=" + key );
            return;
        } else if (lg > 0) {
            TaskHolder holder = taskList.get( 0 );
            new DataAsyncTask( holder.url, holder.mDataCallBack ).execute( holder.params );
            MyLog.e( TAG, "removeTask 没有任务开始执行 url=" + key );
        }
    }

    private boolean isRunning = false;
    private volatile DefaultHttpClient httpClient;

    private DefaultHttpClient getHttpClient() {
        if (httpClient == null) {
            synchronized (this) {
                if (httpClient == null) {
                    SchemeRegistry supportedSchemes = new SchemeRegistry( );
                    SocketFactory sf = PlainSocketFactory.getSocketFactory( );
                    supportedSchemes.register( new Scheme( "http", sf, 80 ) );
                    HttpParams params = new BasicHttpParams( );
                    HttpProtocolParams.setVersion( params, HttpVersion.HTTP_1_1 );
                    HttpProtocolParams.setUseExpectContinue( params, false );

                    HttpClientParams.setRedirecting( params, false );
                    params.setParameter( ConnManagerPNames.MAX_CONNECTIONS_PER_ROUTE, new ConnPerRouteBean( 20 ) );
                    ConnManagerParams.setTimeout( params, 10000 );
                    params.setParameter( ConnManagerPNames.MAX_TOTAL_CONNECTIONS, 150 );

                    MyClientConnectionManager clientConnectionManager = new MyClientConnectionManager( params, supportedSchemes );
                    httpClient = new DefaultHttpClient( clientConnectionManager, params );

                }
            }
        }
        return httpClient;
    }

    class MyClientConnectionManager extends ThreadSafeClientConnManager {

        public MyClientConnectionManager(HttpParams params, SchemeRegistry schreg) {
            super( params, schreg );

        }

        @Override
        public void shutdown() {
            closeExpiredConnections( );
            closeIdleConnections( 0, TimeUnit.MILLISECONDS );
            synchronized (this) {
                if (httpClient == null)
                    return;
                httpClient.clearRequestInterceptors( );
                httpClient.clearResponseInterceptors( );
                httpClient = null;
            }
            MyLog.e( TAG, "******************************shutdown*********  connect shutdown" );
            super.shutdown( );
        }

    }

    private int runningTaskNum = 0;

    class DataAsyncTask extends AsyncTask<MultipartEntity, Integer, DataMessage> {
        private String url;
        private MyCallBacke mDataCallBack;

        public DataAsyncTask(String url, MyCallBacke mDataCallBack) {
            this.url = url;
            this.mDataCallBack = mDataCallBack;
        }

        @Override
        protected void onPreExecute() {
            synchronized (MyDataManager.this) {
                isRunning = true;
            }
            MyLog.i( TAG, "onPreExecute" );
            runningTaskNum++;
            super.onPreExecute( );
        }

        @Override
        protected DataMessage doInBackground(MultipartEntity... params) {
            if (params.length <= 0 || params[0] == null) {
                return getData( ROOTPATH + url, getHttpClient( ) );
            } else {
                return postData( ROOTPATH + url, params[0], getHttpClient( ) );
            }
        }

        @Override
        protected void onCancelled() {
            synchronized (MyDataManager.this) {
                isRunning = false;
            }
            super.onCancelled( );
            if (mDataCallBack != null) {
                mDataCallBack.failureMsg( "" );
            }
            removeTask( url );
            MyLog.i( TAG, "onCancelled" );
            runningTaskNum--;
        }

        @Override
        protected void onPostExecute(DataMessage result) {
            MyLog.d( TAG, result.getStatus( ) + result.getMsg( ) );

            super.onPostExecute( result );
            synchronized (MyDataManager.this) {
                isRunning = false;
            }
            if (mDataCallBack == null) {
                return;
            }
            if (result.getStatus( ) == MessageType.SUCCESS) {
                mDataCallBack.resultMsg( result.getMsg( ) );
            } else {
                mDataCallBack.failureMsg( result.getMsg( ) );
            }
            removeTask( url );
            runningTaskNum--;
            manualShutDown( );
        }

        @Override
        protected void onProgressUpdate(Integer... values) {

            super.onProgressUpdate( values );
            // if (mDataCallBack != null) {
            // mDataCallBack.progress(values[0]);
            // }
        }

    }

    private void manualShutDown() {
        if (httpClient != null) {
            httpClient.getConnectionManager( ).shutdown( );
        }
        httpClient = null;
        MyLog.e( TAG, "用户如果60s  没有使用，手动  shutDown" );
    }

    public DataMessage postSubejctData(String urlStr, Object obj) {
        DataMessage msg = new DataMessage( );
        long time = System.currentTimeMillis( );
        try {
            URL url = new URL( urlStr );
            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection( );
            urlConn.setDoOutput( true );
            urlConn.setDoInput( true );
            urlConn.setUseCaches( false );
            urlConn.setRequestProperty( "Content-type", "application/x-java-serialized-object" );
            urlConn.setRequestProperty( "Accept-Encoding", "gzip,deflate" );
            urlConn.setRequestMethod( "POST" );
            urlConn.connect( );
            OutputStream outStrm = urlConn.getOutputStream( );
            ObjectOutputStream oos = new ObjectOutputStream( outStrm );
            oos.writeObject( obj );
            oos.flush( );
            oos.close( );

            StringBuilder sb = new StringBuilder( );
            GZIPInputStream gis = new GZIPInputStream( urlConn.getInputStream( ) );
            InputStreamReader isr = new InputStreamReader( gis );
            BufferedReader br = new BufferedReader( isr );
            String temp = null;
            while ((temp = br.readLine( )) != null) {
                sb.append( temp );
            }
            br.close( );
            isr.close( );
            gis.close( );
            msg.setMsg( sb.toString( ) );
            msg.setStatus( MessageType.SUCCESS );
            MyLog.d( TAG, "传递对象使用时间  =" + (System.currentTimeMillis( ) - time) );
        } catch (IOException e) {
            e.printStackTrace( );
            msg.setMsg( e.getMessage( ) );
            msg.setStatus( MessageType.FAILURE );
        }
        return msg;
    }

    public DataMessage postData(String url, MultipartEntity mpEntity, HttpClient httpclient) {
        long time = System.currentTimeMillis( );
        DataMessage msg = new DataMessage( );
        StringBuilder sb = new StringBuilder( );
        HttpPost httppost = new HttpPost( url );
        httppost.setHeader( "Accept-Encoding", "gzip" );
        httppost.setEntity( mpEntity );
        HttpResponse response = null;
        try {
            response = httpclient.execute( httppost );
            HttpEntity resEntity = response.getEntity( );
            if (resEntity != null) {
                if (response.getStatusLine( ).getStatusCode( ) == HttpStatus.SC_OK) {
                    GZIPInputStream gis = new GZIPInputStream( resEntity.getContent( ) );
                    InputStreamReader isr = new InputStreamReader( gis );
                    BufferedReader br = new BufferedReader( isr );
                    String temp = null;
                    while ((temp = br.readLine( )) != null) {
                        sb.append( temp );
                    }
                    br.close( );
                    isr.close( );
                    gis.close( );
                    resEntity.consumeContent( );
                    msg.setMsg( sb.toString( ) );
                    msg.setStatus( MessageType.SUCCESS );
                } else {
                    msg.setMsg( "HttpStatus Err code=" + response.getStatusLine( ).getStatusCode( ) );
                    msg.setStatus( MessageType.FAILURE );
                }
            } else {
                msg.setMsg( "HttpEntity=null" );
                msg.setStatus( MessageType.FAILURE );
            }

        } catch (ClientProtocolException e) {
            MyLog.d( TAG, "UnsupportedEncodingException:" + e.getMessage( ) );
            msg.setMsg( e.getMessage( ) );
            msg.setStatus( MessageType.FAILURE );
        } catch (IOException e) {
            MyLog.d( TAG, "IOExceptionIOExceptionIOExcepti:" + e.getMessage( ) );
            msg.setMsg( e.getMessage( ) );
            msg.setStatus( MessageType.FAILURE );
        } catch (Exception e) {
            msg.setMsg( e.getMessage( ) );
            msg.setStatus( MessageType.FAILURE );
            MyLog.d( TAG, "Exception:" + e.getMessage( ) );
        } finally {
            httppost.abort( );
            httpclient.getConnectionManager( ).shutdown( );
            MyLog.d( TAG, "post 耗时=" + (System.currentTimeMillis( ) - time) + "\n  data=" + msg.getMsg( ) );
        }
        return msg;
    }

    public DataMessage getData(String url, HttpClient httpclient) {
        long time = System.currentTimeMillis( );
        DataMessage msg = new DataMessage( );
        StringBuilder sb = new StringBuilder( );
        HttpGet httpGet = new HttpGet( url );
        httpGet.setHeader( "Accept-Encoding", "gzip" );
        // DEBUG
        HttpResponse response = null;
        try {
            response = httpclient.execute( httpGet );
            HttpEntity resEntity = response.getEntity( );
            if (resEntity != null) {
                if (response.getStatusLine( ).getStatusCode( ) == HttpStatus.SC_OK) {
                    GZIPInputStream gis = new GZIPInputStream( resEntity.getContent( ) );
                    InputStreamReader isr = new InputStreamReader( gis );
                    BufferedReader br = new BufferedReader( isr );
                    String temp = null;
                    while ((temp = br.readLine( )) != null) {
                        sb.append( temp );
                    }
                    br.close( );
                    isr.close( );
                    gis.close( );

                    resEntity.consumeContent( );
                    msg.setMsg( sb.toString( ) );
                    msg.setStatus( MessageType.SUCCESS );
                } else {
                    msg.setMsg( "HttpStatus Err code=" + response.getStatusLine( ).getStatusCode( ) );
                    msg.setStatus( MessageType.FAILURE );
                }
            } else {
                msg.setMsg( "HttpEntity=null" );
                msg.setStatus( MessageType.FAILURE );
            }

        } catch (ClientProtocolException e) {
            MyLog.d( TAG, "UnsupportedEncodingException:" + e.getMessage( ) );
            msg.setMsg( e.getMessage( ) );
            msg.setStatus( MessageType.FAILURE );
        } catch (IOException e) {
            MyLog.d( TAG, "IOExceptionIOExceptionIOExcepti:" + e.getMessage( ) );
            msg.setMsg( e.getMessage( ) );
            msg.setStatus( MessageType.FAILURE );
        } catch (Exception e) {
            msg.setMsg( e.getMessage( ) );
            msg.setStatus( MessageType.FAILURE );
            MyLog.d( TAG, "Exception:" + e.getMessage( ) );
        } finally {
            httpGet.abort( );
            // httpclient.getConnectionManager().shutdown();
            MyLog.d( TAG, "get 耗时=" + (System.currentTimeMillis( ) - time) );
        }
        return msg;
    }

    public enum MessageType {
        SUCCESS, FAILURE, NG, UNKOWN, EXCEPTION
    };

    public class TaskHolder {
        String url;
        MyCallBacke mDataCallBack;
        MultipartEntity params;

        public TaskHolder(String url, MyCallBacke mDataCallBack, MultipartEntity params) {
            this.url = url;
            this.mDataCallBack = mDataCallBack;
            this.params = params;
        }
    }

    public void clear() {
        if (httpClient != null) {
            httpClient.getConnectionManager( ).shutdown( );
        }
        runningTaskNum = 0;
        if (taskList != null) {
            taskList.clear( );
            // taskList = null;
        }
        mDataManager = null;
        httpClient = null;

    }

    // public void addMyTask(String msg) {
    // MultipartEntity m=new MultipartEntity();
    // try {
    // m.addPart("msg", new StringBody(msg, Charset.forName("utf-8")));
    // MyLog.e("hai","------http://192.168.1.12:8080/rose-example/insertFeed-----------"
    // );
    // DataMessage
    // s=postData("http://192.168.1.12:8080/rose-example/insertFeed", m,
    // getHttpClient());
    // MyLog.e("hai",s.getMsg() );
    // MyLog.e("hai","上传成功" );
    // MyLog.e("hai","------------------" );
    // } catch (UnsupportedEncodingException e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // }
    //
    //
    //
    // }

}
