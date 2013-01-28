package com.brunjoy.duanluo.imgdowner;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URI;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.widget.ImageView;

/**
 * 下载image 并缓存到本地文件
 * 
 * @author Brunjoy
 * 
 */
public class ImageDownloader {
    private static int MAXDOWNNUM = 2;
    private static int MAXCANCLE = 50;
    /**
     * 如果想保持原图请在url后面加上该标示
     */
    public static final String SOURCE_PIC = "?fromByAndroidDLNA";
    private static ImageDownloader mImageDownloader;

    public synchronized static ImageDownloader getInstacne() {
        if (mImageDownloader == null) {
            mImageDownloader = new ImageDownloader( );
        }
        return mImageDownloader;
    }

    // private static final String LOG_TAG = "ImageDownloader";

    // private static final int HARD_CACHE_CAPACITY = 16;
    // private static final int DELAY_BEFORE_PURGE = 1 * 60 * 1000; // in
    // milliseconds

    // Soft cache for bitmaps kicked out of hard cache
    // private final static ConcurrentHashMap<String, SoftReference<Bitmap>> sSoftBitmapCache = new ConcurrentHashMap<String, SoftReference<Bitmap>>( HARD_CACHE_CAPACITY / 2 );
    // private final static ArrayList<BitmapDownloaderTask> currentTasks = new ArrayList<ImageDownloader.BitmapDownloaderTask>( );
    /*
     * Cache-related fields and methods.
     * 
     * We use a hard and a soft cache. A soft reference cache is too aggressively cleared by the Garbage Collector.
     */

    // Hard cache, with a fixed maximum capacity and a life duration
    // private final HashMap<String, Bitmap> sHardBitmapCache = new LinkedHashMap<String, Bitmap>( HARD_CACHE_CAPACITY / 2, 0.75f, true ) {
    // @Override
    // protected boolean removeEldestEntry(LinkedHashMap.Entry<String, Bitmap> eldest) {
    // if (size( ) > HARD_CACHE_CAPACITY) {
    // // Entries push-out of hard reference cache are transferred to
    // // soft reference cache
    // sSoftBitmapCache.put( eldest.getKey( ), new SoftReference<Bitmap>( eldest.getValue( ) ) );
    // return true;
    // } else
    // return false;
    // }
    //
    // };

    /**
     * Download the specified image from the Internet and binds it to the provided ImageView. The binding is immediate if the image is found in the cache and will be done
     * asynchronously otherwise. A null bitmap will be associated to the ImageView if an error occurs.
     * 
     * @param url
     *            The URL of the image to download.
     * @param imageView
     *            The ImageView to bind the downloaded image to.
     */
    public synchronized void download(String url, ImageView imageView) {
        // resetPurgeTimer( );
        MyLog.d( "download", "url=" + url );
        if (url == null || !url.toLowerCase( ).startsWith( "http://" )) {
            return;
        }
        Bitmap bitmap = BitmapUtil.getBitmap( url );// getBitmapFromCache( url );
        if (bitmap == null) {
            forceDownload( url, imageView );
        } else {
            cancelPotentialDownload( url, imageView );
            imageView.setImageBitmap( bitmap );
        }
    }

    public synchronized void download(String url, ImageView imageView, Bitmap defbitmap) {
        // resetPurgeTimer( );
        Bitmap bitmap = BitmapUtil.getBitmap( url );// getBitmapFromCache( url );
        if (bitmap == null) {
            // imageView.setImageResource(R.drawable.tv);
            forceDownload( url, imageView, defbitmap );
        } else {
            cancelPotentialDownload( url, imageView );
            imageView.setImageBitmap( bitmap );
        }
    }

    public synchronized void download(String url, ImageDownCallBack mImageDownCallBack) {
        // resetPurgeTimer( );
        if (mImageDownCallBack == null)
            return;
        Bitmap bitmap = BitmapUtil.getBitmap( url ); // getBitmapFromCache( url );

        if (bitmap == null) {
            forceDownload( url, mImageDownCallBack );
        } else {
            mImageDownCallBack.downCompleted( bitmap );
        }

    }

    public interface ImageDownCallBack {
        void downCompleted(Bitmap bitmap);
    }

    public interface ImageDownCompleted {
        void completed(String url);
    }

    /*
     * Same as download but the image is always downloaded and the cache is not used. Kept private at the moment as its interest is not clear. private void forceDownload(String
     * url, ImageView view) { forceDownload(url, view, null); }
     */

    /**
     * Same as download but the image is always downloaded and the cache is not used. Kept private at the moment as its interest is not clear.
     */
    private void forceDownload(String url, ImageView imageView) {
        if (url == null) {
            return;
        }
        if (cancelPotentialDownload( url, imageView )) {
            try {
                BitmapDownloaderTask task = new BitmapDownloaderTask( imageView, url );
                DownloadedDrawable downloadedDrawable = new DownloadedDrawable( task, imageView.getDrawable( ) );
                imageView.setImageDrawable( downloadedDrawable );
                addNewTask( task );
            } catch (Exception e) {
            }

            // task.execute( url );
        }
    }

    private static int AccessNum = 0;

    private void addNewTask(BitmapDownloaderTask task) {
        synchronized (cacheDownLoaders) {
            MyLog.d( "down", "addNewTask  AccessNum=" + AccessNum + "   cacheDownLoaders=" + cacheDownLoaders.size( ) );
            if (AccessNum < MAXDOWNNUM) {
                AccessNum++;
                task.execute( );
            } else {
                BitmapDownloaderTask temp = null;
                for (BitmapDownloaderTask t : cacheDownLoaders) {
                    if (t.getUrl( ).equals( task.getUrl( ) )) {
                        temp = t;
                        MyLog.d( "down", "addNewTask ====================== removed=" + t.getUrl( ) );
                        break;
                    }
                }
                if (temp != null)
                    cacheDownLoaders.remove( temp );
                cacheDownLoaders.add( task );
                if (cacheDownLoaders.size( ) > MAXCANCLE) {
                    cacheDownLoaders.remove( 0 );
                }
            }
        }
    }

    public void startNewStart() {
        synchronized (cacheDownLoaders) {
            AccessNum--;
            MyLog.d( "down", "startNewStart  AccessNum=" + AccessNum + "   cacheDownLoaders=" + cacheDownLoaders.size( ) );
            if (cacheDownLoaders.size( ) > 0) {
                AccessNum++;
                cacheDownLoaders.remove( cacheDownLoaders.size( ) - 1 ).execute( );
            }
        }
    }

    public void cancelTasks() {
        AccessNum = 0;
        cacheDownLoaders.clear( );
    }

    private static ArrayList<BitmapDownloaderTask> cacheDownLoaders = new ArrayList<ImageDownloader.BitmapDownloaderTask>( );

    private void forceDownload(String url, ImageView imageView, Bitmap bitmap) {
        if (url == null) {
            return;
        }
        if (cancelPotentialDownload( url, imageView )) {
            BitmapDownloaderTask task = new BitmapDownloaderTask( imageView, url );
            DownloadedDrawable downloadedDrawable = new DownloadedDrawable( task, bitmap );
            imageView.setImageDrawable( downloadedDrawable );
            // task.execute( url );
            addNewTask( task );
        }
    }

    private void forceDownload(String url, ImageDownCallBack mImageDownCallBack) {

        if (url == null) {
            mImageDownCallBack.downCompleted( null );
            return;
        }

        if (cancelPotentialDownload( url, null )) {

            BitmapDownloaderTask task = new BitmapDownloaderTask( mImageDownCallBack, url );
            task.execute( );

        }
    }

    /**
     * Returns true if the current download has been canceled or if there was no download in progress on this image view. Returns false if the download in progress deals with the
     * same url. The download is not stopped in that case.
     */
    private static boolean cancelPotentialDownload(String url, ImageView imageView) {
        BitmapDownloaderTask bitmapDownloaderTask = getBitmapDownloaderTask( imageView );

        if (bitmapDownloaderTask != null) {
            String bitmapUrl = bitmapDownloaderTask.url;
            if ((bitmapUrl == null) || (!bitmapUrl.equals( url ))) {
                bitmapDownloaderTask.cancel( true );
            } else {
                // The same URL is already being downloaded.
                return false;
            }
        }
        return true;
    }

    /**
     * @param imageView
     *            Any imageView
     * @return Retrieve the currently active download task (if any) associated with this imageView. null if there is no such task.
     */
    private static BitmapDownloaderTask getBitmapDownloaderTask(ImageView imageView) {
        if (imageView != null) {
            Drawable drawable = imageView.getDrawable( );
            if (drawable instanceof DownloadedDrawable) {
                DownloadedDrawable downloadedDrawable = (DownloadedDrawable) drawable;
                return downloadedDrawable.getBitmapDownloaderTask( );
            }
        }
        return null;
    }

    // private HttpClient client;
    // long start;

    private Bitmap downloadBitmap(String url) {
        // final int IO_BUFFER_SIZE = 4 * 1024;
        MyLog.d( "down", "downloadBitmap start down=" + url );
        // AndroidHttpClient is not allowed to be used from the main thread
        final HttpClient client = AndroidHttpClient.newInstance( "Android" );
        // start = System.currentTimeMillis( );
        final HttpGet getRequest = new HttpGet( url );
        try {
            HttpResponse response = client.execute( getRequest );
            final int statusCode = response.getStatusLine( ).getStatusCode( );
            if (statusCode != HttpStatus.SC_OK) {
                return null;
            }
            final HttpEntity entity = response.getEntity( );
            if (entity != null) {
                InputStream inputStream = null;
                try {
                    inputStream = entity.getContent( );
                    return BitmapFactory.decodeStream( inputStream );
                } catch (OutOfMemoryError e) {

                } finally {
                    if (inputStream != null) {
                        inputStream.close( );
                    }
                    entity.consumeContent( );
                }
            }
        } catch (IOException e) {
            getRequest.abort( );
        } catch (IllegalStateException e) {
            getRequest.abort( );
        } catch (Exception e) {
            getRequest.abort( );
        } finally {
            if ((client instanceof AndroidHttpClient)) {
                ((AndroidHttpClient) client).close( );
            }
        }
        return null;
    }

    /*
     * An InputStream that skips the exact number of bytes provided, unless it reaches EOF.
     */
    static class FlushedInputStream extends FilterInputStream {
        public FlushedInputStream(InputStream inputStream) {
            super( inputStream );
        }

        // @Override
        // public long skip(long n) throws IOException {
        // long totalBytesSkipped = 0L;
        // while (totalBytesSkipped < n) {
        // long bytesSkipped = in.skip(n - totalBytesSkipped);
        // if (bytesSkipped == 0L) {
        // int b = read();
        // if (b < 0) {
        // break; // we reached EOF
        // } else {
        // bytesSkipped = 1; // we read one byte
        // }
        // }
        // totalBytesSkipped += bytesSkipped;
        // }
        // return totalBytesSkipped;
        // }
    }

    /**
     * The actual AsyncTask that will asynchronously download the image.
     */
    class BitmapDownloaderTask extends AsyncTask<String, Void, Bitmap> {
        private String url;
        private final WeakReference<ImageView> imageViewReference;
        private ImageDownCallBack mImageDownCallBack;

        public String getUrl() {
            return url;
        }

        public BitmapDownloaderTask(ImageView imageView, String url) {
            imageViewReference = new WeakReference<ImageView>( imageView );
            this.url = url;
        }

        public BitmapDownloaderTask(ImageDownCallBack mImageDownCallBack, String url) {
            imageViewReference = null;
            this.mImageDownCallBack = mImageDownCallBack;
            this.url = url;
        }

        /**
         * Actual download method.
         */
        @Override
        protected Bitmap doInBackground(String... params) {

            // url = params[0];
            Bitmap bitmap = downloadBitmap( url.endsWith( SOURCE_PIC ) ? url.substring( 0, url.lastIndexOf( '?' ) ) : url );
            if (bitmap != null && !url.endsWith( SOURCE_PIC )) {
                try {
                    bitmap = BitmapUtil.transform( new Matrix( ), bitmap, 126, 126, true );
                } catch (OutOfMemoryError e) {
                }
                BitmapUtil.savaBitmap( bitmap, url );
            }

            return bitmap;
        }

        @Override
        protected void onCancelled() {

            startNewStart( );
            super.onCancelled( );
        }

        /**
         * Once the image is downloaded, associates it to the imageView
         */
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (isCancelled( )) {
                bitmap = null;
            }

            // addBitmapToCache( url, bitmap );
            if (mImageDownCallBack != null && bitmap != null) {
                mImageDownCallBack.downCompleted( bitmap );
                mImageDownCallBack = null;

                startNewStart( );
                return;
            }
            if (imageViewReference != null) {
                if (bitmap == null) {

                    startNewStart( );
                    return;
                }
                ImageView imageView = imageViewReference.get( );
                BitmapDownloaderTask bitmapDownloaderTask = getBitmapDownloaderTask( imageView );
                if ((this == bitmapDownloaderTask)) {
                    imageView.setImageBitmap( bitmap );
                }
            }
            startNewStart( );
        }
    }

    /**
     * A fake Drawable that will be attached to the imageView while the download is in progress.
     * 
     * <p>
     * Contains a reference to the actual download task, so that a download task can be stopped if a new binding is required, and makes sure that only the last started download
     * process can bind its result, independently of the download finish order.
     * </p>
     */

    class DownloadedDrawable extends BitmapDrawable {
        private final WeakReference<BitmapDownloaderTask> bitmapDownloaderTaskReference;

        public DownloadedDrawable(BitmapDownloaderTask bitmapDownloaderTask) {
            // super(defaultBitmap);
            bitmapDownloaderTaskReference = new WeakReference<BitmapDownloaderTask>( bitmapDownloaderTask );
        }

        public DownloadedDrawable(BitmapDownloaderTask bitmapDownloaderTask, Bitmap bitmap) {

            super( bitmap );
            if (bitmap == null) {
                // MyLog.D(LOG_TAG, "bitmap==null");
            }
            bitmapDownloaderTaskReference = new WeakReference<BitmapDownloaderTask>( bitmapDownloaderTask );

        }

        public DownloadedDrawable(BitmapDownloaderTask bitmapDownloaderTask, Drawable drawable) {

            super( BitmapUtil.drawableToBitmap( drawable ) );
            // if (drawable == null) {
            // // MyLog.D(LOG_TAG, "bitmap==null");
            // }
            bitmapDownloaderTaskReference = new WeakReference<BitmapDownloaderTask>( bitmapDownloaderTask );

        }

        public BitmapDownloaderTask getBitmapDownloaderTask() {
            return bitmapDownloaderTaskReference.get( );
        }
    }

    public void download(URI albumArtURI, ImageView image) {
        if (albumArtURI == null)
            return;
        download( albumArtURI.toString( ), image );

    }

    // private final Handler purgeHandler = new Handler( );
    //
    // private final Runnable purger = new Runnable( ) {
    // public void run() {
    // clearCache( );
    // }
    // };

    /**
     * Adds this bitmap to the cache.
     * 
     * @param bitmap
     *            The newly downloaded bitmap.
     */
    // private void addBitmapToCache(String url, Bitmap bitmap) {
    // if (bitmap != null) {
    // synchronized (sHardBitmapCache) {
    // sHardBitmapCache.put( url, bitmap );
    //
    // }
    // }
    // }

    // /**
    // * @param url
    // * The URL of the image that will be retrieved from the cache.
    // * @return The cached bitmap or null if it was not found.
    // */
    // public Bitmap getBitmapFromCache(String url) {
    // // First try the hard reference cache
    // synchronized (sHardBitmapCache) {
    // final Bitmap bitmap = sHardBitmapCache.get( url );
    //
    // if (bitmap != null) {
    // // Bitmap found in hard cache
    // // Move element to first position, so that it is removed last
    // sHardBitmapCache.remove( url );
    // sHardBitmapCache.put( url, bitmap );
    // return bitmap;
    // } else {
    // Bitmap bt = BitmapUtil.getBitmap( url );
    // if (bt != null) {
    // sHardBitmapCache.put( url, bitmap );
    // return bt;
    // }
    // }
    // }
    //
    // if (sSoftBitmapCache == null || url == null)
    // return null;
    // //
    // // Then try the soft reference cache
    // SoftReference<Bitmap> bitmapReference = sSoftBitmapCache.get( url );
    // if (bitmapReference != null) {
    // final Bitmap bitmap = bitmapReference.get( );
    // if (bitmap != null) {
    // // Bitmap found in soft cache
    // return bitmap;
    // } else {
    // // Soft reference has been Garbage Collected
    // sSoftBitmapCache.remove( url );
    // }
    // }
    //
    // return null;
    // }

    /**
     * Clears the image cache used internally to improve performance. Note that for memory efficiency reasons, the cache will automatically be cleared after a certain inactivity
     * delay.
     */
    // public void clearCache() {
    // sHardBitmapCache.clear( );
    // sSoftBitmapCache.clear( );
    // // sHardBitmapCache.clearAll();
    // // defaultBitmap = null;
    // }

    /**
     * Allow a new delay before the automatic cache clear is done.
     */
    // private void resetPurgeTimer() {
    // purgeHandler.removeCallbacks( purger );
    // purgeHandler.postDelayed( purger, DELAY_BEFORE_PURGE );
    // }
}
